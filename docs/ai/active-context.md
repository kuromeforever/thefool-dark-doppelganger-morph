# 当前上下文

最后更新：2026-08-29

## 当前焦点

当前焦点是发布 `0.1.2` 汉化闭环，修复来源两个进度没有消费既有翻译键的问题，并补齐 `/darkd summon_doppelganger` 四条英文反馈。用户冻结的 `早早汉化补充包` 保持原样。

## 当前状态

- 来源 `9.8.2-1.20.1`、SHA-256、技能闭集、统一 60 tick 冷却和跨仓边界已由中文 OpenSpec与合同冻结。
- `0.1.0` 的实际 PCL 启动在 Mixin apply 阶段因 handler 静态性不匹配失败；`0.1.1` 根因测试已完成修复前失败、修复后通过，实际 PCL冷启动到可响应窗口且原失败族为零。
- `0.1.1` build/PCL均为 56,119 字节、SHA-256 `CA993927B9ECDCFC8A9A62A06DF6067656C64ABD45CE984A168A136213542038`；旧 `0.1.0` 已保存到工作区级回滚目录。
- `0.1.2` 注册强制启用的高优先级内置服务端数据包，只改两个进度的四个展示组件，进度 ID、条件、父子关系和存档进度不变。
- `0.1.2` 把 `/darkd summon_doppelganger` 的四条反馈接入稳定翻译键，16 个 Redirect 目标与 handler 静态性由精确来源合同锁定。
- 附属 `5 suites / 20 tests / 0 failures / 0 errors`、OpenSpec strict、clean test build、reobf 与 thin Jar通过。
- `0.1.2` build/PCL均为 62,423 字节、SHA-256 `0D1F0A0C338E55C7EE28D00F8ADD43DF2F9F8A5EC351C219E472CE3201024495`，PCL只保留一份附属 Jar。
- AOM all Jar已按发布合同重建并安全部署，build/PCL均为 175,504,932 字节、SHA-256 `8BB3FCBDD7AC7A3C85D5DBD4CC3AAAE4F3186A29F295B4ED0A387ADC80EC1025`。
- 用户明确自行执行真实客户端测试，两个进度与召唤命令观感保持待用户验收，自动合同和部署哈希不替代该结论。
- AOM 相关 `4 suites / 28 tests`、全仓 OpenSpec `373/373` strict 和 `reobfJarJar` 已通过；AOM build/PCL 均为 175,313,495 字节、SHA-256 `91CB2037B3B47FEAA1C267356EBAC578D3C07D8B18CFA298DB37D352F8561144`。
- 两仓身份、分支、差异和进程已经完成精确审计；以仓库身份提交并推送 `main`。

## 证据边界

静态合同与构建不能证明第三人称观感、两人隔离、真实 Dedicated Server或旧存档生命周期；未执行项必须保留为待验收。
