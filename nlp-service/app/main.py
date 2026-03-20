from __future__ import annotations

import hashlib
import json
from pathlib import Path
from typing import Any

import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel

try:
    from sentence_transformers import SentenceTransformer
except Exception:  # pragma: no cover
    SentenceTransformer = None


BASE_DIR = Path(__file__).resolve().parents[1]
INDEX_FILE = BASE_DIR / "data" / "faq_index.json"
MODEL_NAME = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"

app = FastAPI(title="gov-qa-nlp", version="0.1.0")


class EmbedRequest(BaseModel):
    text: str


class BatchEmbedRequest(BaseModel):
    texts: list[str]


class SimilarityRequest(BaseModel):
    vector_a: list[float]
    vector_b: list[float]


class ReindexFaqItem(BaseModel):
    faq_id: int
    standard_question: str
    texts: list[str]


class ReindexRequest(BaseModel):
    faqs: list[ReindexFaqItem]


class MatchRequest(BaseModel):
    question: str
    top_n: int = 3


class ModelHolder:
    model: Any = None
    index: list[dict[str, Any]] = []


def _hash_embed(text: str, dim: int = 384) -> np.ndarray:
    # Fallback embedding: hashed char n-gram vector.
    # Compared with random vectors, this preserves partial lexical similarity.
    t = text.strip()
    if not t:
        return np.zeros(dim, dtype=np.float64)
    grams = []
    chars = list(t)
    for n in (1, 2, 3):
        if len(chars) < n:
            continue
        for i in range(len(chars) - n + 1):
            grams.append("".join(chars[i : i + n]))
    v = np.zeros(dim, dtype=np.float64)
    for g in grams:
        h = hashlib.sha256(g.encode("utf-8")).digest()
        idx = int.from_bytes(h[:4], "big") % dim
        sign = 1.0 if (h[4] % 2 == 0) else -1.0
        v[idx] += sign
    norm = np.linalg.norm(v)
    if norm == 0:
        return v
    return v / norm


def _load_model() -> Any:
    if SentenceTransformer is None:
        return None
    try:
        return SentenceTransformer(MODEL_NAME, local_files_only=True)
    except Exception:
        return None


def _embed(text: str) -> np.ndarray:
    model = ModelHolder.model
    if model is None:
        return _hash_embed(text)
    vec = np.array(model.encode([text], normalize_embeddings=True)[0], dtype=np.float64)
    return vec


def _cosine(a: np.ndarray, b: np.ndarray) -> float:
    denom = np.linalg.norm(a) * np.linalg.norm(b)
    if denom == 0:
        return 0.0
    return float(np.dot(a, b) / denom)


def _save_index() -> None:
    INDEX_FILE.parent.mkdir(parents=True, exist_ok=True)
    payload = []
    for row in ModelHolder.index:
        payload.append({
            "faq_id": row["faq_id"],
            "standard_question": row["standard_question"],
            "text": row["text"],
            "vector": row["vector"].tolist(),
        })
    INDEX_FILE.write_text(json.dumps(payload, ensure_ascii=False), encoding="utf-8")


def _load_index() -> None:
    if not INDEX_FILE.exists():
        ModelHolder.index = []
        return
    payload = json.loads(INDEX_FILE.read_text(encoding="utf-8"))
    ModelHolder.index = [
        {
            "faq_id": p["faq_id"],
            "standard_question": p["standard_question"],
            "text": p["text"],
            "vector": np.array(p["vector"], dtype=np.float64),
        }
        for p in payload
    ]


@app.on_event("startup")
def startup() -> None:
    ModelHolder.model = _load_model()
    _load_index()


@app.get("/health")
def health() -> dict[str, Any]:
    return {
        "status": "ok",
        "model_loaded": ModelHolder.model is not None,
        "index_size": len(ModelHolder.index),
    }


@app.post("/embed")
def embed(req: EmbedRequest) -> dict[str, Any]:
    vec = _embed(req.text)
    return {"vector": vec.tolist(), "dimension": int(vec.shape[0])}


@app.post("/embed/batch")
def embed_batch(req: BatchEmbedRequest) -> dict[str, Any]:
    vectors = [_embed(t).tolist() for t in req.texts]
    return {"vectors": vectors, "count": len(vectors)}


@app.post("/similarity")
def similarity(req: SimilarityRequest) -> dict[str, Any]:
    a = np.array(req.vector_a, dtype=np.float64)
    b = np.array(req.vector_b, dtype=np.float64)
    return {"similarity": _cosine(a, b)}


@app.post("/reindex")
def reindex(req: ReindexRequest) -> dict[str, Any]:
    rows: list[dict[str, Any]] = []
    for faq in req.faqs:
        seen = set()
        texts = [faq.standard_question] + faq.texts
        for text in texts:
            t = text.strip()
            if not t or t in seen:
                continue
            seen.add(t)
            rows.append(
                {
                    "faq_id": faq.faq_id,
                    "standard_question": faq.standard_question,
                    "text": t,
                    "vector": _embed(t),
                }
            )
    ModelHolder.index = rows
    _save_index()
    return {"ok": True, "faq_count": len(req.faqs), "index_size": len(rows)}


@app.post("/match")
def match(req: MatchRequest) -> dict[str, Any]:
    if not ModelHolder.index:
        return {"results": []}
    qv = _embed(req.question)
    scored = []
    for row in ModelHolder.index:
        score = _cosine(qv, row["vector"])
        scored.append(
            {
                "faq_id": row["faq_id"],
                "matched_question": row["text"],
                "standard_question": row["standard_question"],
                "score": score,
            }
        )

    # 以 FAQ 聚合，每个 FAQ 保留最高分。
    best: dict[int, dict[str, Any]] = {}
    for s in scored:
        fid = int(s["faq_id"])
        if fid not in best or s["score"] > best[fid]["score"]:
            best[fid] = s

    results = sorted(best.values(), key=lambda x: x["score"], reverse=True)[: req.top_n]
    return {"results": results}
