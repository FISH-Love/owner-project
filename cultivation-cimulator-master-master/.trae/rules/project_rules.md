### **项目规则（project_rules.md）**  
```markdown
# 项目规则 - 修仙模拟器项目开发
## 一、目录与文件规范
1. **目录结构**：严格遵循既定项目结构（见下方树状图 ），新增文件/目录需归类到对应模块：  

📦 project-root
├─ 📁 node_modules # 依赖库（勿手动修改）
├─ 📁 public # 静态资源（favicon.ico、index.html）
├─ 📁 src # 核心源码
│ ├─ 📁 assets # 需打包的静态资源（图片、全局样式）
│ ├─ 📁 components # 通用 Vue 组件（按钮、弹窗等）
│ ├─ 📁 router # Vue Router 配置（路由规则）
│ ├─ 📁 views # 页面级组件（与路由对应）
│ ├─ 📄 App.vue # 根组件（全局布局）
│ └─ 📄 main.js # 入口文件（Vue 初始化）
├─ 📄 .gitignore # Git 忽略规则
├─ 📄 babel.config.js # Babel 配置
├─ 📄 jsconfig.json # JS 编辑器配置
├─ 📄 package.json # 项目依赖与脚本
├─ 📄 package-lock.json# 依赖版本锁
├─ 📄 README.md # 项目说明文档
└─ 📄 vue.config.js # Vue CLI 配置

2. **文件命名**：  
- Vue 组件：使用 PascalCase（如 `RoleAttributes.vue` ），区分通用组件与页面组件。  
- 后端接口：Java 类名用 PascalCase（如 `UserController.java` ），方法名用 camelCase（如 `getUserInfo` ）。  


## 二、数据库与接口规范
1. **数据库操作**：  
- 使用 JPA 定义 Entity（如 `User.java`、`Role.java` ），映射需求文档中的数据库表结构。  
- 复杂查询优先用 JPA 方法命名查询，特殊场景可自定义 JPQL，确保与 `xianxiudan_schema.sql` 表设计对齐。  

2. **接口开发**：  
- 遵循 RESTful 风格，与需求文档中接口列表（如 `/api/user/register`、`/api/cultivate/chop` ）严格一致。  
- 入参、出参需用 DTO（数据传输对象）封装，避免直接暴露 Entity 敏感字段，示例：  
  ```java
  // 用户注册入参 DTO
  public class UserRegisterDTO {
      private String username;
      private String password;
      // ... Getter/Setter
  }

三、功能实现约束
修炼系统：
砍仙树逻辑：点击触发经验 +1，需记录操作日志（可关联 battle_log 或新增 cultivation_log 表 ）。
渡劫机制：经验阈值、成功率（50%）、失败扣经验逻辑需严格实现，状态变更需同步更新 role 表。
冒险与战斗系统：
地图节点：map_node 表数据需动态加载，节点类型（休息、打怪、商贩、Boss ）需区分处理。
战斗流程：回合制逻辑需清晰，技能释放、大招台词（关联 ultimate_line 表 ）、战斗奖励（经验、金币、遗物 ）需与需求文档一致。
遗物系统：
掉落概率：修炼（凡 4%、上 1%、仙 0.5% ）、冒险（凡 40%、上 10%、仙 5% ）需精确实现，掉落逻辑需关联 relic、user_relic 表。
属性加成：仅加成角色基础属性（而非总属性 ），装备 / 卸下需同步更新 role 表计算后的属性值。
四、非功能需求保障
性能：
接口响应超时控制在 1 秒内，前端页面加载（含资源、接口请求 ）控制在 3 秒内。
高频操作（如砍仙树点击 ）需做节流 / 防抖处理，避免重复请求。
安全：
用户密码需加密存储（如 BCrypt ），接口需添加身份验证（如 JWT ），敏感操作（如装备遗物、渡劫 ）需校验权限。
图形验证码需实现防刷机制（关联 captcha_record 表，校验 used、expire_time ）。
可维护性：
核心业务逻辑（如战斗计算、渡劫判定 ）需抽离为工具类 / 服务层，避免耦合在控制器。
定期备份数据库，异常流程（如战斗失败、渡劫失败 ）需记录日志（可扩展 system_log 表 ）。
五、协同与迭代
分支管理：开发基于 dev 分支，功能模块拆分独立分支（如 feature/register-login、feature/cultivation ），合并前需代码评审。
需求迭代：新增功能需同步更新需求文档、数据库设计、接口文档，确保与代码实现对齐。

