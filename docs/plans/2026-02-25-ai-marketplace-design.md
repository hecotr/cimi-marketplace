# AI Marketplace 设计文档

## 概述

企业内部 AI 能力共享平台，提供 LLM 模型和 Skills（提示词模板）的浏览、测试、下载和互动功能。

**目标用户**：企业内部员工
**使用场景**：内部资源共享
**部署环境**：内部 K8s/容器

## 技术架构

### 技术栈

- **前端**：Vue3 + Vite + TypeScript + Element Plus
- **后端**：Java SpringBoot 2.6.5 + Spring Security + MyBatis Plus
- **数据库**：PostgreSQL
- **部署**：Docker + K8s

### 架构图

```
┌─────────────┐
│   前端层     │  Vue3 + Element Plus
└──────┬──────┘
       │
┌──────▼──────┐
│   Nginx     │  反向代理
└──────┬──────┘
       │
┌──────▼──────┐
│  SpringBoot │  RESTful API
│   后端层     │  业务逻辑、数据持久化
└──────┬──────┘
       │
┌──────▼──────┐
│ PostgreSQL  │  数据存储
└─────────────┘
```

### 核心模块

1. **LLM 浏览** - 模型列表、详情查看
2. **LLM 测试调试** - 对话式测试、参数调优配置、性能指标展示、预设用例
3. **LLM API Key 申请** - 申请流程、审批回调、Key 管理
4. **Skills 浏览搜索** - 资源列表、分类、搜索、浏览量统计
5. **Skills 安装** - 查看、复制、下载、下载量统计
6. **Skills 互动** - 点赞、收藏功能、个人收藏列表

## 数据库设计

### 核心数据表

#### llm_model - LLM 模型表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar | 模型名称 |
| provider | varchar | 提供商 |
| model_name | varchar | 模型标识 |
| description | text | 描述 |
| api_endpoint | varchar | API 端点 |
| max_tokens | int | 最大 token 数 |
| status | varchar | 状态 (active/inactive) |
| created_at | timestamp | 创建时间 |
| updated_at | timestamp | 更新时间 |

#### llm_test_case - LLM 预设测试用例表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| model_id | bigint | 模型 ID |
| name | varchar | 用例名称 |
| prompt | text | 提示词 |
| expected_output | text | 预期输出 |
| created_at | timestamp | 创建时间 |

#### llm_test_record - LLM 测试记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| model_id | bigint | 模型 ID |
| prompt | text | 提示词 |
| response | text | 模型响应 |
| parameters | json | 请求参数 |
| response_time | int | 响应时间 (ms) |
| token_usage | json | Token 使用量 |
| created_at | timestamp | 创建时间 |

#### api_key - API Key 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| model_id | bigint | 模型 ID |
| key_value | varchar | Key 值 |
| status | varchar | 状态 (pending/approved/rejected/expired) |
| apply_time | timestamp | 申请时间 |
| approve_time | timestamp | 审批时间 |
| expiry_date | timestamp | 过期日期 |

#### skill - Skills 资源表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar | Skill 名称 |
| description | text | 描述 |
| category | varchar | 分类 |
| type | varchar | 类型 (file/folder) |
| content | text | 内容 (单文件) |
| storage_path | varchar | 存储路径 (文件夹) |
| view_count | int | 浏览量 |
| download_count | int | 下载量 |
| like_count | int | 点赞数 |
| created_by | bigint | 创建人 ID |
| created_at | timestamp | 创建时间 |

#### skill_like - Skills 点赞记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| skill_id | bigint | Skill ID |
| created_at | timestamp | 创建时间 |

#### skill_favorite - Skills 收藏记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| skill_id | bigint | Skill ID |
| created_at | timestamp | 创建时间 |

#### user - 用户表（认证预留）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| username | varchar | 用户名 |
| email | varchar | 邮箱 |
| department | varchar | 部门 |
| created_at | timestamp | 创建时间 |

## LLM 模块设计

### LLM 浏览功能

- **模型列表页**：展示所有可用模型，包含模型名称、提供商、描述、状态标签
- **模型详情页**：展示模型详细信息，包括参数限制、支持的参数类型、使用说明

### LLM 测试调试功能

- **对话式测试**：输入框发送 prompt，实时显示模型响应，支持多轮对话
- **参数调优**：侧边栏配置参数（temperature、max_tokens、top_p、frequency_penalty 等），实时应用
- **性能指标**：显示响应时间、输入/输出 token 数量、预估成本
- **预设用例**：提供预设测试场景，一键加载 prompt，快速验证模型效果

