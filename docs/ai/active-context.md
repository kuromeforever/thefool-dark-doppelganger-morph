# 当前上下文

最后更新：2026-08-28

## 当前焦点

`add-dark-doppelganger-localization-and-morph-skills` 已完成实现、自动验证、AOM 文档同步和 PCL安全部署；后续焦点是按实机清单验收客户端、多人、Dedicated Server与旧存档生命周期。

## 当前状态

- 来源 `9.8.2-1.20.1`、SHA-256、技能闭集、统一 60 tick 冷却和跨仓边界已由中文 OpenSpec与合同冻结。
- 附属 `5 suites / 18 tests / 0 failures / 0 errors`、`clean test build`、reobf、thin Jar与 OpenSpec strict 已通过。
- 附属 build/PCL 均为 56,076 字节、SHA-256 `1C87ED3F342AAA640DA82F262C725FA02BFD687B36196F9E28540D5A0C69187A`。
- AOM 相关 `4 suites / 28 tests`、全仓 OpenSpec `373/373` strict 和 `reobfJarJar` 已通过；AOM build/PCL 均为 175,313,495 字节、SHA-256 `91CB2037B3B47FEAA1C267356EBAC578D3C07D8B18CFA298DB37D352F8561144`。
- 两仓身份、分支、差异和进程已经完成精确审计；以仓库身份提交并推送 `main`。

## 证据边界

静态合同与构建不能证明第三人称观感、两人隔离、真实 Dedicated Server或旧存档生命周期；未执行项必须保留为待验收。
