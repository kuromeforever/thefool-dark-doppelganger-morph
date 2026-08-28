# 项目上下文

## 项目

- Minecraft 1.20.1 / Forge 47.4.12 / Java 17
- 模组 ID：`thefool_dark_doppelganger_morph`
- 形态：独立 BOTH thin Jar

## 边界

- 本附属单向依赖 Dark Doppelganger、AOM、BetterMorph、Iron's Spellbooks 与 GeckoLib。
- AOM 拥有 Morph API、轮盘、调度和唯一玩家冷却；附属拥有来源适配、五技能、汉化和来源合同。
- 不使用运行时反射；私有来源状态只允许 scoped Mixin accessor/invoker并必须有合同测试。
- 服务端拥有业务结果，客户端只投影视觉。
- OpenSpec 文档使用中文，保留验证所需的 `Requirement` 与 `Scenario` 结构词。

## 验证

每次发布必须通过来源 Jar、汉化、目录、技能、网络、生命周期和 thin Jar自动合同，重建 AOM all Jar，并将自动、客户端、多人、Dedicated Server和不重置存档证据分别记录。
