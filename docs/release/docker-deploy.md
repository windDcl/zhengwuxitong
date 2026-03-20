# Docker 部署说明

## 启动

在服务器上安装 Docker 和 Docker Compose 插件后，在仓库根目录执行：

```bash
docker compose up -d --build
```

服务端口：

- 前端：`80`
- 后端：`8080`
- NLP：`8000`
- MySQL：`3306`

访问地址：

- 前端首页：`http://你的服务器IP/`
- 后端健康检查：`http://你的服务器IP:8080/health`
- NLP 健康检查：`http://你的服务器IP:8000/health`

## 停止

```bash
docker compose down
```

如果希望连数据库数据一起删除：

```bash
docker compose down -v
```

## 常用命令

查看日志：

```bash
docker compose logs -f
```

单独查看某个服务：

```bash
docker compose logs -f frontend
docker compose logs -f backend
docker compose logs -f nlp
docker compose logs -f mysql
```

重新构建并启动：

```bash
docker compose up -d --build
```

## 说明

- 前端容器使用 Nginx 提供静态文件，并将 `/api/*` 代理到后端容器。
- 后端通过容器内主机名访问 MySQL 和 NLP：
  - MySQL：`mysql:3306`
  - NLP：`nlp:8000`
- 数据库初始化脚本会在首次创建 MySQL 数据卷时自动执行。
- 生产环境建议修改 `docker-compose.yml` 里的数据库密码，并按需收紧暴露端口。
