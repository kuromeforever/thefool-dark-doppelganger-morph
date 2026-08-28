## ADDED Requirements

### Requirement: 镜刃四式来源时间线
镜刃四式 SHALL 使用来源四个剑技动作及命中节点 `5/6/8/8 tick`，单次第一击造成 12 点玩家归属伤害，40% 概率追加不同动作和 8 点第二击，每个节点最多命中一个合法目标。

#### Scenario: 单击和连击
- **WHEN** 服务端开始镜刃四式
- **THEN** 本次动作序列被固定，命中只在对应来源节点发生，客户端随机结果不能改变伤害

#### Scenario: 目标离开
- **WHEN** 目标在命中节点前死亡、离开范围、转为友方或不再满足 PvP
- **THEN** 该节点不造成伤害且不改选范围外目标

### Requirement: 来源法术协调
暗影斩击、镜界跃迁和召唤镜影仆从 MUST 通过 AOM `MorphSpellCastCoordinator` 的原生会话直接执行精确注册法术，不得重写来源投射物、传送安全点、传送门、仆从所有权、Iron 施法网络或取消逻辑。

#### Scenario: 镜界跃迁完成
- **WHEN** 玩家完成来源 14 tick 传送施法
- **THEN** 来源选择安全落点并创建来源传送门、25 tick 抗性和30 tick 速度效果

#### Scenario: 已有存活仆从
- **WHEN** 来源私有单仆从检查确认玩家已有存活仆从
- **THEN** 服务端激活门禁拒绝新技能且不消费冷却

#### Scenario: 来源仆从跨换形
- **WHEN** 玩家完成召唤后换形、登出或重新进入世界
- **THEN** 附属不删除或复制该来源正式法术实体，来源持久所有权继续有效

### Requirement: 生命汲取有界结算
生命汲取 SHALL 在已加载的 8 格范围内按距离稳定选择最多8个敌对生物或原生 PvP 合法玩家，每个尝试4点玩家归属伤害，只按真实生命损失治疗且单次总治疗不超过16。

#### Scenario: 免疫或保护拒绝
- **WHEN** 目标免疫伤害、保护事件取消或生命值没有下降
- **THEN** 该目标不贡献治疗

### Requirement: 服务端权威与动作投影
技能随机、目标、伤害、治疗、传送、生成、冷却和完成 MUST 由服务端拥有；客户端 SHALL 只在当前 BetterMorph 虚拟 identity 上投影稳定动作 ID和 cast UUID。

#### Scenario: 两名同形态玩家
- **WHEN** 两名玩家同时释放不同技能
- **THEN** 动作、cast、目标、伤害和冷却按玩家 UUID 隔离且观察者看到正确动作

#### Scenario: 迟到追踪者
- **WHEN** 客户端在动作开始后开始追踪施法玩家
- **THEN** 服务端发送剩余时间快照且不会重放业务结果

### Requirement: 生命周期和客户端隔离
动作与法术会话 MUST 在换形、死亡、克隆、退出、切维、世界卸载和停服时幂等清理；common 代码 MUST 不在 Dedicated Server 初始化客户端类。

#### Scenario: 中途换形
- **WHEN** 玩家在技能或原生法术施法期间换形
- **THEN** 后续伤害和动作被取消、视觉状态停止且来源正式仆从不被误删

#### Scenario: Dedicated Server 启动
- **WHEN** Forge Dedicated Server 加载附属
- **THEN** 不解析 `net.minecraft.client` 类且网络只注册显式 S2C 消息
