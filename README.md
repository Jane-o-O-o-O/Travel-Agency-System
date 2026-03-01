# 时拾纪旅行社管理平台

完整的旅行社管理系统，包含：客户、行程模板、订单、资源、工单等一体化管理

## 📋 系统功能概览

### 核心模块
- **👥 客户管理**：客户信息、标签、跟进记录
- **📋 行程模板**：可复用的行程骨架，包含拖拽编辑
- **🎫 旅游产品**：基于模板的对外售卖产品
- **📦 订单管理**：订单全生命周期（草稿→待确认→已确认→已完成/已取消）
- **🚗 资源管理**：导游、车辆、酒店等资源及冲突检测
- **🔧 工单系统**：客户问题/反馈闭环跟踪
- **📊 统计分析**：订单、用户、资源利用率统计

### 关键特性
- ✅ **强一致性**：订单确认与资源占用写入同事务
- ✅ **冲突检测**：资源时间段重叠自动预警
- ✅ **RBAC权限**：支持管理员、运营、计调、客服、财务角色
- ✅ **Redis缓存**：模板、产品、排行榜等热数据缓存
- ✅ **JWT认证**：前后端分离认证机制

## 🛠 技术栈

| 层级 | 技术 | 版本 |
|------|-----|------|
| **后端** | Spring Boot | 3.2.0 |
| | MyBatis-Plus | 3.5.5 |
| | MySQL | 8.0 |
| | Redis | 7 |
| **前端** | Vue | 3.3.4 |
| | TypeScript | 5.1 |
| | Element Plus | 2.4.0 |
| | Vite | 4.4.9 |
| **部署** | Docker Compose | - |
| | Nginx | Alpine |

## 📁 项目结构

```
旅行社管理平台/
├── backend/                      # Spring Boot后端
│   ├── src/main/java/
│   │   └── com/shishiji/travel/
│   │       ├── model/           # 实体类
│   │       ├── mapper/          # 数据访问
│   │       ├── service/         # 业务逻辑
│   │       ├── controller/      # 控制层
│   │       ├── config/          # 配置
│   │       └── common/          # 公共工具
│   ├── src/main/resources/
│   │   ├── application.yml      # 应用配置
│   │   └── mapper/              # MyBatis SQL
│   ├── pom.xml                  # Maven配置
│   └── Dockerfile
│
├── frontend/                     # Vue3前端
│   ├── src/
│   │   ├── pages/              # 页面组件
│   │   ├── components/         # 通用组件
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具函数
│   │   ├── stores/             # Pinia状态管理
│   │   ├── App.vue
│   │   └── main.ts
│   ├── index.html
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── package.json
│   ├── nginx.conf
│   └── Dockerfile
│
├── database/
│   └── schema.sql               # 数据库初始化脚本
│
├── docker-compose.yml           # 容器编排配置
├── README.md
└── .env                         # 环境变量
```

## 🚀 快速开始

### 方式一：Docker Compose（推荐）

```bash
# 1. 进入项目根目录
cd 旅行社管理平台

# 2. 构建并启动所有服务
docker-compose up --build

# 3. 等待服务启动完成（通常2-3分钟）

# 4. 访问应用
# 前端: http://localhost
# 后端API: http://localhost:8080/api
# API文档: http://localhost:8080/api/doc.html
```

### 方式二：本地开发

#### 后端启动
```bash
# 1. 确保MySQL运行（默认root/123456）
# 2. 导入数据库脚本
mysql -uroot -p123456 < database/schema.sql

# 3. Redis启动（本地或Docker）
docker run -d -p 6379:6379 redis:7-alpine

# 4. 启动Spring Boot
cd backend
mvn clean spring-boot:run
```

#### 前端启动
```bash
# 1. 安装依赖
cd frontend
npm install

# 2. 启动开发服务器
npm run dev

# 3. 自动打开 http://localhost:5173
```

## 📝 默认账户

