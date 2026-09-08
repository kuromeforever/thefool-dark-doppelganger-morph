# AGENTS.md

## 入口协议

任何非简单任务开始前读取 `docs/ai/00-start-here.md`、`docs/ai/active-context.md`、`docs/ai/code-map.md`、`docs/indexes/source-index.md` 和 `docs/来源行为与技能适配审计.md`；行为、依赖、网络、资源或发布合同变化时先检查 `openspec/`。

## 冻结边界

- 本仓库是独立 BOTH 薄附属；依赖方向为附属 -> Dark Doppelganger / AOM / BetterMorph，AOM 不反向依赖本附属。
- 目标为 Forge `47.4.12`、Dark Doppelganger `9.8.2-1.20.1`、AOM `0.3.1` 本轮 NativeSession API、BetterMorph 编译版本 `0.0.60`、运行范围 `[0.0.54,)`、Iron `3.15.6`、GeckoLib `4.8.4`、Travel `6.3.0-1.20.1`。依赖范围以元数据为准。
- 通过 AOM Morph API v1.1 注册一个可玩 Boss、一个技术仆从和 26 主动，只设置 60 tick AOM/BetterMorph 输入冷却，不截断来源读条/效果/复施生命期。
- 长篇来源和架构事实以 AOM docs/thefool/thefool_dark_doppelganger_morph/ 下的权威文档为准，子仓仅维护入口、代码、测试和局部 OpenSpec。
- 禁止运行时反射。私有来源状态只能使用 scoped Mixin accessor/invoker并由精确字节码合同锁定。
- 业务结果服务端权威；客户端只投影动作，不同步普通移动、不回传命中或完成结果。
- 不复制或打包来源类、模型、材质、数据和音频；仅允许独立编写的来源 `zh_cn` 与字幕 overlay，并保留上游 MIT 声明。
- 不修改 AOM 终焉召唤、Boss 属性、伤害上限、阶段、奖励或网络投影。
- 自动合同、真实客户端、多人、Dedicated Server和不重置存档证据分开记录。
- 完成后重建 AOM all Jar和附属 Jar，安全同步 PCL并核对哈希与唯一 mod id。
- 保留用户现有修改；只精确暂存本任务文件。每轮验证后确认本轮启动的 Java/Gradle/Minecraft 进程退出。

## 常用验证

以 `docs/ai/qa-commands.md` 为唯一命令入口。
