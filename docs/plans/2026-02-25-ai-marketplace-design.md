# AI Marketplace 增强版设计文档

## 概述

企业内部 AI 能力共享平台，提供 LLM 模型和 Skills（提示词模板）的管理、发布、审核、使用功能。

**目标用户**：企业内部员工
**使用场景**：内部资源共享
**部署环境**：内部 K8s/容器

**重要说明**：
1. LLM 模型由专门的"大模型负责人员"在后台配置，不开放给普通用户发布
2. 前端美化风格使用 frontend-design skill 进行美化，确保 UI/UX 体验

---

## 零、项目配置与初始化数据

### 环境配置

#### 数据库配置
- **地址**：`10.254.254.103:5432`
- **用户/密码**：`postgres/postgres`
- **数据库名**：`ai-marketplace`

#### 对象存储配置
| 环境 | 类型 | 地址 | Bucket |
|------|------|------|--------|
| 测试 | MinIO | `10.254.254.103:9001` | `ai-marketplace` |
| 生产 | S3 | 可配置 | - |

支持通过配置切换 MinIO/S3。

### 初始化数据

#### 1. 用户初始化
```sql
INSERT INTO user (username, password, role) VALUES ('admin', '$2a$10$...', 'admin');
```
- 账号：`admin` / `admin`
- 仅初始化管理员账号

#### 2. LLM 模型初始化
预置模型：
| 模型名称 | 模型标识 | 提供商 | 协议 | 分类 |
|---------|---------|-------|------|------|
| GLM-5 | glm-5 | 智谱AI | OpenAI | 通用对话 |
| GLM-4.7 | glm-4.7 | 智谱AI | OpenAI | 通用对话 |

#### 3. 分类初始化
**LLM 分类**：
- 通用对话
- 代码生成
- 数据分析
- 图像理解

**Skills 分类**：
- 文案写作
- 数据分析
- 代码辅助
- 翻译
- 总结摘要

### 业务规则

#### 1. 用户认证
- 简单的用户名/密码登录页面
- 不使用 JWT/SSO 等复杂认证
- 前端通过用户 ID 判断是否有管理员权限

#### 2. API Key 管理
- **有效期选项**：三个月（默认）、六个月、一年、永久
- **支持协议**：OpenAI、Anthropic
- **审批流程**：管理员在审批页面统一审批（资产和 API Key）

#### 3. 文件上传
- **大小限制**：50MB
- **支持格式**：`.md`、`.zip`
- **上传方式**：单文件（Markdown）或文件夹（ZIP）

#### 4. 搜索与排序
- **搜索字段**：名称、描述、标签
- **排序方式**：最新、最热（浏览量）、下载量、点赞数

#### 5. 数据统计
- 实时更新（不需要定时任务汇总）

#### 6. 分页
- 默认每页 **20 条**

#### 7. 资产类型
- 暂时只支持 **LLM** 和 **Skills**

---

## 一、整体架构和技术栈

### 前端技术栈
- Vue3 + Vite + TypeScript
- Element Plus（UI 组件库）
- Pinia（状态管理）
- VueUse（工具库）
- Monaco Editor / CodeMirror（代码编辑器）

### 后端技术栈
- Java 17
- SpringBoot 2.6.5
- MyBatis Plus
- PostgreSQL
- MinIO / S3（对象存储）

### 架构分层
```
┌─────────────┐
│  前端层    │  Vue3 + Element Plus
└──────┬──────┘
       │
┌──────▼──────┐
│   Nginx      │  反向代理
└──────┬──────┘
       │
┌──────▼──────┐
│ SpringBoot   │  RESTful API + 业务服务层 + 数据访问层
│   后端层     │
└──────┬──────┘
       │
┌──────▼──────┐
│ PostgreSQL   │  数据存储
│   数据层     │
└──────┬──────┘
       │
┌──────▼──────┐
│ MinIO / S3   │  对象存储（文件上传）
└──────────────┘
```

### 核心模块
1. **用户门户**：资产浏览、搜索、测试、下载、收藏
2. **资产管理**：草稿箱、发布表单、版本管理
3. **审核中心**：审核队列（资产 + API Key）
4. **管理后台**：资产管理、分类管理、数据统计、系统配置

---

## 二、数据库设计

### 1. 用户相关表

