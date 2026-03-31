# NLP Service

## 启动
```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

或直接使用项目脚本：

```bash
bash ../scripts/release/start_nlp_local.sh
```

## 接口
- `GET /health`
- `POST /embed`
- `POST /embed/batch`
- `POST /similarity`
- `POST /reindex`
- `POST /match`