- **用户名**: admin
- **密码**: admin123
- **角色**: 管理员

## 🔑 API 快速参考

### 认证
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# 返回
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbG...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员"
    }
  }
}
```

### 订单确认（关键接口）
```bash
POST /api/orders/{orderId}/confirm
Authorization: Bearer <token>
Content-Type: application/json

{
  "resources": [
    {
      "resourceId": 1,
      "startTime": "2024-02-27 09:00:00",
      "endTime": "2024-02-27 17:00:00"
    }
  ]
}

# 成功返回true，冲突则返回false并列出冲突资源
```

## 📊 数据库表设计

### 核心表
- `sys_user` - 系统用户
- `sys_role` - 角色
- `sys_permission` - 权限
- `customer` - 客户
- `tag` - 标签
- `itinerary_template` - 行程模板
- `itinerary_template_item` - 模板明细
- `tour_product` - 旅游产品
- `tour_product_item` - 产品行程明细
- `tour_order` - 订单
- `tour_order_item` - 订单行程明细
- `resource` - 资源
- **`resource_booking`** - 资源占用（关键，用于冲突检测）
- `ticket` - 工单

### 关键索引
```sql
-- 冲突检测关键索引
CREATE INDEX idx_booking_resource_time 
ON resource_booking (resource_id, start_time, end_time, status);
```

## 🔍 订单确认流程（事务强一致性）

```
用户点击「确认订单」
    ↓
检索订单详情 + 资源占用列表
    ↓
[事务开始]
    ↓
收集已分配的资源占用
    ↓
FOR EACH 资源:
    - 检测时间段内是否有ACTIVE的占用
    - 如发现冲突，事务回滚，返回冲突列表
    ↓
无冲突 → 批量插入resource_booking (ACTIVE)
    ↓
更新订单状态为CONFIRMED
    ↓
[事务提交]
    ↓
返回成功响应
```

## 📈 性能指标（压测参考）

| 场景 | 目标 | 备注 |
|------|-----|------|
| 模板/产品详情查询 | P95 < 200ms | 启用Redis缓存 |
| 订单列表分页（10条） | P95 < 300ms | 合理索引 |
| 订单确认（无冲突） | P95 < 500ms | 事务 |
| 冲突检测 | P95 < 300ms | SQL优化 |
| 并发订单确认 | 50/100 TPS | 资源锁 |

## 🛡 安全特性

- ✅ JWT Token认证 + RBAC权限
- ✅ 参数校验 + SQL预编译
- ✅ 关键操作日志
- ✅ 防SQL注入、XSS
- ✅ 环境变量敏感信息隔离

## 📚 文档与指南

- [技术文档](../时拾纪旅行社管理平台技术文档.md) - 详细的需求与设计文档
- [API文档](http://localhost:8080/api/doc.html) - Knife4j Swagger文档（服务启动后访问）
- [部署指南](./快速启动指南.md) - 详细部署步骤

## 🤝 常见问题

### Q: 如何修改数据库密码？
A: 修改 `docker-compose.yml` 中的 `MYSQL_ROOT_PASSWORD` 环境变量，同时修改 `application.yml` 中的数据源配置。

### Q: 前端无法连接后端？
A: 
1. 确保后端服务已启动（8080端口）
2. 检查 `frontend/.env` 中的 `VITE_API_BASE_URL` 配置
3. 浏览器F12查看网络请求，确认CORS没有被拦截

### Q: 如何添加新的权限？
A: 在 `sys_permission` 表插入新记录，格式为 `resource:action`（如 `order:cancel`）

### Q: 支持生产部署吗？
A: 支持，建议使用Kubernetes部署，配合RDS、ElastiCache等托管服务

## 📞 技术支持

- 完整功能开发中...
- 欢迎提交Issue
- 联系方式：admin@shishiji.com

## 📄 许可证

内部使用，版权所有

---

**最后更新**: 2024年2月27日  
**版本**: 1.0.0（初版发布）
