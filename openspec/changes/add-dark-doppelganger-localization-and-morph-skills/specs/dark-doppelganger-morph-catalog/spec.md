## ADDED Requirements

### Requirement: 确定性 provider 目录
附属 MUST 在构造期通过 AOM Morph API v1.1 注册唯一 provider，并 SHALL 声明 `dark_doppelganger` 为可玩 identity、`dark_doppelganger_minion` 为 technical identity。

#### Scenario: API 可用
- **WHEN** AOM Morph API 主版本为 1 且版本不低于 v1.1
- **THEN** provider 以稳定资源 ID 注册一个可玩身份和一个技术身份

#### Scenario: API 不兼容或 provider 被拒绝
- **WHEN** API 版本不兼容或 AOM 拒绝 provider
- **THEN** 附属记录错误并跳过目录注册，游戏加载继续

### Requirement: 五主动技能闭集
黑暗二重身 identity SHALL 按固定顺序提供镜刃四式、暗影斩击、镜界跃迁、召唤镜影仆从和生命汲取五个主动技能，且 SHALL 不注册额外被动。

#### Scenario: BetterMorph 目录消费
- **WHEN** AOM 冻结并消费外部 provider
- **THEN** 黑暗二重身目录恰好包含五个稳定技能 ID且顺序不随启动变化

### Requirement: 唯一玩家级统一冷却
五个主动技能 MUST 只调用 `Ability#setCooldown(60)`，不得新增 resolver、Capability、NBT、SavedData、ready-at Map、自定义冷却包或 HUD。

#### Scenario: 成功激活
- **WHEN** 任一主动通过服务端门禁并建立技能或法术时间线
- **THEN** BetterMorph/AOM 唯一玩家级主动槽进入 60 tick 冷却

#### Scenario: 前置门禁失败
- **WHEN** 无目标、已有来源仆从或 Iron 原生施法状态不允许开始
- **THEN** 不建立动作或法术会话且不消费冷却

### Requirement: 不复制 Boss 遭遇属性
玩家 identity SHALL 继续使用 AOM/BetterMorph 通用形态与属性合同，不得复制终焉 Boss 生命、伤害上限、阶段恢复、音乐、Boss 条、终局法术轮盘或硬模式净化。

#### Scenario: 玩家换形
- **WHEN** 玩家变为黑暗二重身
- **THEN** 只获得目录中的五个主动，不触发真实 Boss 的阶段和事件流程
