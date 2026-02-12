# 供应商质量管理系统 (SQMS) - 运行说明

## 项目简介

基于低代码思想的供应商质量管理系统（Supplier Quality Management System），面向中小型制造企业，实现供应商信息管理、采购与质量记录管理、供应商质量评分、低评分预警提醒、数据可视化辅助决策等功能。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Spring Security + JWT
- Lombok

### 前端
- Vue 3.4.0
- Vite 5.0.0
- Element Plus 2.5.0
- Axios 1.6.5
- Pinia 2.1.7
- ECharts 5.4.3

## 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

## 数据库配置

### 1. 创建数据库

```sql
-- 使用提供的SQL脚本创建数据库和表
-- 文件位置: BackEnd/BackEnd/src/main/resources/schema.sql
```

### 2. 导入数据

```bash
# 方式1: 使用MySQL命令行
mysql -u root -p < BackEnd/BackEnd/src/main/resources/schema.sql

# 方式2: 使用Navicat等工具
# 打开schema.sql文件，执行所有SQL语句
```

### 3. 修改数据库连接配置

编辑 `BackEnd/BackEnd/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sqms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

## 后端启动

### 1. 安装依赖

```bash
cd BackEnd/BackEnd
mvn clean install
```

### 2. 启动后端服务

```bash
mvn spring-boot:run
```

或使用IDE运行 `BackEndApplication.java`

后端服务将在 `http://localhost:8080` 启动

## 前端启动

### 1. 安装依赖

```bash
cd FrontEnd
npm install
```

### 2. 启动前端服务

```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## 默认测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 采购员 | purchaser | 123456 |
| 质检员 | inspector | 123456 |

## 系统功能模块

### 1. 用户与权限模块
- 登录/退出
- JWT认证
- 基于角色的接口权限控制
- 管理员、采购员、质检员三种角色

### 2. 供应商管理模块
- 供应商信息CRUD
- 分页查询
- 条件筛选（关键词、分类、等级）
- 供应商等级管理（A/B/C）

### 3. 采购与质量记录模块
- 采购记录CRUD
- 检验结果记录（合格/不合格）
- 不合格原因记录
- 延迟交货检测
- 付款状态管理

### 4. 供应商评分模块
- 自动计算评分（合格率、准时率、服务评分）
- 评分公式：总分 = 合格率×权重 + 准时率×权重 + 服务评分×权重
- 评分历史记录
- 风险供应商自动标记（评分<60）

### 5. 评分规则低代码配置（创新点）
- 管理员可修改评分权重
- 修改后自动影响评分计算
- 评分逻辑从数据库读取权重
- 体现"低代码思想"

### 6. 预警模块
- 风险供应商列表（评分<60）
- 列表红色高亮显示
- 首页显示预警数量
- 风险供应商详情查看

### 7. 数据可视化模块
- 供应商评分排行榜（柱状图）
- 合格率趋势（折线图）
- 各供应商质量对比图
- 适合答辩展示

### 8. 首页仪表盘
- 统计卡片（风险供应商、供应商总数、采购记录、合格记录）
- 供应商评分TOP5
- 快捷入口
- 近期采购记录

## 项目结构

### 后端结构
```
BackEnd/BackEnd/src/main/java/com/backend/
├── config/              # 配置类
│   ├── CorsConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── controller/          # 控制器
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── PurchaseRecordController.java
│   ├── ScoreRuleController.java
│   ├── SupplierController.java
│   └── SupplierScoreController.java
├── entity/              # 实体类
│   ├── PurchaseRecord.java
│   ├── Role.java
│   ├── ScoreRule.java
│   ├── Supplier.java
│   ├── SupplierScore.java
│   └── User.java
├── mapper/              # 数据访问层
│   ├── PurchaseRecordMapper.java
│   ├── RoleMapper.java
│   ├── ScoreRuleMapper.java
│   ├── SupplierMapper.java
│   ├── SupplierScoreMapper.java
│   └── UserMapper.java
├── service/             # 业务逻辑层
│   ├── PurchaseRecordService.java
│   ├── ScoreRuleService.java
│   ├── SupplierScoreService.java
│   ├── SupplierService.java
│   └── UserService.java
├── util/                # 工具类
│   └── JwtUtil.java
├── vo/                  # 视图对象
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── PageResult.java
│   └── Result.java
├── common/              # 公共类
│   ├── CorsConfig.java
│   └── MyMetaObjectHandler.java
└── BackEndApplication.java
```

### 前端结构
```
FrontEnd/src/
├── api/                 # API接口
│   ├── auth.js
│   ├── dashboard.js
│   ├── purchaseRecord.js
│   ├── scoreRule.js
│   ├── supplier.js
│   └── supplierScore.js
├── layout/              # 布局组件
│   └── MainLayout.vue
├── router/              # 路由配置
│   └── index.js
├── stores/              # 状态管理
│   └── user.js
├── utils/               # 工具函数
│   ├── request.js
│   └── token.js
├── views/               # 页面组件
│   ├── Charts.vue
│   ├── Dashboard.vue
│   ├── Login.vue
│   ├── PurchaseRecord.vue
│   ├── ScoreRule.vue
│   ├── Supplier.vue
│   ├── SupplierScore.vue
│   └── Warning.vue
├── App.vue
└── main.js
```

## API接口文档

### 认证接口
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/logout` - 用户退出
- GET `/api/auth/info` - 获取用户信息

