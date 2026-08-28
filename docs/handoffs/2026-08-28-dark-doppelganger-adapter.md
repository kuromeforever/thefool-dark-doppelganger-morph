# 黑暗二重身适配交接

最后更新：2026-08-28

## 当前变更

OpenSpec：`add-dark-doppelganger-localization-and-morph-skills`。实现对应 Dark Doppelganger `9.8.2-1.20.1`，不能未经来源重审扩展版本范围。

## 接手顺序

1. 阅读 `AGENTS.md`、`docs/ai/00-start-here.md` 和 `docs/ai/active-context.md`。
2. 阅读 `docs/来源行为与技能适配审计.md` 与 OpenSpec design/specs。
3. 执行 `docs/ai/qa-commands.md`。
4. 若来源升级，先比较 Jar SHA-256、20 个语言键、声音、方法描述符、literal 调用数、法术构造器和四个动画片段，再改代码。
5. 不要复制来源仆从 NBT、Boss `summonMinions()` 或安全传送算法；继续复用来源法术与 scoped invoker。

## 高风险点

- literal Mixin 的方法名、调用数和映射模式与精确二进制绑定。
- 中文 Boss 名称必须继续被来源客户端 Boss 条逻辑识别。
- 客户端动作只是一条有界视觉事实，不能用于决定命中、召唤、传送、治疗或冷却。
- AOM provider API兼容边界固定为 v1.1；AOM 不应反向依赖本附属。

## 回滚

从整合包移除 `thefool_dark_doppelganger_morph-*.jar`，并恢复部署前的 AOM all Jar备份。附属不写私有存档数据；来源持久仆从仍由来源模组管理。回滚后会失去中文覆盖和该 BetterMorph provider，但不应破坏 AOM 终焉或来源实体。

本轮部署产物：附属 SHA-256 `1C87ED3F342AAA640DA82F262C725FA02BFD687B36196F9E28540D5A0C69187A`，AOM SHA-256 `91CB2037B3B47FEAA1C267356EBAC578D3C07D8B18CFA298DB37D352F8561144`。部署前 AOM 备份位于工作区级 `release-backups/2026-08-28-dark-doppelganger-before-deploy/`。
