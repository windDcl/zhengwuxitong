#!/usr/bin/env python3
import csv
import json
import statistics
import sys
import urllib.request


def http_post(url: str, payload: dict):
    data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json"}, method="POST")
    with urllib.request.urlopen(req, timeout=10) as resp:
        return json.loads(resp.read().decode("utf-8"))


def http_put(url: str):
    req = urllib.request.Request(url, data=b"", method="PUT")
    with urllib.request.urlopen(req, timeout=10) as resp:
        return json.loads(resp.read().decode("utf-8"))


def http_login(base: str, username: str, password: str) -> str:
    data = http_post(base + "/api/admin/login", {"username": username, "password": password})
    return data["data"]["token"]

def http_get(url: str, headers=None):
    req = urllib.request.Request(url, method="GET", headers=headers or {})
    with urllib.request.urlopen(req, timeout=10) as resp:
        return json.loads(resp.read().decode("utf-8"))


def ask(base: str, q: str):
    return http_post(base + "/api/public/ask", {"question": q, "topN": 3})["data"]


def update_threshold(base: str, token: str, value: float):
    req = urllib.request.Request(
        base + f"/api/admin/settings/threshold?value={value}",
        method="PUT",
        headers={"Authorization": token},
        data=b"",
    )
    with urllib.request.urlopen(req, timeout=10) as resp:
        return json.loads(resp.read().decode("utf-8"))


def evaluate(base: str, dataset_csv: str, threshold: float):
    token = http_login(base, "admin", "Admin@123")
    update_threshold(base, token, threshold)
    faq_resp = http_get(base + "/api/admin/faqs", headers={"Authorization": token})
    faq_map = {int(f["id"]): f["standardQuestion"] for f in faq_resp.get("data", [])}

    rows = []
    with open(dataset_csv, "r", encoding="utf-8-sig", newline="") as f:
        for r in csv.DictReader(f):
            rows.append(r)

    total = len(rows)
    top1 = 0
    top3 = 0
    scores = []
    details = []

    for r in rows:
        expected = r["expected_standard_question"].strip()
        resp = ask(base, r["query"])
        cands = resp.get("candidates", [])
        cand_std = []
        for c in cands:
            fid = c.get("faqId")
            if fid is not None and int(fid) in faq_map:
                cand_std.append(faq_map[int(fid)])
            else:
                cand_std.append(c.get("matchedQuestion"))
        if cands:
            scores.append(float(cands[0].get("score", 0)))
        if len(cand_std) > 0 and cand_std[0] == expected:
            top1 += 1
        if expected in cand_std[:3]:
            top3 += 1
        details.append({"query": r["query"], "expected": expected, "candidates": cand_std[:3], "hit": resp.get("hit")})

    return {
        "threshold": threshold,
        "total": total,
        "top1_accuracy": top1 / total if total else 0,
        "top3_recall": top3 / total if total else 0,
        "avg_top1_score": statistics.mean(scores) if scores else 0,
        "details": details,
    }


def main():
    if len(sys.argv) != 4:
        print("Usage: run_eval.py <base_url> <dataset_csv> <out_json>")
        sys.exit(2)
    base, ds, out = sys.argv[1], sys.argv[2], sys.argv[3]

    results = []
    for t in [0.3, 0.5, 0.75, 0.9]:
        results.append(evaluate(base, ds, t))

    best = max(results, key=lambda x: (x["top1_accuracy"], x["top3_recall"]))
    payload = {"results": results, "best": best}
    with open(out, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False, indent=2)
    print(f"saved={out}")
    print(f"best_threshold={best['threshold']}")


if __name__ == "__main__":
    main()
