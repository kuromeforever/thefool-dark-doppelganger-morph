## Context

目标来源为 Dark Doppelganger `9.8.2-1.20.1`，精确 Jar SHA-256 为 `A40E7AC6599A208DA8BCEC747EA2630C3DBB903D3C3353C860EBCCA62BADF988`。来源是 Forge BOTH 模组，依赖 Iron's Spellbooks、GeckoLib、Curios、Caelus 与 PlayerAnimator；AOM `0.3.1` 已强依赖来源并在终焉嬗变中创建真实 Boss，但没有来源专用 BetterMorph provider。

来源 `en_us.json` 只有 20 键，没有 `zh_cn.json`，四个声音没有字幕，Boss、阶段、仆从、命令和废弃卷轴含精确英文 literal。来源两个进度共有四个展示字段，其中 `root` 把英文句子误作翻译键，`kill` 直接使用英文 `text`，因此已经存在的四个语义翻译键没有被真实入口消费。来源客户端通过渲染后的英文 Boss 名称前缀识别自定义 Boss 条，不能只替换服务端名称。Boss `summonMinions()` 还对同一仆从执行两次实体加入，因此玩家召唤技能必须复用独立来源法术而不能复制该 AI 分支。

参考项目是 `thefool-boss-companions-morph` 与 `thefool-crop-critters-modify`：AOM 拥有 provider 冻结、轮盘、调度、技术身份并集和唯一玩家冷却；附属拥有来源类型、技能目录、来源动作、汉化、容量和来源合同。自动合同与真实客户端、多人、Dedicated Server 证据必须分开。

## Goals / Non-Goals

**Goals:**

- 以独立 BOTH 薄 Jar 完成全部游戏内简体中文覆盖。
- 注册一个可玩 Boss identity、一个技术仆从 identity 和五个来源对齐主动技能。
- 让业务结果保持服务端权威，让 GeckoLib 自定义动作可靠投影给本人和观察者。
- 保持 AOM 终焉召唤、Boss 属性、伤害上限、阶段、奖励和来源实体行为不变。
- 锁定精确来源 Jar，并用自动合同阻止版本漂移、Mixin 漂移、资源复制和依赖内嵌。

**Non-Goals:**

- 不修改第三方 Jar、Forge 模组列表中的来源 `displayName` 或既有 TOML 注释。
- 不修改、删除或重新打包用户冻结的 `早早汉化补充包`，外部资源包可以继续按 Minecraft 资源优先级覆盖具体中文措辞。
- 不把来源全部阶段法术、满血复活、Boss 条、Boss 音乐、雷电、爆炸、硬模式净化或伤害上限授予玩家。
- 不修复来源废弃卷轴的阻塞等待，也不修改 Boss AI 的双实体加入；适配技能绕开后者。
- 不向 AOM 增加新模组反向依赖，不建立第二套冷却、法力、HUD 或普通移动同步。

## Decisions

### 1. 独立附属和单向依赖

新模组依赖 Dark Doppelganger、AOM 与 BetterMorph；AOM 不依赖新模组。构造期通过 `AomMorphApi.registerProvider` 声明目录，API 不兼容或 provider 被拒绝时记录错误并让游戏继续加载。替代方案是把技能写回 AOM，但这会让来源所有权、汉化和发布周期再次耦合，故不采用。

### 2. 一个可玩身份和一个技术身份

`darkdoppelganger:dark_doppelganger` 注册 `BaseIdentity`。`darkdoppelganger:dark_doppelganger_minion` 只声明 `technicalIdentity()`，真实来源召唤和渲染不受影响；两个传送门不是 `LivingEntity`，天然不进入 BetterMorph。替代方案是让仆从也可玩，但其所有权、主人皮肤、装备和施法状态使其成为派生技术实体，故不采用。

### 3. 五技能闭集与统一冷却

五个主动为镜刃四式、暗影斩击、镜界跃迁、召唤镜影仆从、生命汲取，全部只设置 `Ability#setCooldown(60)`。来源法术的 8/25 秒冷却和法力不形成第二套锁。前置门禁失败不建立 Ability 时间线；一旦时间线或原生法术会话成功建立，后续中断不返还冷却。

镜刃四式从四个来源动作中确定性保存本次序列，命中节点使用来源 `5/6/8/8 tick`；40% 概率追加不同动作，第一击 12、第二击 8，单节点最多一个目标。生命汲取把来源只攻击玩家的 Boss 语义转为敌对生物和原生 PvP 合法玩家，半径 8、最多 8 个目标、每个 4 点伤害，只按真实损血治疗且总治疗不超过 16。

### 4. 来源法术直接复用

暗影斩击使用 Iron's Spellbooks `SpellRegistry.SHADOW_SLASH`；镜界跃迁与召唤仆从使用来源 `SpellRegistry.DOPPEL_PORTAL`、`MINION_SPELL`。三者经 AOM `MorphSpellCastCoordinator.ExecutionMode.NATIVE_SESSION` 执行，保留 Iron 施法事件、网络、取消、传送安全点、传送门和来源仆从持久所有权，不要求玩家学会法术，也不污染额外施法数据。

召唤前置门禁通过 scoped static invoker 调用来源私有 `hasLivingMinion`，不复制私有 NBT 键或实体查找逻辑。Invoker 的目标名、描述符和静态性由精确 Jar 合同测试锁定。

### 5. 有界动作协议

