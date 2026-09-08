# QA 命令

最后更新：2026-09-08。

AOM 对应构建先提供已编译 mapped classes。本仓只读消费，不触发根构建。

```powershell
.\gradlew.bat --no-daemon '-Paom_classes_dir=D:/develop/project-mc/TheFool/.codex-build/morph-variants-0908/aom/build/classes/java/main' test build
openspec validate complete-doppelganger-morph-fidelity --strict
git diff --check
```

常规父仓已完成 compileJava 时可省略 aom_classes_dir。输出 build/libs/thefool_dark_doppelganger_morph-0.1.4.jar，XML 为 build/test-results/test/TEST-*.xml。build 必须含 reobf、verifyThinJar。

每轮确认自己的 Gradle/Java 退出，不停止用户或其他任务进程。两仓正式发布由主任务统一完成 AOM all、安全同步、哈希和 mod id 核对。本轮附属代理未部署。真实验收见 docs/实机验收清单.md。
