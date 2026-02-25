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
- Spring Security（权限控制）
- MyBatis Plus
- PostgreSQL
- MinIO / OSS（对象存储，用于文件存储）

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
│  MinIO/OSS   │  对象存储（文件上传）
└──────────────┘
```

### 核心模块
1. **用户门户**：资产浏览、搜索、测试、下载、收藏
2. **资产管理**：草稿箱、发布表单、版本管理
3. **审核中心**：审核队列、审批回调接口
4. **管理后台**：资产管理、分类管理、数据统计、系统配置

---

## 二、数据库设计

### 1. 用户相关表

#### user - 用户表（硬编码用户，角色字段预留）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| username | varchar | 用户名 |
| email | varchar | 邮箱 |
| department | varchar | 部门 |
| role | varchar | 角色（user/admin） |
| created_at | timestamp | 创建时间 |

#### user_role - 用户角色表（预留，用于 RBAC 扩展）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| role_code | varchar | 角色编码 |
| role_name | varchar | 角色名称 |
| permissions | json | 权限列表 |

### 2. 资产基础表

#### asset - 通用资产表（支持 LLM、Skills、未来 MCP/Tools）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| asset_type | varchar | 资产类型（llm/skill/mcp/tool） |
| name | varchar | 资产名称 |
| description | text | 描述 |
| category_id | bigint | 分类 ID |
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
| view_count | int | 浏览量 |
| download_count | int | 下载量 |
| like_count | int | 点赞数 |
| created_at | timestamp | 创建时间 |

#### category - 分类表（支持多层级）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| name | varchar | 分类名称 |
| parent_id | bigint | 父分类 ID（多层级） |
| asset_type | varchar | 适用资产类型 |
| sort_order | int | 排序 |
| created_at | timestamp | 创建时间 |

### 3. LLM 专用表

#### llm_model_config - LLM 模型配置表（由大模型负责人员配置）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| name | varchar | 模型名称 |
| provider | varchar | 提供商 |
| model_name | varchar | 模型标识 |
| description | text | 描述 |
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
| status | varchar | 状态（pending/approved/rejected/expired） |
| apply_time | timestamp | 申请时间 |
| approve_time | timestamp | 审批时间 |
| expiry_date | timestamp | 过期日期 |

### 4. 审核相关表

#### approval_record - 审核记录表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| asset_id | bigint | 资产 ID |
| asset_type | varchar | 资产类型 |
| asset_version_id | bigint | 资产版本 ID |
| reviewer_id | bigint | 审核人 ID |
| status | varchar | 审批状态（approved/rejected） |
| comment | text | 审批意见 |
| external_workflow_id | varchar | 外部工作流 ID |
| created_at | timestamp | 创建时间 |

### 5. 互动相关表

#### favorite - 收藏表（支持多资产类型）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户 ID |
| asset_id | bigint | 资产 ID |
| asset_type | varchar | 资产类型（llm/skill/mcp/tool） |
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

#### asset_statistics - 资产统计汇总表（可定期更新）
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| asset_id | bigint | 资产 ID |
| asset_version_id | bigint | 资产版本 ID |
| stat_date | date | 统计日期 |
| view_count | int | 浏览量 |
| download_count | int | 下载量 |
| like_count | int | 点赞数 |
| created_at | timestamp | 创建时间 |

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

#### api_key_usage_log - API Key 使用日志表
| 字段 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键 |
| api_key_id | bigint | API Key ID |
| user_id | bigint | 用户 ID |
| request_count | int | 请求次数 |
| total_tokens | int | 总消耗 token |
| cost | decimal | 消费金额 |
| stat_date | date | 统计日期 |
| created_at | timestamp | 创建时间 |

---

## 三、前端页面结构和路由

### 用户门户路由

```
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
/admin/reviews              - 审核队列
/admin/categories            - 分类管理
/admin/users                - 用户管理（预留）
/admin/statistics            - 数据统计
  /assets                 - 资产热度
  /users                  - 用户活跃度
  /api-keys               - API Key 使用
/admin/config               - 系统配置
  /llm-models            - LLM 模型配置
  /approval               - 审批流程配置
