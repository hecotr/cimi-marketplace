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

### 资产发布（增强版）
- 资产创建和编辑（草稿/发布）
- Markdown 编辑器支持
- 版本管理
- 文件上传功能
- 我的草稿和已发布管理

### 审批流程
- 管理员审批队列
- 批准/拒绝/请求修改
- 审批历史记录
- 状态追踪（草稿、待审批、已发布、已拒绝）

### 管理后台
- 资产管理
- 分类管理（树形结构）
- 统计面板（资产数量、用户数、浏览量等）
- LLM 模型配置管理
- 审批管理

## 技术栈

- **前端**: Vue3 + Vite + TypeScript + Element Plus + Pinia
- **后端**: Java 17 + SpringBoot 2.6.5 + MyBatis Plus + Spring Security + JWT
- **数据库**: PostgreSQL
- **对象存储**: MinIO
- **部署**: Docker + Docker Compose

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

### 资产接口（增强）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/asset | 创建资产 |
| PUT | /api/asset/{id} | 更新资产 |
| POST | /api/asset/{id}/publish | 发布资产 |
| POST | /api/asset/{id}/version | 创建新版本 |
| GET | /api/asset/{id} | 获取资产详情 |
| GET | /api/asset/{id}/version/{version} | 获取指定版本 |
| GET | /api/asset/{id}/versions | 获取版本列表 |
| GET | /api/asset/list | 获取已发布资产 |
| GET | /api/asset/search | 搜索资产 |
| GET | /api/asset/my/drafts | 我的草稿 |
| GET | /api/asset/my/published | 我的已发布 |
| DELETE | /api/asset/{id} | 删除资产 |

### 交互接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/interaction/favorite | 切换收藏 |
| DELETE | /api/interaction/favorite | 取消收藏 |
| GET | /api/interaction/favorites | 获取收藏列表 |
| POST | /api/interaction/like | 切换点赞 |
| GET | /api/interaction/liked | 检查是否点赞 |
| GET | /api/interaction/favorited | 检查是否收藏 |

### 文件接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/file/upload | 上传文件 |
| POST | /api/file/upload/multiple | 批量上传 |
| DELETE | /api/file | 删除文件 |

### 管理员接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/admin/approval/{id}/approve | 审批通过 |
| POST | /api/admin/approval/{id}/reject | 审批拒绝 |
| POST | /api/admin/approval/{id}/request-changes | 请求修改 |
| GET | /api/admin/approval/pending | 待审批列表 |
| GET | /api/admin/statistics/overall | 总体统计 |
| GET | /api/admin/statistics/daily | 每日统计 |
| GET | /api/admin/statistics/category | 分类统计 |
| POST | /api/admin/category | 创建分类 |
| PUT | /api/admin/category/{id} | 更新分类 |
| DELETE | /api/admin/category/{id} | 删除分类 |
| GET | /api/admin/category/tree | 分类树 |
| POST | /api/admin/llm-config | 配置 LLM 模型 |

## 许可证

内部使用
