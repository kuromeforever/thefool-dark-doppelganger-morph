## ADDED Requirements

### Requirement: 所有变身主动不强制播放动作
附属 SHALL 停止全部主动技能的视觉START及来源施法动作，但 SHALL 保留技能业务时序和60tick输入冷却。

#### Scenario: 迟到开始包
- **WHEN** 客户端收到旧版或迟到的协议2 START
- **THEN** 不绑定载体、不缓存控制器、不恢复任何技能动作。

### Requirement: 真实Boss与施法业务隔离
附属 SHALL 仅抑制精确归属的虚拟变身/预览实体，不修改来源世界实体或服务器MagicData。

#### Scenario: 同场Boss与变身玩家
- **WHEN** 两者同时施法
- **THEN** 玩家变身仅保留普通移动，Boss按来源播放动作，两者的原生伤害和完成逻辑继续执行。
