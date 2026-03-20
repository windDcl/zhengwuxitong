# 热门问题统计口径（T23.1）

- 数据源：`qa_log`
- 统计范围：`is_hit = 1` 且 `matched_question` 非空
- 聚合维度：`matched_question`
- 统计值：出现次数 `count(*)`
- 排序与返回：按次数降序取 Top10
