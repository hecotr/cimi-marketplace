# AI Marketplace

企业级 AI 能力共享平台，支持 LLM 模型和 Skills（Prompt 模板）的管理、发布、审核和使用。

## 功能特性

### 用户功能
- **Skills 管理**: 发布、编辑、删除 Prompt 模板
- **LLM 模型浏览**: 查看可用的 LLM 模型配置
- **收藏与点赞**: 收藏感兴趣的资产，点赞优质内容
- **API Key 申请**: 申请访问 LLM 模型的 API Key
- **草稿管理**: 保存草稿，随时继续编辑

### 管理员功能
- **分类管理**: 管理资产分类
- **审核管理**: 审核用户提交的内容
- **LLM 配置**: 配置 LLM 模型参数
- **API Key 审批**: 审批用户的 API Key 申请
- **数据统计**: 查看平台使用统计

## 技术栈

### 后端
- Java 17
- Spring Boot 2.6.5
- MyBatis Plus 3.5.3
- PostgreSQL
- MinIO / S3 (文件存储)
- Spring Security (密码加密)

### 前端
- Vue 3.4
- TypeScript 5.4
- Vite 5
- Element Plus 2.5
- Pinia (状态管理)
- Vue Router 4
- Axios

## 项目结构

```
ai-marketplace/
├── backend/                    # 后端服务
│   ├── src/main/java/
│   │   └── com/aimarketplace/
│   │       ├── common/         # 通用类 (Result, Exception)
│   │       ├── config/         # 配置类 (Security, CORS, MinIO)
│   │       ├── controller/     # 控制器
│   │       ├── dto/            # 数据传输对象
│   │       ├── entity/         # 实体类
│   │       ├── mapper/         # MyBatis Mapper
│   │       └── service/        # 服务层
│   └── src/main/resources/
│       ├── application.yml     # 应用配置
│       └── db/                 # 数据库脚本
│           ├── schema.sql      # 表结构
│           └── init-data.sql   # 初始数据
│
├── frontend/                   # 前端应用
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── components/        # 组件
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态
│   │   ├── views/             # 页面视图
│   │   └── main.ts            # 入口文件
│   └── package.json
│
└── docs/                       # 文档
    └── plans/                  # 实现计划
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- PostgreSQL 13+
- MinIO (可选，用于文件存储)

### 后端启动

```bash
cd backend

# 配置数据库连接 (修改 application.yml)
# 或使用环境变量:
# export DB_HOST=localhost
# export DB_PORT=5432
# export DB_NAME=ai_marketplace
# export DB_USERNAME=postgres
# export DB_PASSWORD=postgres

# 初始化数据库
psql -U postgres -c "CREATE DATABASE ai_marketplace;"
psql -U postgres -d ai_marketplace -f src/main/resources/db/schema.sql
psql -U postgres -d ai_marketplace -f src/main/resources/db/init-data.sql

# 启动服务
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:5173 启动

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin | 管理员 |
| testuser | 123456 | 普通用户 |

## API 接口

### 认证
- `POST /api/auth/login` - 登录
- `POST /api/auth/logout` - 登出
- `GET /api/auth/me` - 获取当前用户

### 资产
- `GET /api/assets` - 获取资产列表
- `GET /api/assets/{id}` - 获取资产详情
- `POST /api/assets` - 创建资产
- `PUT /api/assets/{id}` - 更新资产
- `DELETE /api/assets/{id}` - 删除资产
- `POST /api/assets/{id}/submit` - 提交审核
- `GET /api/assets/my` - 获取我的资产

### LLM 模型
- `GET /api/llm/models` - 获取模型列表
- `GET /api/llm/models/{id}` - 获取模型详情
- `POST /api/llm/test` - 测试模型

### 分类
- `GET /api/categories` - 获取分类列表

### 交互
- `POST /api/interactions/favorite` - 收藏
- `DELETE /api/interactions/favorite` - 取消收藏
- `POST /api/interactions/like` - 点赞
- `DELETE /api/interactions/like` - 取消点赞

### 管理员
- `GET /api/admin/assets/pending` - 获取待审核资产
- `POST /api/admin/assets/{id}/approve` - 批准资产
- `POST /api/admin/assets/{id}/reject` - 拒绝资产

## 数据库表结构

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| category | 分类表 |
| asset | 资产表 (LLM/Skill) |
| asset_version | 资产版本表 |
| llm_model_config | LLM 模型配置表 |
| llm_test_record | LLM 测试记录表 |
| api_key | API Key 表 |
| approval_record | 审核记录表 |
| favorite | 收藏表 |
| like_record | 点赞记录表 |
| llm_usage_log | LLM 使用日志表 |

## 环境变量配置

### 后端

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| DB_HOST | 10.254.254.103 | 数据库地址 |
| DB_PORT | 5432 | 数据库端口 |
| DB_NAME | ai_marketplace | 数据库名称 |
| DB_USERNAME | postgres | 数据库用户名 |
| DB_PASSWORD | postgres | 数据库密码 |
| MINIO_ENDPOINT | http://10.254.254.103:9000 | MinIO 地址 |
| MINIO_ACCESS_KEY | minioadmin | MinIO Access Key |
| MINIO_SECRET_KEY | minioadmin | MinIO Secret Key |
| MINIO_BUCKET | ai-marketplace | MinIO Bucket |

## 构建部署

### 后端构建
```bash
cd backend
mvn clean package -DskipTests
java -jar target/ai-marketplace-1.0.0.jar
```

### 前端构建
```bash
cd frontend
npm run build
# 生成的文件在 dist/ 目录
```

## License

MIT License