### 供应商接口
- GET `/api/suppliers` - 获取供应商列表
- GET `/api/suppliers/all` - 获取所有供应商
- GET `/api/suppliers/{id}` - 获取供应商详情
- POST `/api/suppliers` - 创建供应商
- PUT `/api/suppliers/{id}` - 更新供应商
- DELETE `/api/suppliers/{id}` - 删除供应商

### 采购记录接口
- GET `/api/purchase-records` - 获取采购记录列表
- GET `/api/purchase-records/{id}` - 获取采购记录详情
- POST `/api/purchase-records` - 创建采购记录
- PUT `/api/purchase-records/{id}` - 更新采购记录
- DELETE `/api/purchase-records/{id}` - 删除采购记录

### 供应商评分接口
- GET `/api/supplier-scores` - 获取评分列表
- GET `/api/supplier-scores/risk` - 获取风险供应商
- GET `/api/supplier-scores/risk-count` - 获取风险数量
- POST `/api/supplier-scores/calculate` - 计算评分
- POST `/api/supplier-scores` - 创建评分
- PUT `/api/supplier-scores/{id}` - 更新评分
- DELETE `/api/supplier-scores/{id}` - 删除评分

### 评分规则接口
- GET `/api/score-rules` - 获取评分规则列表
- GET `/api/score-rules/{id}` - 获取规则详情
- POST `/api/score-rules` - 创建评分规则
- PUT `/api/score-rules/{id}` - 更新评分规则
- PUT `/api/score-rules/{id}/weight` - 更新规则权重
- DELETE `/api/score-rules/{id}` - 删除评分规则

### 仪表盘接口
- GET `/api/dashboard/stats` - 获取统计数据
- GET `/api/dashboard/score-ranking` - 获取评分排名
- GET `/api/dashboard/quality-trend` - 获取质量趋势
- GET `/api/dashboard/supplier-comparison` - 获取供应商对比

## 创新点说明

### 低代码评分规则配置
系统支持管理员通过界面动态配置评分权重，无需修改代码即可调整评分算法。评分逻辑从数据库读取权重配置，修改后自动影响后续评分计算，体现了低代码思想。

### 自动风险预警
系统自动计算供应商评分，当评分低于60分时自动标记为风险供应商，并在首页和预警页面高亮显示，帮助企业及时发现质量问题。

## 答辩要点

1. **系统架构**: 采用前后端分离架构，后端使用Spring Boot，前端使用Vue 3
2. **数据库设计**: 合理的表结构设计，包含用户、角色、供应商、采购记录、评分规则等表
3. **权限控制**: 基于Spring Security + JWT实现用户认证和授权
4. **低代码思想**: 评分规则可配置，管理员可动态调整评分权重
5. **数据可视化**: 使用ECharts实现多种图表展示，适合答辩演示
6. **实用功能**: 包含供应商管理、采购记录、评分计算、预警等完整功能

## 常见问题

### 1. 后端启动失败
- 检查JDK版本是否为17+
- 检查MySQL是否已启动
- 检查数据库连接配置是否正确

### 2. 前端启动失败
- 检查Node.js版本是否为18+
- 删除node_modules重新安装依赖
- 检查端口3000是否被占用

### 3. 数据库连接失败
- 确认MySQL服务已启动
- 确认数据库名、用户名、密码正确
- 确认MySQL端口为3306

### 4. 登录失败
- 确认已导入schema.sql初始化数据
- 确认使用正确的用户名和密码
- 检查后端服务是否正常启动

## 项目特点

1. **简单实用**: 功能完整但不复杂，适合毕设展示
2. **代码规范**: 分层清晰，注释适量，便于讲解
3. **界面美观**: 使用Element Plus组件库，界面整洁
4. **创新点突出**: 低代码评分规则配置是亮点
5. **易于运行**: 提供完整的运行说明和测试数据

## 联系方式

如有问题，请联系开发者。
