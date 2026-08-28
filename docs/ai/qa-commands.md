# QA 命令

最后更新：2026-08-28

```powershell
..\thefool-forge-1.20.1\gradlew.bat -p ..\thefool-forge-1.20.1 --no-daemon compileJava
.\gradlew.bat --no-daemon clean test build
openspec validate add-dark-doppelganger-localization-and-morph-skills --strict
git diff --check
```

`build` 必须包含 reobf 与 `verifyThinJar`。正式发布还需重建 AOM all Jar，用 AOM `tools/deployment/SafeModJarDeployment.psm1` 先执行 `-WhatIf` 再同步 PCL，并核对 build/PCL SHA-256、唯一 mod id和必需条目。

真实客户端、两人、Dedicated Server与不重置存档测试见 `docs/实机验收清单.md`，不能由自动测试替代。