动作 ID 使用稳定 `ResourceLocation`，消息为显式 `PLAY_TO_CLIENT`，包含玩家 UUID、动作 ID、active、剩余 tick 与 cast UUID。服务端每名玩家最多一个视觉动作，开始新动作先停止旧动作；动作最长 60 tick。追踪开始发送剩余时间快照，未知动作安全忽略，普通移动和姿态不发包。

客户端从真实玩家 UUID 取得当前 BetterMorph 虚拟 identity，仅在其为 `DarkDoppelgangerEntity` 时调用公开 `playAnimation`。服务端换形、死亡、克隆、退出、切维、世界卸载和停服清理会话；客户端只清理视觉缓存，不拥有命中或法术完成结果。

### 6. 汉化覆盖与窄 Mixin

附属提供 `assets/darkdoppelganger/lang/zh_cn.json` 和仅补字幕的 `sounds.json`。来源 literal 通过按类、方法、描述符和调用次数锁定的 `@Redirect` 转换为 translatable，未知字符串原样返回。Boss 条客户端兼容同时接受来源英文前缀和当前语言渲染后的普通或终焉名称。

附属通过 `AddPackFindersEvent` 注册强制启用的 `Pack.Position.TOP` 内置服务端数据包。该数据包只拥有 `darkdoppelganger:root` 与 `darkdoppelganger:kill` 两个进度覆盖，并且只把四个展示字段改为来源已经声明的稳定语义键。进度 ID、父子关系、图标、条件、Boss NBT、Toast 与聊天广播保持不变，因此既有存档不需要迁移。

来源命名空间只允许语言、声音和上述两个进度覆盖资源，附属自身 `en_us` 与 `zh_cn` 键集合完全一致。最终 Jar 禁止 AOM、BetterMorph、Iron、GeckoLib、Dark Doppelganger 类和未批准来源资源。

`/darkd summon_doppelganger` 的附近无人、执行异常、目标失效和五秒召唤提示使用精确静态方法 Mixin 转换为 translatable。动态玩家名作为组件参数传入，不解析拼接后的英文。来源既有五秒倒计时不属于汉化完成条件，本变更不增加等待、重试、吞错或强制重载。

### 7. 发布与文档所有权

附属仓维护逐节点来源审计、代码地图、QA 命令和实机清单；AOM 在 `docs/thefool/thefool_dark_doppelganger_morph/` 维护跨仓权威边界，并在源码/文档索引、长期待办和 `0.3` 更新日志引用它。发布时重新构建 AOM all Jar和附属 Jar，使用安全部署模块同步 PCL 并对账哈希与唯一 mod id。

## Risks / Trade-offs

- [来源升级导致 Mixin/法术契约漂移] → 锁定版本、SHA-256、类方法描述符、literal 调用次数和动画键；升级必须新建变更。
- [Redirect handler 静态性与来源目标不一致会在 FML 构造期崩溃] → 逐个锁定 13 个 Redirect 的来源目标描述符和目标/handler 静态性；实例目标只允许实例 handler，静态命令和客户端事件目标继续使用静态 handler。
- [中文名称破坏来源 Boss 条] → 客户端 scoped redirect 同时识别稳定翻译和旧英文，真实客户端验证普通/终焉阶段。
- [普通模组数据包覆盖顺序不足以证明来源进度被替换] → 使用强制启用的高优先级内置服务端数据包，并在单机与 Dedicated Server读取实际启用包和进度结果。
- [翻译键存在但进度入口没有消费] → 自动合同同时读取来源进度与覆盖进度，锁定四个展示键并比较全部非展示字段。
- [来源法术 raw config 警告对应错误配置值] → 自动验证注册对象，实机分别测距离、施法时间、抗性/速度和单仆从门禁；不得仅凭无崩溃判定通过。
- [虚拟 identity 动作粘滞或多人串号] → 每玩家单会话、cast UUID、明确 stop、追踪快照、客户端到期和全生命周期幂等清理。
- [生命汲取绕过 PvP/保护] → 使用玩家归属伤害源、排除友方/创造/旁观，只按真实生命下降计疗并设置目标与治疗上限。
- [来源持久仆从跨换形存在] → 这是来源正式法术语义；附属不标记、不删除、不持久化第二份所有权。卸载附属后来源仍可管理实体。
- [新远端仓库当前不存在] → 完成验证后用已登录的 `kuromeforever` GitHub 身份创建公开仓库并推送 `main`；本地提交身份固定为仓库身份。

## Migration Plan

1. 构建附属并执行来源、目录、汉化、网络、专服隔离和 thin Jar 合同。
2. 严格验证本 change；AOM 侧补齐跨仓文档和更新日志并重新构建 all Jar。
3. 用安全部署模块先 `-WhatIf`，再将唯一附属 Jar与当前 AOM all Jar同步到 `D:\PCL\.minecraft\versions\TheFool\mods`。
4. 对账 build/PCL SHA-256、mod id 唯一性和必需 Jar 条目。
5. 执行真实客户端、两人、Dedicated Server 与不重置存档生命周期清单。
6. 回滚时移除附属 Jar并恢复部署前 AOM Jar；来源 Boss、终焉事件和已存在来源仆从仍可运行。

## Open Questions

无。用户已批准按审计方案开始实现，五技能、60 tick 统一冷却、技术仆从、来源仆从跨换形持续及完整游戏内汉化均按推荐值冻结。用户同时明确要求 `早早汉化补充包` 保持原样。
