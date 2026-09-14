# 变身主动无动作投影

本轮按用户新要求停止26主动的所有强制动作，原生业务、时长和60tick输入CD保留。两处限定客户端Mixin覆盖DDD排队控制器及Iron继承施法控制器，协议2旧START直接丢弃，真实Boss不受影响。

长篇事实与联合交付统一见[AOM实施审计](../../../../thefool-forge-1.20.1/changelog/2026-09-15-生物技能十四项复测实施审计.md)及[验证记录](../../../../thefool-forge-1.20.1/openspec/changes/repair-creature-retest-20260915/verification.md)。本轮不启动游戏，像素与多人验收仍待用户验证。

43项独立测试及主线build通过，版本保持0.1.4。生产提交 `ee467ee` 已按 Kurome4ever 身份推送origin/main，构建件与PCL安装件哈希一致；联合AOM生产提交 `056f98b8c` 已推送。完整包核验、哈希与人工边界以AOM验证记录为准；代理未启动游戏，部署后用户自行启动的进程保持运行。
