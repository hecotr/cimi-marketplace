# AI Marketplace

企业内部 AI 能力共享平台，提供 LLM 模型和 Skills（提示词模板）的浏览、测试、下载和互动功能。

## 功能特性

### LLM 模块
- 模型浏览和详情查看
- 在线对话测试
- 参数调优配置（temperature、max_tokens、top_p 等）
- 性能指标展示（响应时间、token 使用量）
- 预设测试用例
- API Key 申请和管理

### Skills 模块
- Skills 资源列表和搜索
- 分类筛选和排序
- 单文件（md）和文件夹（zip）支持
- 查看内容、复制、下载
- 点赞和收藏功能
- 我的收藏列表

## 技术栈

- **前端**: Vue3 + Vite + TypeScript + Element Plus
- **后端**: Java 17 + SpringBoot 2.6.5 + MyBatis Plus
- **数据库**: PostgreSQL
- **部署**: Docker + K8s

## 快速开始

### 前置要求

- Node.js 18+
- Java 17
- Maven 3.6+
- PostgreSQL 15+

### 本地开发

#### 1. 启动数据库

```bash
# 使用 Docker 启动 PostgreSQL
docker run -d \
  --name ai-marketplace-db \
  -e POSTGRES_DB=ai_marketplace \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# 初始化数据库
psql -h localhost -U postgres -d ai_marketplace -f backend/src/main/resources/db/schema.sql
```

#### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:3000

### Docker Compose

```bash
docker-compose up -d
```

访问 http://localhost

## 项目结构

```
ai-marketplace/
├── frontend/          # Vue3 前端
│   ├── src/
│   │   ├── api/      # API 请求
│   │   ├── components/ # 公共组件
│   │   ├── views/    # 页面组件
│   │   └── router/   # 路由配置
│   └── package.json
├── backend/          # SpringBoot 后端
│   ├── src/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── entity/
│   └── pom.xml
└── docker-compose.yml
```

## API 文档

### LLM 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/llm/models | 获取模型列表 |
| GET | /api/llm/models/{id} | 获取模型详情 |
| POST | /api/llm/test | 执行模型测试 |

### Skills 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/skills | 获取 Skills 列表 |
| GET | /api/skills/{id} | 获取 Skill 详情 |
| GET | /api/skills/{id}/download | 下载 Skill |
| POST | /api/skills/{id}/like | 点赞/取消点赞 |
| POST | /api/skills/{id}/favorite | 收藏/取消收藏 |
| GET | /api/skills/favorites | 获取我的收藏列表 |
| GET | /api/skills/categories | 获取分类列表 |

## 许可证

内部使用