### LLM API Key 申请

- **申请表单**：选择模型、填写申请理由、设置预计使用量
- **申请状态**：待审批、已批准、已拒绝
- **审批回调**：预留接口，接收企业内部审批系统的回调，批准后自动下发 API Key
- **我的 Key 管理**：查看已申请的 Key、状态、过期时间、使用情况

## Skills 模块设计

### Skills 浏览搜索

- **资源列表页**：卡片式展示 Skills，包含名称、描述、类别、浏览量、下载量、点赞数
- **分类筛选**：按类别筛选（如代码生成、文档编写、数据分析等）
- **搜索功能**：支持按名称、描述关键词搜索
- **排序**：按时间、浏览量、下载量、点赞数排序
- **浏览量统计**：用户访问详情页时自动累加浏览量

### Skills 安装

- **详情页**：展示 Skill 完整信息，包括使用说明、依赖要求、版本信息
- **单文件（md）**：
  - 代码高亮展示
  - 一键复制按钮
  - 下载 md 文件
- **文件夹**：
  - 文件结构预览
  - 下载 zip 压缩包
- **下载量统计**：用户点击下载时自动累加下载量

### Skills 互动

- **点赞**：点击爱心按钮点赞/取消点赞，实时显示点赞数
- **收藏**：点击星标按钮收藏/取消收藏
- **我的收藏**：个人中心查看已收藏的 Skills 列表

## API 设计

### LLM 相关接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/llm/models | 获取模型列表 |
| GET | /api/llm/models/{id} | 获取模型详情 |
| POST | /api/llm/test | 执行模型测试 |
| GET | /api/llm/test-cases/{modelId} | 获取预设用例 |
| POST | /api/llm/api-key/apply | 申请 API Key |
| GET | /api/llm/api-keys | 获取我的 Key 列表 |
| POST | /api/llm/approve | 审批回调接口 |

### Skills 相关接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/skills | 获取 Skills 列表（支持分页、搜索、筛选、排序） |
| GET | /api/skills/{id} | 获取 Skill 详情 |
| GET | /api/skills/{id}/download | 下载 Skill（md 或 zip） |
| POST | /api/skills/{id}/like | 点赞/取消点赞 |
| POST | /api/skills/{id}/favorite | 收藏/取消收藏 |
| GET | /api/skills/favorites | 获取我的收藏列表 |
| GET | /api/skills/categories | 获取分类列表 |

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 前端页面结构

### 页面路由结构

```
/                          - 首页（导航到 LLM 或 Skills）
/llm                       - LLM 模块入口
/llm/models                - LLM 模型列表
/llm/models/:id            - LLM 模型详情/测试页
/llm/my-keys               - 我的 API Key
/skills                    - Skills 模块入口
/skills                    - Skills 列表页
/skills/:id                - Skills 详情页
/favorites                 - 我的收藏
```

### 布局结构

- **顶部导航**：Logo、LLM、Skills、我的收藏
- **主内容区**：根据路由展示不同页面
- **LLM 页面布局**：
  - 左侧：对话测试区
  - 右侧：参数配置面板、性能指标、预设用例
- **Skills 页面布局**：
  - 顶部：搜索框、分类筛选、排序
  - 中间：卡片网格展示
  - 详情页：左侧内容区，右侧信息统计和操作按钮

## 部署和开发环境

### 项目结构

```
ai-marketplace/
├── frontend/              # Vue3 前端
│   ├── src/
│   │   ├── api/          # API 请求
│   │   ├── components/   # 公共组件
│   │   ├── views/        # 页面组件
│   │   └── router/       # 路由配置
│   ├── package.json
│   └── Dockerfile
└── backend/              # SpringBoot 后端
    ├── src/
    │   ├── controller/   # 控制器
    │   ├── service/      # 业务逻辑
    │   ├── mapper/       # 数据访问
    │   └── entity/       # 实体类
    ├── pom.xml
    └── Dockerfile
```

### Docker 部署

- **前端镜像**：Nginx 静态资源托管
- **后端镜像**：OpenJDK 17 + SpringBoot JAR
- **数据库**：PostgreSQL 官方镜像
- **配置**：通过 K8s ConfigMap 管理数据库连接、API 配置

### 开发环境

- **前端**：Node.js 18+, pnpm/npm，本地开发 `npm run dev`
- **后端**：JDK 17, Maven 3.6+，本地开发 `mvn spring-boot:run`
- **数据库**：本地 PostgreSQL 或 Docker 容器
