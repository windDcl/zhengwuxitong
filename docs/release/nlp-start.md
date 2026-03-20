# NLP 服务启动说明（T29.3）

```bash
cd nlp-service
pip3 install -r requirements.txt
uvicorn app.main:app --host 127.0.0.1 --port 8000
```

健康检查：
```bash
curl -s http://127.0.0.1:8000/health
```