#### user - 用户表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| username | varchar | 用户名 |
| password | varchar | 密码（加密） |
| email | varchar | 邮箱 |
| department | varchar | 部门 |
| role | varchar | 角色（user/admin） |
| created_at | timestamp | 创建时间 |

#### user_role - 用户角色表（预留）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| role_code | varchar | 角色编码 |
| role_name | varchar | 角色名称 |
| permissions | json | 权限列表 |

### 2. 资产基础表

#### asset - 通用资产表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| asset_type | varchar | 资产类型（llm/skill） |
| name | varchar | 资产名称 |
| description | text | 描述 |
| category_id | bigint | 分类 ID |
| tags | varchar | 标签（逗号分隔） |
| status | varchar | 状态（draft/pending_review/approved/rejected/offline） |
| current_version_id | bigint | 当前版本 ID |
| created_by | bigint | 创建人 ID |
| created_at | timestamp | 创建时间 |
| updated_at | timestamp | 更新时间 |

#### asset_version - 资产版本表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| asset_id | bigint | 资产 ID |
| version_no | int | 版本号 |
| content | text | 内容（文本型资产） |
| storage_path | varchar | 存储路径（文件型资产） |
| file_type | varchar | 文件类型（md/zip） |
| view_count | int | 浏览量 |
| download_count | int | 下载量 |
| like_count | int | 点赞数 |
| created_at | timestamp | 创建时间 |

#### category - 分类表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| name | varchar | 分类名称 |
| parent_id | bigint | 父分类 ID（预留多层级） |
| asset_type | varchar | 适用资产类型（llm/skill） |
| sort_order | int | 排序 |
| created_at | timestamp | 创建时间 |

### 3. LLM 专用表

#### llm_model_config - LLM 模型配置表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| name | varchar | 模型名称 |
| provider | varchar | 提供商 |
| model_name | varchar | 模型标识 |
| description | text | 描述 |
| category_id | bigint | 分类 ID |
| api_protocol | varchar | API 协议（openai/anthropic） |
| api_endpoint | varchar | API 端点 |
| max_tokens | int | 最大 token 数 |
| default_params | json | 默认参数（temperature、top_p 等） |
| billing_rule | json | 计费规则 |
| version | varchar | 版本号 |
| status | varchar | 状态（active/inactive） |
| created_by | bigint | 配置人 ID |
| created_at | timestamp | 创建时间 |
| updated_at | timestamp | 更新时间 |

#### llm_test_record - LLM 测试记录表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| model_config_id | bigint | 模型配置 ID |
| prompt | text | 提示词 |
| response | text | 模型响应 |
| parameters | json | 请求参数 |
| response_time | int | 响应时间 (ms) |
| token_usage | json | Token 使用量 |
| created_at | timestamp | 创建时间 |

#### api_key - API Key 表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| model_config_id | bigint | 模型配置 ID |
| key_value | varchar | Key 值 |
| api_protocol | varchar | API 协议（openai/anthropic） |
| status | varchar | 状态（pending/approved/rejected/expired） |
| expiry_type | varchar | 有效期类型（3m/6m/1y/permanent） |
| apply_time | timestamp | 申请时间 |
| approve_time | timestamp | 审批时间 |
| expiry_date | timestamp | 过期日期 |
| approver_id | bigint | 审批人 ID |

### 4. 审核相关表

#### approval_record - 审核记录表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| approval_type | varchar | 审批类型（asset/api_key） |
| target_id | bigint | 目标 ID（资产 ID 或 API Key ID） |
| target_type | varchar | 目标类型 |
| reviewer_id | bigint | 审核人 ID |
| status | varchar | 审批状态（approved/rejected） |
| comment | text | 审批意见 |
| external_workflow_id | varchar | 外部工作流 ID（预留） |
| created_at | timestamp | 创建时间 |

### 5. 互动相关表

#### favorite - 收藏表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| asset_id | bigint | 资产 ID |
| asset_type | varchar | 资产类型（llm/skill） |
| created_at | timestamp | 创建时间 |
| UNIQUE(user_id, asset_id, asset_type) | - | 联合唯一索引 |

#### like_record - 点赞记录表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| asset_id | bigint | 资产 ID |
| asset_type | varchar | 资产类型 |
| asset_version_id | bigint | 资产版本 ID |
| created_at | timestamp | 创建时间 |
| UNIQUE(user_id, asset_id, asset_type, asset_version_id) | - | 联合唯一索引 |