数据库设计：

完整文档：
#1. 请保持对话语言为中文
#2. 我的系统为 Windows
#3. 请在生成代码时添加函数级注释

一、项目概述
项目名称：修仙模拟器
项目背景：开发一款基于 Web 的修仙题材模拟游戏，玩家通过修炼、冒险提升角色能力，体验修仙成长过程。
技术栈：SpringBoot + JPA + Vue3 + MySQL
二、功能需求
1. 用户模块
注册登录：支持账号密码注册登录，增加图形验证码验证。
角色绑定：用户首次登录需选择角色并绑定，角色拥有初始属性。
用户信息管理：查看和修改个人基本信息。
2. 角色系统
属性展示：侧边栏实时显示角色 HP、DEF、ATK、修为、Exp、寿元、战力、描述、金币。
属性成长：通过修炼、装备和遗物提升各项属性。
技能系统：每个角色拥有 3 个独特技能（普攻、元素战技、元素爆发），效果随角色而异。
3. 修炼系统
砍仙树：点击屏幕增加经验值，有概率掉落遗物。
渡劫机制：经验值达到阈值可渡劫，成功提升修为，失败减少经验。
遗物系统：掉落不同品质遗物（凡、上、仙），提升角色基础属性。
装备系统：管理背包和已装备的五件装备，装备后提升对应属性。
4. 冒险系统
地图探索：自动前进，随机遇到不同类型节点。
怪物系统：怪物属性随通关进度提升，难度逐渐增加。
BOSS 系统：每 5 关挑战一次 BOSS，掉落更好的遗物和装备。
战斗系统：回合制战斗，玩家可选择技能进行战斗，大招有特殊效果和台词。
战斗奖励：击败敌人后获得经验、金币、遗物和装备。
三、数据库设计
xianxiudan-database
修仙模拟器数据库设计
V3
生成 xianxiudan_schema.sql
四、接口设计
1. 用户相关接口
plaintext
POST /api/user/register - 用户注册
POST /api/user/login - 用户登录(账号密码)
POST /api/user/login/captcha - 图形验证码登录
GET /api/user/info - 获取用户信息
POST /api/user/bindRole - 绑定角色
2. 角色相关接口
plaintext
GET /api/role/info - 获取角色信息
GET /api/role/available - 获取可用角色列表
GET /api/role/skills - 获取角色技能列表
3. 修炼相关接口
plaintext
POST /api/cultivate/chop - 砍仙树增加经验
POST /api/cultivate/tribulation - 进行渡劫
GET /api/cultivate/equipment - 获取装备列表
POST /api/cultivate/equip - 装备物品
POST /api/cultivate/unequip - 卸下物品
POST /api/cultivate/end - 结束修炼
GET /api/cultivate/relics - 获取遗物列表
POST /api/cultivate/equipRelic - 装备遗物
POST /api/cultivate/unequipRelic - 卸下遗物
4. 冒险相关接口
plaintext
POST /api/adventure/start - 开始冒险
GET /api/adventure/status - 获取冒险状态
POST /api/adventure/move - 移动到下一个节点
POST /api/adventure/rest - 在休息节点休息
POST /api/adventure/battle/start - 开始战斗
POST /api/adventure/battle/action - 战斗行动(选择技能)
GET /api/adventure/battle/status - 获取战斗状态
GET /api/adventure/merchant/items - 获取商贩物品列表
POST /api/adventure/merchant/buy - 从商贩购买物品
POST /api/adventure/challengeBoss - 挑战Boss
POST /api/adventure/end - 结束冒险
5. 遗物相关接口
plaintext
GET /api/relic/info - 获取遗物信息
GET /api/relic/drop - 获取遗物掉落概率
五、前端页面设计
1. 登录注册页面
账号密码登录表单
图形验证码组件
注册入口
2. 角色选择页面
角色列表展示
角色详情查看
绑定确认按钮
3. 主页面
侧边栏：角色属性展示
主内容区：修炼和冒险入口
4. 修炼页面
仙树交互区域
经验值和修为进度条
渡劫提示和按钮
装备背包区域
遗物展示区域
结束修炼按钮
5. 冒险页面
地图展示区域
当前节点信息
节点交互按钮
冒险状态提示
6. 战斗页面
角色和怪物头像显示
角色血量和灵力条
技能选择按钮（普攻、元素战技、元素爆发）
战斗日志区域
大招台词显示区域
战斗结果展示
继续战斗 / 返回主页按钮
7. 遗物页面
遗物列表展示
遗物详情查看
装备 / 卸下按钮
六、业务流程
1. 用户注册登录流程
用户访问登录页面
输入账号密码或选择图形验证码登录
系统验证身份
登录成功后检查是否绑定角色
未绑定角色则跳转角色选择页面
2. 修炼流程
用户点击 "自我修炼" 进入修炼页面
点击仙树增加经验值，随机掉落遗物
经验值达到阈值时提示可以渡劫
用户选择渡劫，系统随机判定成功或失败
用户可以从背包中选择装备进行装备
用户可以选择遗物进行装备，提升基础属性
点击 "结束修炼" 返回主页面
3. 冒险流程
用户点击 "出去冒险" 进入冒险页面
系统初始化冒险地图和当前节点
用户在当前节点进行交互（休息、战斗、购买等）
交互完成后移动到下一个节点
遇到 Boss 节点时可以选择挑战
击败 Boss 后冒险成功，获得奖励，难度提升
可以选择返回主页面或开始新的冒险
4. 战斗流程
在战斗节点点击 "战斗" 按钮开始战斗
系统显示角色和怪物信息
玩家选择技能进行攻击
怪物进行反击
系统计算伤害并更新状态
如果玩家选择大招，显示对应角色的台词
战斗结束后，根据结果给予奖励或惩罚
可以选择 "继续战斗" 或 "返回主页"
5. 遗物系统流程
在修炼或冒险过程中随机掉落遗物
玩家可以在遗物页面查看已获得的遗物
选择遗物进行装备，提升对应基础属性
可以卸下遗物，移除属性加成
七、非功能需求
1. 性能需求
页面加载时间不超过 3 秒
接口响应时间不超过 1 秒
支持至少 1000 人同时在线
2. 安全需求
用户密码加密存储
敏感接口需要身份验证
图形验证码防刷机制
数据传输加密
3. 可用性需求
系统 7×24 小时可用
定期数据备份
异常处理和恢复机制
4. 扩展性需求
模块化设计，支持后续功能扩展
配置化设计，便于调整游戏参数

