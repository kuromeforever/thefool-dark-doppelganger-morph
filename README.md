# 愚者：黑暗二重身适配

Forge 1.20.1 独立 BOTH 薄附属，当前版本 `0.1.4`。

- 补齐来源游戏内简体中文、字幕、进度和命令提示。
- 注册一个可玩黑暗二重身、一个技术仆从和 26 个主动技能。
- 修复首次预览/变身出生状态，按来源施法阶段同步本人和观察者动作。
- 真实 Boss 的出生、AI、阶段、奖励和来源持久仆从保持来源所有权。

完整依赖范围以 `META-INF/mods.toml` 为准。新增 Travel Optics 6.3.0-1.20.1 或兼容后续版本。AOM 必须包含本轮 NativeSession 收据 API，与附属成对发布。来源 Curios、Caelus 和 PlayerAnimator 仍由整合包提供，不内嵌第三方 Jar、模型或动画。

来源和架构事实以 [AOM 权威文档](../thefool-forge-1.20.1/docs/thefool/thefool_dark_doppelganger_morph/[import-thefool_dark_doppelganger_morph]黑暗二重身汉化与变身适配架构审计.md) 为准。构建见 [QA 命令](docs/ai/qa-commands.md)，产物为 `build/libs/thefool_dark_doppelganger_morph-0.1.4.jar`。自动验证不替代 [实机验收](docs/实机验收清单.md)。

自有代码与资源保留全部权利。来源 MIT 声明见 `THIRD_PARTY_NOTICES.md`。