### 6. 统计相关表

#### llm_usage_log - LLM 使用日志表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| api_key_id | bigint | API Key ID |
| model_config_id | bigint | 模型配置 ID |
| prompt_tokens | int | 输入 token 数 |
| completion_tokens | int | 输出 token 数 |
| total_tokens | int | 总 token 数 |
| response_time | int | 响应时间 |
| error_count | int | 错误次数 |
| created_at | timestamp | 创建时间 |

---

## 三、前端页面结构和路由

### 用户门户路由

```
/login                     - 登录页
/                          - 首页（导航到各模块）
/llm                       - LLM 模块
/llm/models                - 模型列表
/llm/models/:id            - 模型详情/测试页
/llm/my-keys               - 我的 API Key
/skills                     - Skills 模块
/skills/list                - Skills 列表
/skills/:id                - Skills 详情页
/favorites                 - 我的收藏（支持类型筛选）
/my-published              - 我发布的资产
/my-drafts                 - 我的草稿箱
/publish                    - 资产发布页（新建/编辑）
/publish/:id                - 编辑指定资产
```

### 管理后台路由

```
/admin                      - 管理后台首页
/admin/assets               - 资产管理
/admin/assets/:id           - 资产编辑/审核
/admin/reviews              - 审核队列（资产 + API Key）
/admin/categories           - 分类管理
/admin/statistics           - 数据统计
  /assets                 - 资产热度
  /users                  - 用户活跃度
  /api-keys               - API Key 使用
/admin/config               - 系统配置
  /llm-models            - LLM 模型配置
```

### 前端布局

**用户门户布局**：
- 顶部导航：Logo、搜索框、LLM、Skills、我发布的、我的草稿、收藏
- 主内容区：根据路由展示不同页面
- LLM 测试页：左侧对话区，右侧参数配置 + 性能指标
- Skills 详情页：左侧内容区，右侧信息统计和操作按钮

**管理后台布局**：
- 侧边栏菜单：资产管理、审核队列、分类管理、数据统计、LLM 模型配置
- 右侧内容区：根据菜单展示对应页面

---

## 四、后端 API 接口设计

### 认证相关

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |
| GET | /api/auth/me | 获取当前用户信息 |

### 用户门户 API

#### 资产相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/assets | 获取资产列表（分页、搜索、筛选、排序） |
| GET | /api/assets/:id | 获取资产详情 |
| GET | /api/assets/categories | 获取分类列表 |
| GET | /api/assets/favorites | 获取我的收藏 |
| POST | /api/assets/favorite | 收藏/取消收藏 |
| POST | /api/assets/like | 点赞/取消点赞 |
| POST | /api/assets/:id/view | 增加浏览量 |
| POST | /api/assets/:id/download | 增加下载量 |
| GET | /api/assets/my-published | 获取我发布的资产 |
| GET | /api/assets/my-drafts | 获取我的草稿 |
| POST | /api/assets | 创建/保存资产 |
| PUT | /api/assets/:id | 更新资产 |
| DELETE | /api/assets/:id | 删除资产（草稿） |
| POST | /api/assets/:id/submit | 提交审核 |
| GET | /api/assets/:id/versions | 获取资产版本列表 |

#### LLM 专用
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/llm/models | 获取模型列表（已上架） |
| GET | /api/llm/models/:id | 获取模型详情 |
| POST | /api/llm/test | 执行模型测试 |
| GET | /api/llm/api-keys | 获取我的 API Key |
| POST | /api/llm/api-keys/apply | 申请 API Key |

#### 文件上传
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/files/upload | 文件上传（.md/.zip，最大50MB） |
| GET | /api/files/:id | 下载文件 |

### 管理后台 API

#### 资产管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/assets | 获取资产管理列表 |
| GET | /api/admin/assets/:id | 获取资产详情 |
| PUT | /api/admin/assets/:id/status | 修改资产状态 |
| DELETE | /api/admin/assets/:id | 删除资产 |

#### 审核相关（资产 + API Key）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/reviews | 获取审核队列（支持类型筛选） |
| POST | /api/admin/reviews/:id/approve | 审批通过 |
| POST | /api/admin/reviews/:id/reject | 审批拒绝 |
| GET | /api/admin/approval/records | 获取审核记录 |
| POST | /api/admin/approval/callback | 审批回调接口（外部工作流预留） |

