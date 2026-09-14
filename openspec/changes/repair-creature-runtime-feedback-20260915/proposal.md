# 修复生物反馈运行路径

## Why
结束段按来源实际动画长度计算，控制器结束与取消时清空排队动作并复位；未知长度有界回退，结束段最长六秒。

## What Changes
补齐已确认的动作与运行结果。完整审计归 AOM 同名 change。

## Capabilities
### New Capabilities
- `morph-feedback-runtime`：反馈对应的动作/运行闭环。

## Impact
仅本附属对应实现、测试与入口文档。
