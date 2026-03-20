# 分支策略（T02.2）

- 主分支：`main`（仅稳定可发布代码）
- 集成分支：`dev`（日常合并验证）
- 功能分支：`feature/<task-id>-<short-name>`

合并规则：
- 功能分支提 MR 到 `dev`
- 通过测试后由 `dev` 合并到 `main`
