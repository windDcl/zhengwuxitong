# 系统架构图与说明（T03.1/T03.2/T03.3/T03.4）

## 总体架构

- `frontend/` Vue 前端：用户页 + 管理后台
- `backend/` Spring Boot：业务 API、权限、日志、统计
- `nlp-service/` Python FastAPI：向量化、匹配、索引
- `mysql`：业务数据

调用关系：

1. 前端调用后端 REST API
2. 后端在问答匹配与重建索引时调用 NLP 服务
3. 后端写入 MySQL，NLP 维护本地索引文件

## 模块边界

后端负责：
- 管理员认证与鉴权
- 分类/FAQ/公告/设置/日志/统计
- 问答流程编排（阈值判断、落日志）

NLP 服务负责：
- 文本向量化
- 相似度计算
- TopN 召回
- FAQ 向量索引重建与缓存

前端负责：
- 页面交互与展示
- 表单输入校验
- 调用 API 展示结果

## 服务接口边界（后端<->NLP）

- `GET /health`
- `POST /embed`：单文本向量化
- `POST /embed/batch`：批量向量化
- `POST /similarity`：计算余弦相似度
- `POST /reindex`：批量重建 FAQ 索引
- `POST /match`：输入问题返回 TopN 匹配结果

## 部署方式

开发：本地启动三服务（frontend/backend/nlp-service）+ MySQL

答辩：单机部署
- 前端静态资源
- Spring Boot jar
- Python NLP 服务
- MySQL