数据库设计：
-- 用户表
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  `role_id` bigint(20) DEFAULT NULL COMMENT '绑定的角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 角色表
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `hp` int(11) NOT NULL DEFAULT '100' COMMENT '生命值',
  `def` int(11) NOT NULL DEFAULT '10' COMMENT '防御力',
  `atk` int(11) NOT NULL DEFAULT '10' COMMENT '攻击力',
  `cultivation` int(11) NOT NULL DEFAULT '1' COMMENT '修为等级',
  `exp` int(11) NOT NULL DEFAULT '0' COMMENT '经验值',
  `life_span` int(11) NOT NULL DEFAULT '100' COMMENT '寿元',
  `combat_power` int(11) NOT NULL DEFAULT '0' COMMENT '战力',
  `gold` int(11) NOT NULL DEFAULT '100' COMMENT '金币',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- 装备表
CREATE TABLE `equipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '装备ID',
  `name` varchar(50) NOT NULL COMMENT '装备名称',
  `quality` tinyint(4) NOT NULL COMMENT '品质(1:凡,2:上,3:仙)',
  `type` tinyint(4) NOT NULL COMMENT '类型(1:武器,2:防具,3:饰品等)',
  `hp_bonus` int(11) DEFAULT '0' COMMENT '生命值加成',
  `def_bonus` int(11) DEFAULT '0' COMMENT '防御力加成',
  `atk_bonus` int(11) DEFAULT '0' COMMENT '攻击力加成',
  `combat_power_bonus` int(11) DEFAULT '0' COMMENT '战力加成',
  `description` varchar(255) DEFAULT NULL COMMENT '装备描述',
  `drop_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '掉落概率(%)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_quality` (`quality`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装备信息表';

