# 愚者：黑暗二重身适配

面向 Minecraft Forge 1.20.1 的独立 BOTH 附属，为 Dark Doppelganger `9.8.2-1.20.1` 提供：

- 来源 20 个语言键、4 个派生实体名、4 个声音字幕和全部已知游戏内硬编码文案的简体中文；
- `darkdoppelganger:dark_doppelganger` BetterMorph 可玩身份；
- 镜刃四式、暗影斩击、镜界跃迁、召唤镜影仆从、生命汲取五个主动技能；
- 本人和观察者可见的来源 GeckoLib 动作投影；
- 精确来源 Jar、Mixin、动作协议和 thin Jar 自动合同。

## 依赖

- Minecraft 1.20.1、Forge 47.4.12+
- Dark Doppelganger 9.8.2-1.20.1
- The Fool / Age of Mythology 0.3.1
- BetterMorph 0.0.54
- Iron's Spellbooks 3.15.6
- GeckoLib 4.8.4

来源模组自己的 Curios、Caelus 和 PlayerAnimator 依赖仍须由整合包提供。本附属不会内嵌或修改任何第三方 Jar。

## 构建

```powershell
.\gradlew.bat --no-daemon clean test build
openspec validate add-dark-doppelganger-localization-and-morph-skills --strict
```

产物位于 `build/libs/thefool_dark_doppelganger_morph-0.1.2.jar`。完整行为边界和测试入口见 `docs/indexes/docs-index.md`。

## 许可

本仓自有代码与资源保留全部权利。Dark Doppelganger 的名称、类型和被兼容接口属于上游，MIT 声明见 `THIRD_PARTY_NOTICES.md`。
