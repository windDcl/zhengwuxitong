# 基于语义匹配的政务智能问答系统

## 目录结构
- `frontend/` Vue 前端（前台 + 管理后台）
- `backend/` Spring Boot 后端
- `nlp-service/` Python NLP 服务
- `sql/` 数据库脚本
- `docs/` 需求、架构、测试、部署文档

## 环境要求
- JDK 17
- Maven 3.9+
- Python 3.11+
- Node.js 20+
- MySQL 8+

默认本地参数：
- MySQL: `127.0.0.1:3306`
- 用户名: `root`
- 密码: `12345678`
- 数据库: `gov_qa`

## 第一次部署
第一次部署建议直接执行一键脚本，它会自动做这些事：
- 检查数据库是否已初始化，未初始化时执行 `sql/init.sql`
- 安装 `frontend` 依赖
- 安装 `nlp-service` 依赖
- 编译 `backend`
- 启动 NLP、后端、前端三个服务
- 等待服务健康检查通过

执行命令：

```bash
bash scripts/release/start_all_local.sh
```

启动成功后访问：
- 前端：`http://127.0.0.1:5173/`
- 后端健康检查：`http://127.0.0.1:8080/health`
- NLP 健康检查：`http://127.0.0.1:8000/health`

日志文件：
- `/tmp/govqa_frontend.log`
- `/tmp/govqa_backend.log`
- `/tmp/govqa_nlp.log`

## 日常启动
如果环境和依赖已经装好，后续仍然可以直接执行同一个脚本：

```bash
bash scripts/release/start_all_local.sh
```

脚本会自动关闭旧端口上的服务并重新拉起。

## 停止服务
```bash
bash scripts/release/stop_all_local.sh
```

## 手动启动
如果不想用脚本，也可以按下面顺序手动启动：

1. 初始化数据库（首次）
```bash
mysql -h127.0.0.1 -P3306 -uroot -p12345678 < sql/init.sql
```

2. 启动 NLP 服务
```bash
cd nlp-service
pip3 install -r requirements.txt
uvicorn app.main:app --host 127.0.0.1 --port 8000
```

3. 启动后端
```bash
cd backend
mvn -q compile -Dmaven.repo.local=/tmp/m2
mvn -q org.springframework.boot:spring-boot-maven-plugin:3.3.5:run -Dmaven.repo.local=/tmp/m2
```

4. 启动前端
```bash
cd frontend
npm install
npm run dev -- --host 127.0.0.1 --port 5173
```

## 可交付说明
如果你要把这个项目交给别人跑，最简单的方式是直接把仓库和下面两个脚本一起给对方：
- `scripts/release/start_all_local.sh`
- `scripts/release/stop_all_local.sh`

对方只需要满足“环境要求”，然后执行：

```bash
bash scripts/release/start_all_local.sh
```

详细文档见：
- [docs/release/db-init.md](/Users/wind/apps/money_maker/zhengwuxitong/docs/release/db-init.md)
- [docs/release/backend-start.md](/Users/wind/apps/money_maker/zhengwuxitong/docs/release/backend-start.md)
- [docs/release/nlp-start.md](/Users/wind/apps/money_maker/zhengwuxitong/docs/release/nlp-start.md)
- [docs/release/frontend-start.md](/Users/wind/apps/money_maker/zhengwuxitong/docs/release/frontend-start.md)
