# 搜索联想规则（T20.1）

- 数据来源：`faq.standard_question` + `faq_alias.alias_question`
- 过滤条件：仅 `faq.status=1`（启用）
- 匹配方式：包含匹配（contains）
- 数量限制：默认 8 条，可通过 `limit` 参数调整
- 去重策略：按文本去重后返回
