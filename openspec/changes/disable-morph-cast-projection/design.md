## Context

仅停止附属动作网络不足以保证无施法姿态：来源继承 Iron 的 instantCastingPredicate、longCastingPredicate、otherCastingPredicate，另有自己的排队动作控制器。必须覆盖这两条表现路径，但不更改服务器MagicData。

## Decisions

原生会话收据移除视觉字段，伤害与施法完成/取消继续按原生会话代次处理。镜刃与汲取仅删除动作START，保留伤害帧。客户端协议2消息作为兼容空接收器，不缓存/复播迟到START。

精确引用的OWNERS或独立PREVIEWS且非世界实体才允许抑制。来源排队控制器和三个施法谓词返回STOP，清除排队动作及腿部施法锁；变身isAnimating返回false、shouldPointArmsWhileCasting返回false，让来源普通移动/瞄头照常工作。真实Boss、其他法师不经过该条件。

## Risks / Verification

新增注入点由9.8.2与Iron3.15.6来源字节码合同锁定；客户端类仅放client分区，保持既有refmap与thin Jar检查。动作取消后的像素、多人观察、同场Boss与旧存档由用户手动验收，不能用test/build替代。
