## ADDED Requirements

### Requirement: 精确来源和字节码合同
验证 MUST 锁定来源 Jar SHA-256、20 个英文键、4 个声音、关键实体/法术/客户端方法描述符、四剑技名称与命中节点、私有单仆从方法和每个 literal 注入点调用次数。

#### Scenario: 来源 Jar 漂移
- **WHEN** 来源版本、哈希、类、描述符、动画键或注入次数变化
- **THEN** 自动验证失败并要求重新审计，不静默放宽版本范围

### Requirement: 薄 Jar 和依赖边界
最终 Jar MUST 不内嵌 AOM、BetterMorph、Iron's Spellbooks、GeckoLib 或 Dark Doppelganger 类，并 SHALL 仅包含批准的来源中文与声音覆盖。

#### Scenario: 发布物检查
- **WHEN** `verifyThinJar` 检查最终 reobf Jar
- **THEN** 外部类前缀、JarJar 依赖和未批准来源资源均为零，必需适配类和资源全部存在

### Requirement: 分层验证
完成声明 MUST 分别报告自动测试、OpenSpec strict、reobf/build、部署哈希、真实客户端、多人、Dedicated Server 和不重置存档生命周期证据，不得以其中一类替代另一类。

#### Scenario: 只有自动合同通过
- **WHEN** 测试和构建通过但未执行真实客户端或 Dedicated Server
- **THEN** 文档明确保留对应手测为待验收

### Requirement: AOM 与整合包同步
实现完成后 MUST 重新构建 AOM all Jar，将附属和 AOM Jar安全同步到本地 TheFool 整合包，并核对 build/PCL SHA-256、唯一 mod id和必需条目。

#### Scenario: 正式部署
- **WHEN** 安全部署 `-WhatIf` 通过并执行正式同步
- **THEN** PCL `mods` 中附属与 AOM 各只有一份且哈希与构建物一致

### Requirement: 跨仓文档和更新日志
AOM SHALL 维护本附属的 `[import-*]` 权威入口、索引、长期待办和 `0.3` 玩家更新日志；附属 SHALL 维护来源审计、代码地图、QA 与实机清单。

#### Scenario: 发布审计
- **WHEN** 维护者从任一仓库查找本适配
- **THEN** 能定位唯一权威边界、精确来源、目录、风险、验证状态和回滚方式
