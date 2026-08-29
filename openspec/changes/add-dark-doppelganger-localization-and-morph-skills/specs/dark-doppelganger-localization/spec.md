## ADDED Requirements

### Requirement: 完整游戏内简体中文覆盖
附属 SHALL 为精确来源 Jar 的全部 20 个现有语言键、4 个派生实体显示键和4个声音字幕提供非空简体中文，并 SHALL 为两个来源进度与经字节码确认的 Boss、阶段、仆从、命令和废弃卷轴 literal 提供 translatable 映射。

#### Scenario: 中文资源加载
- **WHEN** 客户端语言为简体中文并加载来源与附属
- **THEN** 来源物品、法术、进度、实体、字幕和玩家消息均显示中文且没有裸翻译键

#### Scenario: 未知 literal
- **WHEN** 来源向翻译桥传入未登记字符串
- **THEN** 附属原样返回该字符串且不做全局替换

#### Scenario: 两个来源进度
- **WHEN** 服务端加载 `darkdoppelganger:root` 与 `darkdoppelganger:kill`
- **THEN** 四个展示字段使用 `advancement.summon_dark_doppelganger.*` 与 `advancement.kill_dark_doppelganger.*` 稳定语义键，且不得包含英文 `text` 或句子式翻译键

#### Scenario: 召唤管理命令
- **WHEN** 管理员执行 `/darkd summon_doppelganger` 并进入附近无人、正常提示、目标失效或异常路径
- **THEN** 四条反馈均为 translatable，动态玩家名以组件参数传入且不改变来源五秒倒计时

### Requirement: Boss 条兼容
附属 MUST 在不修改真实 Boss 阶段和 BossEvent 所有权的前提下，让来源自定义 Boss 条同时识别旧英文名称与当前语言的普通/终焉名称。

#### Scenario: 普通和终焉名称
- **WHEN** Boss 名称由英文 literal 改为 translatable 普通名或终焉名
- **THEN** 来源自定义 Boss 条仍被绘制并正确更新进度

### Requirement: 来源资源和附属语言边界
发布 Jar SHALL 仅在来源命名空间包含 `lang/zh_cn.json`、`sounds.json` 与内置服务端数据包中的两个进度覆盖，并 SHALL 保持附属 `en_us` 与 `zh_cn` 键集合一致、值非空且描述真实技能行为。

#### Scenario: Thin Jar 资源检查
- **WHEN** 验证最终 reobf Jar
- **THEN** 未批准的来源模型、材质、音频、数据和语言文件均不存在，两个附属语言文件键集合完全一致，两个进度覆盖的非展示语义与精确来源一致

### Requirement: 元数据和配置边界
附属 SHALL 不重写第三方 Jar、来源 `mods.toml` 显示名或既有用户 TOML 注释，并 SHALL 以中文文档解释当前配置。

#### Scenario: 安装和卸载附属
- **WHEN** 用户安装或移除附属
- **THEN** 来源 Jar 与既有配置文件字节不被附属修改

#### Scenario: 外部汉化补充包
- **WHEN** 整合包同时加载用户冻结的 `早早汉化补充包`
- **THEN** 附属不得修改、删除或重新打包该资源包，具体中文措辞继续遵循 Minecraft 客户端资源优先级
