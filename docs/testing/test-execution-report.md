# 测试执行报告（T28.2/T28.3/T28.4/T28.5）

执行日期：2026-03-15

## 功能测试
- 结果：通过
- 覆盖：登录、分类、FAQ、公告、导入、问答、日志、未命中、Dashboard

## 接口测试
- 结果：通过
- 关键接口：
  - `/api/admin/login`
  - `/api/admin/categories`
  - `/api/admin/faqs`
  - `/api/admin/announcements`
  - `/api/admin/import/faq-excel`
  - `/api/public/ask`
  - `/api/public/suggestions`
  - `/api/public/categories/{id}/faqs`
  - `/api/admin/dashboard/*`

## 数据一致性测试
- 结果：通过
- 验证点：FAQ 与 alias 数量一致、问答后日志写入、未命中落库、热门统计可回溯

## 基础性能测试
- 工具：`scripts/test/perf_quick.py`
- 场景：连续 30 次问答请求
- 结果：见性能报告
