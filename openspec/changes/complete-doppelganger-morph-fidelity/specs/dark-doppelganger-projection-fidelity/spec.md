## ADDED Requirements

### Requirement: 二十六项来源主动目录

附属 MUST 保留原五主动顺序并追加明确的十八项 Iron 与三项 Travel 来源法术，总计二十六项。所有入口 MUST 使用唯一 60 tick AOM/BetterMorph 输入冷却，且 MUST NOT 用输入冷却截断原生会话、效果或复施状态。

#### Scenario: 来源条件失败

- **WHEN** 没有合法目标、已有存活仆从或来源前置条件失败
- **THEN** 不建立技能会话且不消费输入冷却，返回条件提示

#### Scenario: 原生持续与复施

- **WHEN** 输入冷却结束而来源效果或邪术冲击波复施窗口仍存活
- **THEN** 来源状态继续由原生管理，附属不统一reset或删除同名效果

### Requirement: 首帧出生准备隔离

附属 MUST 在渲染前组合原 EntityUpdater，对精确当前玩家 identity 或独立未入世界预览准备来源出生完成年龄、控制器和初始骨骼。附属 MUST NOT 对真实入世界 Boss 执行此准备或调用来源完整 tick/AI/onAddedToWorld。

#### Scenario: 独立预览与首次变身

- **WHEN** 当前投影出生年龄尚未完成
- **THEN** 在第一次实体绘制前准备 age=46 并恢复零厚度出生姿势

#### Scenario: 真实 Boss 同场

- **WHEN** 目标是世界实际跟踪的来源实体
- **THEN** 不获得附属投影权限，其出生与AI保持来源行为

### Requirement: 原生会话精确凭证

来源法术 MUST 使用 NATIVE_SESSION 和匹配当前identity、MagicData、spell对象及AOM不透明NativeSession代次的收据。完成观察 MUST 原样执行来源调用一次并区分成功、中断和异常。清理 MUST 只取消预期收据。

#### Scenario: 旧清理遇到相同法术的新会话

- **WHEN** 玩家开始新的装备或变身施法且旧收据仍被回调持有
- **THEN** 旧收据不能完成、取消或认领新原生会话

#### Scenario: 原生调用中断

- **WHEN** 原生完成节点携带interrupted或抛出异常
- **THEN** 原调用语义保持，附属停止旧投影而不制造成功动作或伤害

### Requirement: 协议二阶段投影

协议 MUST 包含载体UUID、维度、服务端阶段时间和递增序号。早到START MUST 等待对应identity，迟到观察 MUST 消费elapsed。STOP MUST 继续按castUUID相等清理，过期/旧序号 MUST 被拒绝。

#### Scenario: START先于身份

- **WHEN** 有效START先于对应载体同步到达
- **THEN** 保留待播状态并在精确载体可用时按已过时间播放

#### Scenario: 迟到与旧STOP

- **WHEN** 观察者进入追踪或旧STOP晚于新施法到达
- **THEN** 有效阶段从实际elapsed恢复且旧STOP不能结束新cast

#### Scenario: 身份回调先于服务器NBT装载

- **WHEN** BetterMorph先create并setIdentity触发回调，随后readNbt安装服务器UUID
- **THEN** 未绑定START在原维度及有限时限内保留，待NBT完成后才按当前引用和服务器UUID认证，不能被随机构造UUID删除

#### Scenario: 同一客户端对象被NBT替换服务器身份

- **WHEN** 同类型payload复用客户端identity对象但readNbt更换其服务器UUID
- **THEN** 同引用快速路径及控制器时钟之前校验服务器UUID、维度和时限，旧载体动作作废

### Requirement: 来源动画只读映射和薄附属

附属 MUST 显式依赖Travel最低兼容版本，五条缺失动画只对精确投影读取来源已加载缓存。附属 MUST 保持原生模型与缓存所有权，不复制第三方代码/模型/动画。姿势和控制器状态 MUST 在动作/身份结束及资源重载时清理。

#### Scenario: Travel资源可用

- **WHEN** 精确投影请求来源五条Travel动作之一
- **THEN** 使用来源baked动画与对应腿部姿势，真实Boss模型不改变

#### Scenario: 发布合同

- **WHEN** 生成0.1.4发布Jar
- **THEN** reobf/thin检查通过且Jar不包含任何Travel或其他依赖的类/动画资源
