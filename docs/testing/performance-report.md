# 基础性能报告（T28.5）

执行时间：2026-03-15
工具：`scripts/test/perf_quick.py`
场景：连续 30 次 `POST /api/public/ask`

结果：
- p50 = 8.28ms
- p95 = 9.03ms

说明：
- 为本地单机开发环境结果，主要用于回归对比。
