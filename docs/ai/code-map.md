# 源码地图

最后更新：2026-09-08。Java 包根为 `io.github.kuromeforever.thefooldarkdoppelgangermorph`。

| 路径 | 职责 |
| --- | --- |
| morph/Registration、SpellCatalog | 一个可玩/一个技术身份，固定 5+21 技能目录 |
| morph/Abilities | 60 tick 输入冷却、来源条件预检、原生会话、两项自有时间线 |
| runtime/SpellSessions | identity/MagicData/spell/opaque NativeSession 精确收据 |
| runtime/BladeComboService、Combat | 镜刃冻结目标、可见性和实际损血治疗 |
| runtime/ActionPhaseClock | 序号、过期、STOP、elapsed、精确引用合同 |
| network/ | 协议 2 载体 UUID、维度、服务端阶段时间和有界会话 |
| client/Presentation、BirthPoseReset | 组合 updater、独立预览、出生 age 和骨骼恢复 |
| client/ClientActions | 早到 START、迟到观察、当前载体/控制器精确投影 |
| mixin/accessor/ | 单仆从、出生/动作控制器、animatingLegs 窄私有访问 |
| mixin/compat/NativeCompletionMixin | 两个来源完成调用的透明观察 |
| mixin/client/ | Boss 条汉化、预览边界、动作时钟、Travel 动画只读映射 |
| lifecycle/ | 换形/死亡/退出/切维和外来施法归属撤销 |
| src/test/java | 精确来源、行为状态、网络、语言、依赖和 thin 合同 |

省略类名前缀 DarkDoppelganger，实际路径按包目录查找。
