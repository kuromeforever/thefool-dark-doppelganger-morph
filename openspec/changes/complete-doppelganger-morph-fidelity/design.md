# 设计

采用 AOM 已批准的 F3/F4 设计。完整逐节点和备选事实统一引用 [AOM 来源审计](../../../../thefool-forge-1.20.1/openspec/changes/complete-morph-variants-and-doppelganger-fidelity/source-doppelganger.md)，不在子仓重复。

出生准备只处理精确当前 identity 对象或客户端独立未入世界预览，保留原 updater。通过 narrow accessor 设置来源 age=46、复位出生控制器/初始骨骼，不调用完整 tick/AI/onAddedToWorld。

26 项目录为原五项加18 Iron、3 Travel。NATIVE_SESSION 保留来源预检、开始、持续、完成、中断、目标/粒子/伤害/召唤和复施，输入冷却保持60tick。Receipt校验当前identity/MagicData/spell及AOM不透明NativeSession代次，清理仅取消预期收据。

协议2加入carrier UUID/dimension/startedAt/sequence/finishing/castDuration，保留castId STOP。早到START等待当前载体，迟到按elapsed推进精确控制器，旧动作/过期消息不能复活。

Travel五条动画从来源已加载缓存读取，不替换cache或模型资源路径。animatingLegs按来源holder设置并在停止时恢复。资源重载清理附属投影。代码/动作资源不嵌入thinJar。

自动合同证明源码/字节码/资源/状态逻辑与打包，真实首帧、多人、同场Boss、专服和旧存档须另行验收。