-- 用户装备表
CREATE TABLE `user_equipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `equipment_id` bigint(20) NOT NULL COMMENT '装备ID',
  `is_equipped` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否装备(0:未装备,1:已装备)',
  `position` tinyint(4) DEFAULT NULL COMMENT '装备位置(1-5)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_equipment` (`user_id`,`equipment_id`),
  KEY `idx_equipment_id` (`equipment_id`),
  KEY `idx_is_equipped` (`is_equipped`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户装备表';

-- 地图节点表
CREATE TABLE `map_node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '节点ID',
  `map_id` bigint(20) NOT NULL COMMENT '地图ID',
  `type` tinyint(4) NOT NULL COMMENT '节点类型(1:休息,2:普通怪,3:精英怪,4:商贩,5:Boss)',
  `position` int(11) NOT NULL COMMENT '节点位置',
  `monster_id` bigint(20) DEFAULT NULL COMMENT '怪物ID(如果是战斗节点)',
  `merchant_id` bigint(20) DEFAULT NULL COMMENT '商贩ID(如果是商贩节点)',
  `description` varchar(255) DEFAULT NULL COMMENT '节点描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_map_id` (`map_id`),
  KEY `idx_type` (`type`),
  KEY `idx_position` (`position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图节点表';

-- 冒险记录表
CREATE TABLE `adventure_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `map_id` bigint(20) NOT NULL COMMENT '地图ID',
  `current_node_id` bigint(20) DEFAULT NULL COMMENT '当前节点ID',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态(0:进行中,1:已完成,2:已失败)',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_map_id` (`map_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冒险记录表';

-- 怪物表
CREATE TABLE `monster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '怪物ID',
  `name` varchar(50) NOT NULL COMMENT '怪物名称',
  `type` tinyint(4) NOT NULL COMMENT '类型(1:普通怪,2:精英怪,3:Boss)',
  `min_hp` int(11) NOT NULL COMMENT '最小生命值',
  `max_hp` int(11) NOT NULL COMMENT '最大生命值',
  `min_def` int(11) NOT NULL COMMENT '最小防御力',
  `max_def` int(11) NOT NULL COMMENT '最大防御力',
  `min_atk` int(11) NOT NULL COMMENT '最小攻击力',
  `max_atk` int(11) NOT NULL COMMENT '最大攻击力',
  `exp_reward` int(11) NOT NULL COMMENT '经验奖励',
  `gold_reward` int(11) NOT NULL COMMENT '金币奖励',
  `description` varchar(255) DEFAULT NULL COMMENT '怪物描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='怪物信息表';

-- 渡劫记录表
CREATE TABLE `tribulation_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `success` tinyint(4) NOT NULL COMMENT '是否成功(0:失败,1:成功)',
  `old_cultivation` int(11) NOT NULL COMMENT '渡劫前修为',
  `new_cultivation` int(11) DEFAULT NULL COMMENT '渡劫后修为(成功时)',
  `old_exp` int(11) NOT NULL COMMENT '渡劫前经验',
  `new_exp` int(11) NOT NULL COMMENT '渡劫后经验',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '渡劫时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_success` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渡劫记录表';

-- 验证码记录表
CREATE TABLE `captcha_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `captcha_key` varchar(50) NOT NULL COMMENT '验证码key',
  `captcha_code` varchar(10) NOT NULL COMMENT '验证码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已使用(0:未使用,1:已使用)',
  PRIMARY KEY (`id`),
  KEY `idx_captcha_key` (`captcha_key`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_used` (`used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码记录表';

-- 技能表
CREATE TABLE `skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '技能ID',
  `name` varchar(50) NOT NULL COMMENT '技能名称',
  `type` tinyint(4) NOT NULL COMMENT '技能类型(1:普攻,2:元素战技,3:元素爆发)',
  `damage` int(11) NOT NULL COMMENT '伤害值',
  `mp_cost` int(11) NOT NULL COMMENT '消耗灵力',
  `cooldown` int(11) NOT NULL COMMENT '冷却时间(回合)',
  `description` varchar(255) DEFAULT NULL COMMENT '技能描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能信息表';

-- 角色技能关联表
CREATE TABLE `role_skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `skill_id` bigint(20) NOT NULL COMMENT '技能ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_role_skill` (`role_id`,`skill_id`),
  KEY `idx_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色技能关联表';

-- 大招台词表
CREATE TABLE `ultimate_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `line` varchar(255) NOT NULL COMMENT '台词内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大招台词表';

-- 战斗记录表
CREATE TABLE `battle_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `monster_id` bigint(20) NOT NULL COMMENT '怪物ID',
  `adventure_id` bigint(20) DEFAULT NULL COMMENT '冒险ID',
  `is_win` tinyint(4) NOT NULL COMMENT '是否胜利(0:失败,1:胜利)',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_monster_id` (`monster_id`),
  KEY `idx_adventure_id` (`adventure_id`),
  KEY `idx_is_win` (`is_win`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='战斗记录表';

-- 战斗日志表
CREATE TABLE `battle_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `battle_id` bigint(20) NOT NULL COMMENT '战斗ID',
  `round` int(11) NOT NULL COMMENT '回合数',
  `action_type` tinyint(4) NOT NULL COMMENT '动作类型(1:玩家攻击,2:怪物攻击,3:使用技能,4:触发特效)',
  `action_detail` varchar(255) NOT NULL COMMENT '动作详情',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_battle_id` (`battle_id`),
  KEY `idx_round` (`round`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='战斗日志表';

-- 遗物表
CREATE TABLE `relic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '遗物ID',
  `name` varchar(50) NOT NULL COMMENT '遗物名称',
  `quality` tinyint(4) NOT NULL COMMENT '品质(1:凡,2:上,3:仙)',
  `attribute` varchar(20) NOT NULL COMMENT '加成属性(HP,DEF,ATK,EXP,LIFE_SPAN)',
  `bonus_percent` decimal(5,2) NOT NULL COMMENT '加成百分比',
  `description` varchar(255) DEFAULT NULL COMMENT '遗物描述',
  `adventure_drop_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '冒险掉落概率(%)',
  `cultivation_drop_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '修炼掉落概率(%)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_quality` (`quality`),
  KEY `idx_attribute` (`attribute`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='遗物信息表';

-- 用户遗物表
CREATE TABLE `user_relic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `relic_id` bigint(20) NOT NULL COMMENT '遗物ID',
  `is_equipped` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否装备(0:未装备,1:已装备)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_relic` (`user_id`,`relic_id`),
  KEY `idx_relic_id` (`relic_id`),
  KEY `idx_is_equipped` (`is_equipped`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户遗物表';

-- 游戏难度表
CREATE TABLE `game_difficulty` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `boss_count` int(11) NOT NULL DEFAULT '0' COMMENT '击败BOSS数量',
  `min_hp` int(11) NOT NULL DEFAULT '0' COMMENT '怪物最小生命值',
  `max_hp` int(11) NOT NULL DEFAULT '50' COMMENT '怪物最大生命值',
  `min_def` int(11) NOT NULL DEFAULT '0' COMMENT '怪物最小防御力',
  `max_def` int(11) NOT NULL DEFAULT '50' COMMENT '怪物最大防御力',
  `min_atk` int(11) NOT NULL DEFAULT '0' COMMENT '怪物最小攻击力',
  `max_atk` int(11) NOT NULL DEFAULT '50' COMMENT '怪物最大攻击力',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_boss_count` (`boss_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏难度信息表';