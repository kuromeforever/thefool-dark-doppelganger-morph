# 源码地图

最后更新：2026-08-28

| 路径 | 职责 |
| --- | --- |
| `TheFoolDarkDoppelgangerMorph.java` | 构造期入口，初始化网络、生命周期和 provider。 |
| `morph/` | 一个 identity、固定五技能目录、技能时间线和来源法术入口。 |
| `runtime/` | 服务端目标、伤害、治疗和镜刃短会话。 |
| `network/` | 稳定动作 ID、cast UUID、开始/停止/追踪快照。 |
| `client/` | 客户端从玩家 UUID解析当前 BetterMorph 虚拟 identity并播放来源动作。 |
| `lifecycle/` | 换形、死亡、克隆、退出、切维、卸载和停服清理。 |
| `compat/`、`mixin/compat/` | 来源 literal 到 translatable 的精确桥。 |
| `mixin/client/` | 中文名称下的来源 Boss 条兼容。 |
| `mixin/accessor/` | 来源单仆从私有检查的 scoped invoker。 |
| `src/main/resources` | 元数据、双语技能文本、来源中文/字幕覆盖。 |
| `src/test/java` | 来源字节码、目录、冷却、汉化、网络、生命周期和 thin Jar合同。 |