#### 分类管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/categories | 获取分类列表 |
| POST | /api/admin/categories | 创建分类 |
| PUT | /api/admin/categories/:id | 更新分类 |
| DELETE | /api/admin/categories/:id | 删除分类 |

#### 数据统计
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/statistics/assets | 资产热度统计 |
| GET | /api/admin/statistics/users | 用户活跃度统计 |
| GET | /api/admin/statistics/api-keys | API Key 使用统计 |

#### LLM 模型配置
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/config/llm-models | 获取 LLM 模型配置列表 |
| POST | /api/admin/config/llm-models | 创建 LLM 模型配置 |
| PUT | /api/admin/config/llm-models/:id | 更新 LLM 模型配置 |
| DELETE | /api/admin/config/llm-models/:id | 删除 LLM 模型配置 |

---

## 五、核心业务流程

### 1. 用户登录流程

```
访问系统
       ↓
重定向到登录页
       ↓
输入用户名/密码
       ↓
后端验证
       ↓
设置 Session / 返回用户信息
       ↓
跳转到首页
```

### 2. 资产发布流程

```
用户进入发布页
       ↓
选择资产类型（Skill） - LLM 由管理员配置
       ↓
填写表单（名称、描述、分类、标签）
       ↓
选择编辑模式：
  - 在线编辑：使用 Markdown 编辑器
  - 上传文件：支持 .md 或 .zip
       ↓
保存草稿（状态：draft）
       ↓
【可多次编辑】
       ↓
提交审核（状态：pending_review）
       ↓
管理员审核
       ↓
审批通过/拒绝
       ↓
状态更新（approved/rejected）
       ↓
审核通过 → 上架发布
```

### 3. API Key 申请流程

```
用户进入 LLM 模型详情
       ↓
点击"申请 API Key"
       ↓
选择有效期（三个月/六个月/一年/永久）
       ↓
选择协议（OpenAI/Anthropic）
       ↓
提交申请（状态：pending）
       ↓
管理员审批队列
       ↓
审批通过/拒绝
       ↓
状态更新（approved/rejected）
       ↓
生成/返回 API Key
```

### 4. 收藏流程

```
用户在 LLM 或 Skill 详情页点击收藏
       ↓
前端调用 POST /api/assets/favorite
（传入 asset_id 和 asset_type）
       ↓
后端写入 favorite 表
       ↓
我的收藏页按类型筛选展示：
  - 全部 / LLM / Skills
```

### 5. 数据统计流程

```
资产浏览/下载/点赞 → 实时更新 asset_version 计数
       ↓
API Key 调用 → 实时写入 llm_usage_log
       ↓
数据统计页面实时查询展示：
  - 资产热度：浏览量、下载量、点赞数
  - 用户活跃度：发布资产数
  - API Key 使用：调用统计、token 消耗
```

---

## 六、技术要点

### 1. 权限控制
- 不使用 Spring Security，硬编码用户角色
- 前端通过用户角色判断是否有管理员权限
- `/admin/*` 路由需要管理员角色才能访问
- Session 管理用户登录状态

### 2. 对象存储
- 支持配置切换 MinIO/S3
- 文件上传限制：50MB、.md/.zip 格式
- 上传后获取存储路径，写入 asset_version.storage_path

### 3. 版本管理
- 一个资产可以有多个版本
- asset.current_version_id 指向当前发布的版本
- 只有草稿状态的版本可以编辑和删除
- 已发布的版本只读，新建版本

### 4. 审批集成
- 预留 `/api/admin/approval/callback` 接口
- 审批队列支持资产和 API Key 两种类型

### 5. 前端美化
- 使用 frontend-design skill 进行 UI/UX 美化
- 确保页面风格统一、交互体验流畅
- 响应式设计，支持多终端访问

---

## 七、项目实施建议

### 方案选择
**建议：新建独立项目，迁移有价值代码**

原因：
1. 新项目可以避免现有 MVP 代码的技术债
2. 数据库结构变化较大，新建更清晰
3. 可以复用现有有价值的组件和工具类
4. 便于对比新旧功能，逐步迁移

### 实施步骤
1. 创建新项目分支或独立仓库
2. 按照新设计重新搭建项目结构
3. 逐步迁移现有可复用代码
4. 分模块开发新功能
5. 逐步替换原有项目
