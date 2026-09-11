## ADDED Requirements

### Requirement: 接受当前正式AOM0.3.0
附属 SHALL 声明AOM范围`[0.3.0,0.4.0)`并使用当前正式AOM公开API编译，不改变自身技能或来源依赖。

#### Scenario: 联合发布
- **WHEN** 当前AOM0.3.0与重建附属同时加载
- **THEN** AOM版本范围接受0.3.0并保留原有薄Jar边界