```

### 前端布局

**用户门户布局**：
- 顶部导航：Logo、搜索、LLM、Skills、我发布的、我的草稿
- 主内容区：根据路由展示不同页面
- LLM 测试页：左侧对话区，右侧参数配置 + 性能指标
- Skills 详情页：左侧内容区，右侧信息统计和操作按钮

**管理后台布局**：
- 侧边栏菜单：资产管理、审核队列、分类管理、数据统计、系统配置
- 右侧内容区：根据菜单展示对应页面

---

## 四、后端 API 接口设计

### 用户门户 API

#### 资产相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/assets | 获取资产列表（支持分页、搜索、筛选、排序） |
| GET | /api/assets/:id | 获取资产详情 |
| GET | /api/assets/categories | 获取分类列表 |
| GET | /api/assets/favorites | 获取我的收藏（支持 asset_type 筛选） |
| POST | /api/assets/favorite | 收藏/取消收藏 |
| POST | /api/assets/like | 点赞/取消点赞 |
| POST | /api/assets/:id/view | 增加浏览量 |
| POST | /api/assets/:id/download | 增加下载量 |
| GET | /api/assets/my-published | 获取我发布的资产 |
| GET | /api/assets/my-drafts | 获取我的草稿 |
| POST | /api/assets | 创建/保存资产（草稿或提交审核） |
| PUT | /api/assets/:id | 更新资产 |
| DELETE | /api/assets/:id | 删除资产（草稿） |
| POST | /api/assets/:id/submit | 提交审核 |
| GET | /api/assets/versions/:id | 获取资产版本列表 |

#### LLM 专用
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/llm/models | 获取模型列表（已上架） |
| GET | /api/llm/models/:id | 获取模型详情 |
| POST | /api/llm/test | 执行模型测试 |
| GET | /api/llm/api-keys | 获取我的 API Key |
| POST | /api/llm/api-key/apply | 申请 API Key |

### 管理后台 API

#### 资产管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/assets | 获取资产管理列表（全部状态） |
| GET | /api/admin/assets/:id | 获取资产详情（包括版本列表） |
| PUT | /api/admin/assets/:id/status | 修改资产状态（审核、上下架） |
| DELETE | /api/admin/assets/:id | 删除资产 |

#### 审核相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/reviews | 获取审核队列 |
| GET | /api/admin/approval/records | 获取审核记录 |
| POST | /api/admin/approval/callback | 审批回调接口（外部工作流） |

#### 分类管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/categories | 获取分类列表（树形结构） |
| POST | /api/admin/categories | 创建分类 |
| PUT | /api/admin/categories/:id | 更新分类 |
| DELETE | /api/admin/categories/:id | 删除分类 |

#### 数据统计
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/statistics/assets | 资产热度统计（浏览量、下载量、点赞数排名） |
| GET | /api/admin/statistics/users | 用户活跃度统计（活跃用户数、发布资产数） |
| GET | /api/admin/statistics/api-keys | API Key 使用统计（各用户使用情况、异常调用） |

#### 系统配置
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/config/llm-models | 获取 LLM 模型配置列表 |
| POST | /api/admin/config/llm-models | 创建/更新 LLM 模型配置 |
| PUT | /api/admin/config/llm-models/:id | 更新 LLM 模型配置 |
| DELETE | /api/admin/config/llm-models/:id | 删除 LLM 模型配置 |
| GET | /api/admin/config/approval | 获取审批流程配置 |
| POST | /api/admin/config/approval | 保存审批流程配置 |

---

## 五、核心业务流程

### 1. 资产发布流程

```
用户进入发布页
       ↓
选择资产类型（LLM/Skill）
       ↓
填写表单（名称、描述、分类、版本）
       ↓
选择编辑模式：
  - 在线编辑：使用 Markdown 富文本编辑器
  - 上传文件：支持单文件或文件夹 zip
       ↓
保存草稿（状态：draft）
       ↓
【可多次编辑】
       ↓
提交审核（状态：pending_review）
       ↓
审核队列
       ↓
【管理员审核 或 外部工作流审批】
       ↓
审批回调 /api/admin/approval/callback
       ↓
状态更新（approved/rejected）
       ↓
审核通过 → 上架发布（状态：approved）
```

### 2. 收藏扩展流程

```
用户在 LLM 或 Skill 详情页点击收藏
       ↓
前端调用 POST /api/assets/favorite
（传入 asset_id 和 asset_type）
       ↓
后端写入 favorite 表（支持多资产类型）
       ↓
我的收藏页按类型筛选展示：
  - 全部 / LLM / Skills（为 MCP/Tools 预留）
```

### 3. 审核流程

```
管理员进入审核队列
（查看 pending_review 状态的资产）
       ↓
审核通过/拒绝
       ↓
调用后端 API 更新状态
       ↓
【或外部工作流审批】
       ↓
调用审批回调接口 /api/admin/approval/callback
       ↓
状态更新（approved/rejected）
       ↓
记录审批结果到 approval_record 表
```

### 4. 数据统计流程

```
资产浏览/下载/点赞 → 实时更新计数
       ↓
定时任务（每小时/每日）
       ↓
汇总统计数据到 asset_statistics 表
       ↓
数据统计页面展示：
  - 资产热度：浏览量、下载量、点赞数
  - 用户活跃度：发布资产数、活跃天数
  - API Key 使用：记录每次调用到 llm_usage_log
```

---

## 六、技术要点

### 1. 权限控制
- 暂不使用 Spring Security，硬编码用户角色
- 前端通过用户 ID 判断是否有管理员权限
- `/admin/*` 路由需要管理员角色才能访问

### 2. 对象存储
- 文件上传使用 MinIO 或企业内部 OSS
- 支持单文件（Markdown）和文件夹（ZIP）两种形式
- 文件上传后获取存储路径，写入 asset_version.storage_path

### 3. 版本管理
- 一个资产可以有多个版本
- asset.current_version_id 指向当前发布的版本
- 只有草稿状态的版本可以编辑和删除
- 已发布的版本只读，新建版本

### 4. 审批集成
- 预留 `/api/admin/approval/callback` 接口
- 接收外部工作流的审批结果
- 更新资产状态和审核记录

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
