# AI 入口

最后更新：2026-08-28

本项目是 Dark Doppelganger `9.8.2-1.20.1` 的独立 BOTH 薄适配层，为来源提供完整游戏内简体中文、一个 BetterMorph 可玩身份、一个技术仆从和五个来源对齐主动技能。

非简单任务依次读取：

1. `AGENTS.md`；
2. `docs/ai/active-context.md`；
3. `docs/ai/code-map.md` 与 `docs/indexes/source-index.md`；
4. `docs/来源行为与技能适配审计.md`；
5. 相关 `openspec/` change；
6. 验证时读取 `docs/ai/qa-commands.md` 与 `docs/实机验收清单.md`。

长期事实：AOM 拥有 provider 目录、BetterMorph 调度和唯一玩家冷却；本附属拥有来源类型、五技能、动作投影、汉化和来源合同。AOM 终焉逻辑不依赖本附属。
