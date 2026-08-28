## Why

Dark Doppelganger `9.8.2-1.20.1` 没有简体中文资源，多个 Boss、阶段、仆从、命令和废弃道具提示仍由英文 literal 直接生成；当前 AOM 也没有为其注册 BetterMorph 身份或技能。需要用一个独立 BOTH 附属模组统一拥有来源汉化、变身技能和动作投影，同时保持 AOM 终焉事件与来源 Boss 行为不变。

## What Changes

- 新建 `thefool_dark_doppelganger_morph` 薄附属，锁定精确来源 Jar、AOM Morph API v1.1、BetterMorph `0.0.54`、Iron's Spellbooks `3.15.6` 与 GeckoLib `4.8.4`。
- 为来源 20 个语言键、4 个派生实体名、4 个声音字幕以及经字节码确认的玩家可见 literal 提供完整简体中文；修复中文 Boss 名称与来源英文前缀 Boss 条识别之间的兼容冲突。
- 通过 `AomMorphApi.registerProvider` 注册一个黑暗二重身可玩身份、一个镜影仆从技术身份、五个 60 tick 统一冷却主动技能。
- 直接复用来源 `doppel_portal`、`summon_doppel_minion` 和 Iron's Spellbooks `shadow_slash` 法术；剑技与生命汲取由附属建立有界、服务端权威时间线。
- 增加稳定动作 ID、cast UUID、开始/停止/追踪快照和全生命周期清理；客户端仅投影当前 BetterMorph 虚拟身份的 GeckoLib 动作。
- 增加精确来源哈希、字节码描述符、汉化、目录、冷却、网络、客户端隔离和 thin Jar 合同，并提供单机、多人、Dedicated Server 与生命周期验收清单。

## Capabilities

### New Capabilities

- `dark-doppelganger-localization`: 来源语言键、派生键、字幕、literal 翻译桥与 Boss 条兼容合同。
- `dark-doppelganger-morph-catalog`: 一个可玩身份、一个技术身份和五主动技能的确定性目录与统一冷却。
- `dark-doppelganger-morph-runtime`: 服务端技能、来源法术协调、动作投影、目标预算和生命周期合同。
- `dark-doppelganger-release-contract`: 精确依赖、薄 Jar、验证、跨仓文档、AOM 构建和整合包部署合同。

### Modified Capabilities

无。

## Impact

- 新增独立仓库、Forge 工程、中文 OpenSpec、Java/资源/测试和发布文档。
- 运行时依赖 Dark Doppelganger、AOM、BetterMorph、Iron's Spellbooks 与 GeckoLib；不向 AOM 增加反向依赖。
- AOM 侧只新增跨仓权威入口、索引、长期待办和 `0.3` 更新日志，不修改终焉召唤、Boss 属性、伤害上限或 BetterMorph 核心实现。
- 本地整合包新增唯一一份附属 Jar，并重新对账来源、附属与 AOM all Jar 的 SHA-256。
