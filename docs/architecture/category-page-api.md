# 分类页接口设计（T22.1）

接口：`GET /api/public/categories/{id}/faqs`

参数：
- `id`：分类 ID
- `keyword`（可选）：分类内搜索关键词

返回：
- FAQ 列表（仅启用）
- 字段：`id` `standardQuestion` `standardAnswer` `aliases`
