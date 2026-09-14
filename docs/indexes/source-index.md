# 源码索引

最后更新：2026-09-08。

事实优先级为当前 Jar 字节码/资源，其次实际 AOM/BetterMorph 源码。旧本地 Dark Doppelganger 8.3.1 源码不能代替 9.8.2。

- Dark Doppelganger 9.8.2：Boss/仆从、法术、出生状态和动画。
- AOM 0.3.0 本轮构建：Morph API、Ability、BaseIdentity、NativeSession。
- BetterMorph 0.0.63 编译验证：EntityUpdater 和 identity 同步，运行范围沿用元数据。
- Iron 3.15.6：正式 AbstractSpell Registry、原生开始/持续/完成/中断。
- Travel 6.3.0-1.20.1：三项来源法术、五条 humanoid 动画。
- GeckoLib 4.8.4：控制器时钟、初始骨骼快照和只读 baked animation。

哈希、节点与省略项统一见 [来源审计入口](../来源行为与技能适配审计.md)。


2026-09-14 来源动作反馈及编译依赖对齐见 `docs/ai/handoffs/2026-09-14-creature-feedback.md`；长篇事实归 AOM 对应权威审计。


2026-09-15：结束段按来源实际动画长度计算，控制器结束与取消时清空排队动作并复位；未知长度有界回退，结束段最长六秒。 本轮证据入口：`docs/ai/handoffs/2026-09-15-creature-runtime-feedback.md`。
