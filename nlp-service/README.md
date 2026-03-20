# NLP Service

## 启动
```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

## 接口
- `GET /health`
- `POST /embed`
- `POST /embed/batch`
- `POST /similarity`
- `POST /reindex`
- `POST /match`
