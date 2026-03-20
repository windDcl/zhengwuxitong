# 缺陷清单与修复记录（T28.6）

1. 登录失败 `Invalid salt version`
- 原因：初始化密码哈希前缀兼容性问题
- 修复：改为 `$2a$` 前缀

2. 问答日志写入 `created_at cannot be null`
- 原因：JPA 插入空时间字段
- 修复：时间列改为数据库默认写入（insertable/updatable false）

3. 问答日志外键失败 `matched_faq_id`
- 原因：写入了索引中的无效 FAQ ID
- 修复：仅在 FAQ 存在时写入 `matched_faq_id`

4. 参数绑定异常 `Name for argument ... not specified`
- 原因：控制器参数未显式命名
- 修复：补全 `@PathVariable("id")` 和 `@RequestParam("...")`

5. Excel 导入全部判空
- 原因：读取单元格方式不稳定
- 修复：使用 `DataFormatter` 读取
