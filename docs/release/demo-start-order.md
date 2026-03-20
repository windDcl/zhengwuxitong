# 答辩演示启动顺序（T29.5）

1. 启动 MySQL 并执行 `sql/init.sql`
2. 启动 NLP 服务（8000）
3. 启动后端服务（8080）
4. 启动前端服务（5173）
5. 运行 smoke：`scripts/test/smoke_api.sh`
