# 语义评测报告（T26.2/T26.3/T26.4/T26.6）

执行时间：2026-03-15
数据集：`docs/testing/eval-question-set.csv`（9 条）
工具：`scripts/eval/run_eval.py`

阈值扫描：0.3 / 0.5 / 0.75 / 0.9

最佳参数：
- threshold = 0.3
- Top1 准确率 = 1.00
- Top3 召回率 = 1.00

说明：
- 当前结果基于小样本验证。
- 300 条正式 FAQ 到位后，需要复跑并固化最终阈值。
