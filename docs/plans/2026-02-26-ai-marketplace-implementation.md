# AI Marketplace Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an enterprise AI capability sharing platform with LLM model and Skills (prompt templates) management, publishing, review, and usage functionality.

**Architecture:**
- Frontend: Vue3 + Vite + TypeScript + Element Plus (SPA)
- Backend: SpringBoot 2.6.5 + MyBatis Plus + PostgreSQL
- Storage: MinIO/S3 (configurable)
- Auth: Simple username/password without JWT

**Tech Stack:** Java 17, Vue3, PostgreSQL, MinIO/S3, Element Plus

---

## Phase 1: Project Setup (Foundation)

### Task 1.1: Backend Project Structure

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/java/com/aimarketplace/AiMarketplaceApplication.java`

**Step 1: Create Maven POM file**

Create `backend/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.5</version>
        <relativePath/>
    </parent>

    <groupId>com.aimarketplace</groupId>
    <artifactId>ai-marketplace</artifactId>
    <version>1.0.0</version>
    <name>AI Marketplace</name>
    <description>Enterprise AI capability sharing platform</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.3</mybatis-plus.version>
        <postgresql.version>42.3.3</postgresql.version>
        <minio.version>8.5.2</minio.version>
        <aws.version>1.12.300</aws.version>
    </properties>

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MyBatis Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>

        <!-- MinIO -->
        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>${minio.version}</version>
        </dependency>

        <!-- AWS S3 -->
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-s3</artifactId>
            <version>${aws.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**Step 2: Create application configuration**

Create `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: ai-marketplace

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://10.254.254.103:5432/ai-marketplace
    username: postgres
    password: postgres

  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

storage:
  type: minio  # minio or s3
  minio:
    endpoint: http://10.254.254.103:9001
    access-key: minioadmin
    secret-key: minioadmin
    bucket: ai-marketplace
  s3:
    endpoint: ${S3_ENDPOINT:}
    access-key: ${S3_ACCESS_KEY:}
    secret-key: ${S3_SECRET_KEY:}
    bucket: ${S3_BUCKET:}
    region: ${S3_REGION:}
```

**Step 3: Create main application class**

Create `backend/src/main/java/com/aimarketplace/AiMarketplaceApplication.java`:

```java
package com.aimarketplace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aimarketplace.mapper")
public class AiMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMarketplaceApplication.class, args);
    }
}
```

**Step 4: Verify project compiles**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 5: Commit**

```bash
git add backend/
git commit -m "feat: setup backend project structure with Maven and Spring Boot"
```

---

### Task 1.2: Frontend Project Structure

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.ts`

**Step 1: Create package.json**

Create `frontend/package.json`:

```json
{
  "name": "ai-marketplace-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "@vueuse/core": "^10.7.0",
    "element-plus": "^2.5.0",
    "@element-plus/icons-vue": "^2.3.1",
    "axios": "^1.6.5"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "typescript": "^5.3.3",
    "vue-tsc": "^1.8.27",
    "vite": "^5.0.11",
    "unplugin-vue-components": "^0.26.0",
    "unplugin-auto-import": "^0.17.3"
  }
}
```

**Step 2: Create Vite config**

Create `frontend/vite.config.ts`:

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia']
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

**Step 3: Create TypeScript config**

Create `frontend/tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": "./src",
    "paths": {
      "@/*": ["./*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

**Step 4: Create tsconfig.node.json**

Create `frontend/tsconfig.node.json`:

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

**Step 5: Create index.html**

Create `frontend/index.html`:

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AI Marketplace - 企业AI能力共享平台</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.ts"></script>
</body>
</html>
```

**Step 6: Create main entry file**

Create `frontend/src/main.ts`:

```ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import router from './router'
import App from './App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
```

**Step 7: Create root App component**

Create `frontend/src/App.vue`:

```vue
<template>
  <router-view />
</template>

<script setup lang="ts">
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
```

**Step 8: Create basic router**

Create `frontend/src/router/index.ts`:

```ts
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      component: () => import('@/views/Login.vue')
    }
  ]
})

export default router
```

**Step 9: Create placeholder login page**

Create `frontend/src/views/Login.vue`:

```vue
<template>
  <div class="login-container">
    <h1>AI Marketplace</h1>
    <p>登录页（待实现）</p>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
```

**Step 10: Install dependencies and verify**

Run: `cd frontend && npm install`
Expected: Dependencies installed successfully

**Step 11: Commit**

```bash
git add frontend/
git commit -m "feat: setup frontend project structure with Vue3 and Element Plus"
```

---

## Phase 2: Database Schema

### Task 2.1: Create Database Schema

**Files:**
- Create: `backend/src/main/resources/db/schema.sql`

**Step 1: Write complete database schema**

Create `backend/src/main/resources/db/schema.sql`:

```sql
-- AI Marketplace Database Schema

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    asset_type VARCHAR(20) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 资产表
CREATE TABLE IF NOT EXISTS asset (
    id BIGSERIAL PRIMARY KEY,
    asset_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES category(id),
    tags VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    current_version_id BIGINT,
    created_by BIGINT REFERENCES user(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 资产版本表
CREATE TABLE IF NOT EXISTS asset_version (
    id BIGSERIAL PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES asset(id) ON DELETE CASCADE,
    version_no INT NOT NULL,
    content TEXT,
    storage_path VARCHAR(500),
    file_type VARCHAR(20),
    view_count INT DEFAULT 0,
    download_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(asset_id, version_no)
);

-- LLM 模型配置表
CREATE TABLE IF NOT EXISTS llm_model_config (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES category(id),
    api_protocol VARCHAR(20) NOT NULL DEFAULT 'openai',
    api_endpoint VARCHAR(500) NOT NULL,
    max_tokens INT DEFAULT 4096,
    default_params JSONB,
    billing_rule JSONB,
    version VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_by BIGINT REFERENCES user(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM 测试记录表
CREATE TABLE IF NOT EXISTS llm_test_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES user(id),
    model_config_id BIGINT REFERENCES llm_model_config(id),
    prompt TEXT,
    response TEXT,
    parameters JSONB,
    response_time INT,
    token_usage JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API Key 表
CREATE TABLE IF NOT EXISTS api_key (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id),
    model_config_id BIGINT REFERENCES llm_model_config(id),
    key_value VARCHAR(255) UNIQUE NOT NULL,
    api_protocol VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    expiry_type VARCHAR(20) NOT NULL DEFAULT '3m',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP,
    expiry_date TIMESTAMP,
    approver_id BIGINT REFERENCES user(id)
);

-- 审核记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGSERIAL PRIMARY KEY,
    approval_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    target_type VARCHAR(20),
    reviewer_id BIGINT REFERENCES user(id),
    status VARCHAR(20) NOT NULL,
    comment TEXT,
    external_workflow_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 收藏表
CREATE TABLE IF NOT EXISTS favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id) ON DELETE CASCADE,
    asset_id BIGINT NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, asset_id, asset_type)
);

-- 点赞记录表
CREATE TABLE IF NOT EXISTS like_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id) ON DELETE CASCADE,
    asset_id BIGINT NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    asset_version_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, asset_id, asset_type, asset_version_id)
);

-- LLM 使用日志表
CREATE TABLE IF NOT EXISTS llm_usage_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES user(id),
    api_key_id BIGINT REFERENCES api_key(id),
    model_config_id BIGINT REFERENCES llm_model_config(id),
    prompt_tokens INT DEFAULT 0,
    completion_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    response_time INT,
    error_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_asset_type ON asset(asset_type);
CREATE INDEX IF NOT EXISTS idx_asset_status ON asset(status);
CREATE INDEX IF NOT EXISTS idx_asset_created_by ON asset(created_by);
CREATE INDEX IF NOT EXISTS idx_asset_category ON asset(category_id);
CREATE INDEX IF NOT EXISTS idx_asset_version_asset_id ON asset_version(asset_id);
CREATE INDEX IF NOT EXISTS idx_llm_config_status ON llm_model_config(status);
CREATE INDEX IF NOT EXISTS idx_api_key_user_id ON api_key(user_id);
CREATE INDEX IF NOT EXISTS idx_api_key_status ON api_key(status);
CREATE INDEX IF NOT EXISTS idx_approval_target ON approval_record(approval_type, target_id);
CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorite(user_id, asset_type);
CREATE INDEX IF NOT EXISTS idx_like_record_asset ON like_record(asset_id, asset_type);
```

**Step 2: Execute schema creation**

Run: `psql -h 10.254.254.103 -U postgres -d ai-marketplace -f backend/src/main/resources/db/schema.sql`
Expected: CREATE TABLE statements executed successfully

**Step 3: Commit**

```bash
git add backend/src/main/resources/db/schema.sql
git commit -m "feat: create database schema for AI Marketplace"
```

---

### Task 2.2: Create Initial Data Script

**Files:**
- Create: `backend/src/main/resources/db/init-data.sql`

**Step 1: Write initial data script**

Create `backend/src/main/resources/db/init-data.sql`:

```sql
-- AI Marketplace Initial Data

-- 插入管理员用户 (密码: admin, BCrypt加密)
INSERT INTO user (username, password, role, email, department) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', 'admin@example.com', 'IT部门')
ON CONFLICT (username) DO NOTHING;

-- 插入 LLM 分类
INSERT INTO category (name, asset_type, sort_order) VALUES
('通用对话', 'llm', 1),
('代码生成', 'llm', 2),
('数据分析', 'llm', 3),
('图像理解', 'llm', 4)
ON CONFLICT DO NOTHING;

-- 插入 Skills 分类
INSERT INTO category (name, asset_type, sort_order) VALUES
('文案写作', 'skill', 1),
('数据分析', 'skill', 2),
('代码辅助', 'skill', 3),
('翻译', 'skill', 4),
('总结摘要', 'skill', 5)
ON CONFLICT DO NOTHING;

-- 获取分类ID (用于后续插入)
DO $$
DECLARE
    chat_cat_id BIGINT;
BEGIN
    SELECT id INTO chat_cat_id FROM category WHERE name = '通用对话' AND asset_type = 'llm' LIMIT 1;

    -- 插入预置 LLM 模型 (GLM-5, GLM-4.7)
    -- 注意：实际部署时需要配置正确的 API endpoint
    INSERT INTO llm_model_config (name, provider, model_name, description, category_id, api_protocol, api_endpoint, max_tokens, status, created_by)
    VALUES
    ('GLM-5', '智谱AI', 'glm-5', '智谱AI GLM-5 大语言模型，支持通用对话、代码生成等能力', chat_cat_id, 'openai', 'https://open.bigmodel.cn/api/paas/v4/', 128000, 'active', 1),
    ('GLM-4.7', '智谱AI', 'glm-4.7', '智谱AI GLM-4.7 大语言模型', chat_cat_id, 'openai', 'https://open.bigmodel.cn/api/paas/v4/', 128000, 'active', 1)
    ON CONFLICT DO NOTHING;
END $$;
```

**Step 3: Execute initial data script**

Run: `psql -h 10.254.254.103 -U postgres -d ai-marketplace -f backend/src/main/resources/db/init-data.sql`
Expected: Data inserted successfully

**Step 4: Verify admin user created**

Run: `psql -h 10.254.254.103 -U postgres -d ai-marketplace -c "SELECT id, username, role FROM user WHERE username='admin';"`
Expected: Shows admin user with id=1, role=admin

**Step 5: Commit**

```bash
git add backend/src/main/resources/db/init-data.sql
git commit -m "feat: add initial data with admin user and preset LLM models"
```

---

## Phase 3: Backend Core - Entity & Mapper

### Task 3.1: Create Entity Classes

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/entity/User.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/Category.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/Asset.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/AssetVersion.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/LlmModelConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/ApiKey.java`

**Step 1: Create User entity**

Create `backend/src/main/java/com/aimarketplace/entity/User.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String department;
    private String role; // user, admin
    private LocalDateTime createdAt;
}
```

**Step 2: Create Category entity**

Create `backend/src/main/java/com/aimarketplace/entity/Category.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private String assetType; // llm, skill
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
```

**Step 3: Create Asset entity**

Create `backend/src/main/java/com/aimarketplace/entity/Asset.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("asset")
public class Asset {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String assetType; // llm, skill
    private String name;
    private String description;
    private Long categoryId;
    private String tags;
    private String status; // draft, pending_review, approved, rejected, offline
    private Long currentVersionId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Step 4: Create AssetVersion entity**

Create `backend/src/main/java/com/aimarketplace/entity/AssetVersion.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("asset_version")
public class AssetVersion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long assetId;
    private Integer versionNo;
    private String content;
    private String storagePath;
    private String fileType; // md, zip
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private LocalDateTime createdAt;
}
```

**Step 5: Create LlmModelConfig entity**

Create `backend/src/main/java/com/aimarketplace/entity/LlmModelConfig.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName("llm_model_config")
public class LlmModelConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private Long categoryId;
    private String apiProtocol; // openai, anthropic
    private String apiEndpoint;
    private Integer maxTokens;
    private Map<String, Object> defaultParams;
    private Map<String, Object> billingRule;
    private String version;
    private String status; // active, inactive
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Step 6: Create ApiKey entity**

Create `backend/src/main/java/com/aimarketplace/entity/ApiKey.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("api_key")
public class ApiKey {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long modelConfigId;
    private String keyValue;
    private String apiProtocol; // openai, anthropic
    private String status; // pending, approved, rejected, expired
    private String expiryType; // 3m, 6m, 1y, permanent
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime expiryDate;
    private Long approverId;
}
```

**Step 7: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 8: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/entity/
git commit -m "feat: add entity classes for User, Category, Asset, AssetVersion, LlmModelConfig, ApiKey"
```

---

### Task 3.2: Create Mapper Interfaces

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/UserMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/CategoryMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/AssetMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/AssetVersionMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/LlmModelConfigMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/ApiKeyMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/FavoriteMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/LikeRecordMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/ApprovalRecordMapper.java`

**Step 1: Create UserMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/UserMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

**Step 2: Create CategoryMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/CategoryMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
```

**Step 3: Create AssetMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/AssetMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Asset;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
}
```

**Step 4: Create AssetVersionMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/AssetVersionMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.AssetVersion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AssetVersionMapper extends BaseMapper<AssetVersion> {
}
```

**Step 5: Create LlmModelConfigMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/LlmModelConfigMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LlmModelConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LlmModelConfigMapper extends BaseMapper<LlmModelConfig> {
}
```

**Step 6: Create ApiKeyMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/ApiKeyMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.ApiKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKey> {
}
```

**Step 7: Create FavoriteMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/FavoriteMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
```

**Step 8: Create LikeRecordMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/LikeRecordMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
}
```

**Step 9: Create ApprovalRecordMapper**

Create `backend/src/main/java/com/aimarketplace/mapper/ApprovalRecordMapper.java`:

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {
}
```

**Step 10: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 11: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/mapper/
git commit -m "feat: add MyBatis mapper interfaces for all entities"
```

---

## Phase 4: Backend Core - DTO & Common

### Task 4.1: Create Common Result Class

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/common/Result.java`

**Step 1: Create Result class**

Create `backend/src/main/java/com/aimarketplace/common/Result.java`:

```java
package com.aimarketplace.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/common/
git commit -m "feat: add common Result class for API responses"
```

---

### Task 4.2: Create DTO Classes

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LoginResponse.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/AssetDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/AssetPublishRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/CategoryDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmModelDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmTestRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/ApiKeyApplyRequest.java`

**Step 1: Create LoginRequest**

Create `backend/src/main/java/com/aimarketplace/dto/LoginRequest.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
```

**Step 2: Create LoginResponse**

Create `backend/src/main/java/com/aimarketplace/dto/LoginResponse.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String department;
}
```

**Step 3: Create AssetDTO**

Create `backend/src/main/java/com/aimarketplace/dto/AssetDTO.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssetDTO {
    private Long id;
    private String assetType;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String tags;
    private String status;
    private Long currentVersionId;
    private String createdBy;
    private String createdAt;
    private String updatedAt;

    // Version info
    private List<AssetVersionDTO> versions;
}

@Data
public class AssetVersionDTO {
    private Long id;
    private Integer versionNo;
    private String content;
    private String storagePath;
    private String fileType;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private String createdAt;
}
```

**Step 4: Create AssetPublishRequest**

Create `backend/src/main/java/com/aimarketplace/dto/AssetPublishRequest.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class AssetPublishRequest {
    private String assetType; // skill (llm 由管理员配置)
    private String name;
    private String description;
    private Long categoryId;
    private String tags;
    private String content; // 在线编辑内容
    private String fileType; // md, zip
}
```

**Step 5: Create CategoryDTO**

Create `backend/src/main/java/com/aimarketplace/dto/CategoryDTO.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String assetType;
    private Integer sortOrder;
}
```

**Step 6: Create LlmModelDTO**

Create `backend/src/main/java/com/aimarketplace/dto/LlmModelDTO.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LlmModelDTO {
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String apiProtocol;
    private Integer maxTokens;
    private Map<String, Object> defaultParams;
    private String status;
    private String createdAt;
}
```

**Step 7: Create LlmTestRequest**

Create `backend/src/main/java/com/aimarketplace/dto/LlmTestRequest.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LlmTestRequest {
    private Long modelConfigId;
    private String prompt;
    private Map<String, Object> parameters;
}
```

**Step 8: Create ApiKeyApplyRequest**

Create `backend/src/main/java/com/aimarketplace/dto/ApiKeyApplyRequest.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class ApiKeyApplyRequest {
    private Long modelConfigId;
    private String apiProtocol; // openai, anthropic
    private String expiryType; // 3m, 6m, 1y, permanent
}
```

**Step 9: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 10: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/dto/
git commit -m "feat: add DTO classes for API requests and responses"
```

---

## Phase 5: Backend Core - Authentication

### Task 5.1: Create Missing Entity Classes for Auth

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/entity/Favorite.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/LikeRecord.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/ApprovalRecord.java`

**Step 1: Create Favorite entity**

Create `backend/src/main/java/com/aimarketplace/entity/Favorite.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long assetId;
    private String assetType; // llm, skill
    private LocalDateTime createdAt;
}
```

**Step 2: Create LikeRecord entity**

Create `backend/src/main/java/com/aimarketplace/entity/LikeRecord.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("like_record")
public class LikeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long assetId;
    private String assetType;
    private Long assetVersionId;
    private LocalDateTime createdAt;
}
```

**Step 3: Create ApprovalRecord entity**

Create `backend/src/main/java/com/aimarketplace/entity/ApprovalRecord.java`:

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_record")
public class ApprovalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String approvalType; // asset, api_key
    private Long targetId;
    private String targetType;
    private Long reviewerId;
    private String status; // approved, rejected
    private String comment;
    private String externalWorkflowId;
    private LocalDateTime createdAt;
}
```

**Step 4: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 5: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/entity/Favorite.java
git add backend/src/main/java/com/aimarketplace/entity/LikeRecord.java
git add backend/src/main/java/com/aimarketplace/entity/ApprovalRecord.java
git commit -m "feat: add Favorite, LikeRecord, ApprovalRecord entity classes"
```

---

### Task 5.2: Create Configuration for CORS

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/config/CorsConfig.java`

**Step 1: Create CORS configuration**

Create `backend/src/main/java/com/aimarketplace/config/CorsConfig.java`:

```java
package com.aimarketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/config/CorsConfig.java
git commit -m "feat: add CORS configuration"
```

---

### Task 5.3: Create Auth Service

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/service/AuthService.java`

**Step 1: Create AuthService interface**

Create `backend/src/main/java/com/aimarketplace/service/AuthService.java`:

```java
package com.aimarketplace.service;

import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout();
    LoginResponse getCurrentUser(Long userId);
}
```

**Step 2: Create AuthServiceImpl**

Create `backend/src/main/java/com/aimarketplace/service/impl/AuthServiceImpl.java`:

```java
package com.aimarketplace.service.impl;

import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;
import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.UserMapper;
import com.aimarketplace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    // 简单的 Session 存储（生产环境应使用 Redis）
    private final ConcurrentHashMap<String, Long> sessionStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> userSessionStore = new ConcurrentHashMap<>();

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("用户名或密码不能为空");
        }

        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 简单密码验证（生产环境应使用 BCrypt）
        // 注意：初始化数据中的密码已经是 BCrypt 加密的
        if (!org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成 sessionId
        String sessionId = java.util.UUID.randomUUID().toString();
        sessionStore.put(sessionId, user.getId());
        userSessionStore.put(user.getId(), sessionId);

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());
        return response;
    }

    @Override
    public void logout() {
        // Session 清理在拦截器中处理
    }

    @Override
    public LoginResponse getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());
        return response;
    }
}
```

**Step 3: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/service/AuthService.java
git add backend/src/main/java/com/aimarketplace/service/impl/AuthServiceImpl.java
git commit -m "feat: add AuthService with login/logout functionality"
```

---

### Task 5.4: Create Auth Controller

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/controller/AuthController.java`

**Step 1: Create AuthController**

Create `backend/src/main/java/com/aimarketplace/controller/AuthController.java`:

```java
package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;
import com.aimarketplace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request,
                                       HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request);
            // 设置 sessionId 到 cookie
            String sessionId = java.util.UUID.randomUUID().toString();
            response.addHeader("X-Session-Id", sessionId);
            response.addCookie(new javax.servlet.http.Cookie("sessionId", sessionId));
            return Result.success(loginResponse);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/me")
    public Result<LoginResponse> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            LoginResponse user = authService.getCurrentUser(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
```

**Step 2: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/controller/AuthController.java
git commit -m "feat: add AuthController with login/logout/me endpoints"
```

---

### Task 5.5: Create Auth Interceptor

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/config/AuthInterceptor.java`
- Create: `backend/src/main/java/com/aimarketplace/config/WebConfig.java`

**Step 1: Create AuthInterceptor**

Create `backend/src/main/java/com/aimarketplace/config/AuthInterceptor.java`:

```java
package com.aimarketplace.config;

import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    // 简单的 Session 存储（生产环境应使用 Redis）
    private static final ConcurrentHashMap<String, Long> sessionStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> userSessionStore = new ConcurrentHashMap<>();

    public static void addSession(String sessionId, Long userId) {
        sessionStore.put(sessionId, userId);
        userSessionStore.put(userId, sessionId);
    }

    public static void removeSession(String sessionId) {
        Long userId = sessionStore.remove(sessionId);
        if (userId != null) {
            userSessionStore.remove(userId);
        }
    }

    public static Long getUserId(String sessionId) {
        return sessionStore.get(sessionId);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.equals("/api/auth/login")) {
            return true;
        }

        // 从 header 或 cookie 获取 sessionId
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId == null) {
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (javax.servlet.http.Cookie cookie : cookies) {
                    if ("sessionId".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (sessionId == null || !sessionStore.containsKey(sessionId)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        Long userId = sessionStore.get(sessionId);
        request.setAttribute("userId", userId);

        // 检查管理员权限
        if (uri.startsWith("/api/admin")) {
            User user = userMapper.selectById(userId);
            if (user == null || !"admin".equals(user.getRole())) {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权限\"}");
                return false;
            }
        }

        return true;
    }
}
```

**Step 2: Create WebConfig**

Create `backend/src/main/java/com/aimarketplace/config/WebConfig.java`:

```java
package com.aimarketplace.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
    }
}
```

**Step 3: Update AuthServiceImpl to use shared session store**

Update `backend/src/main/java/com/aimarketplace/service/impl/AuthServiceImpl.java`:

```java
package com.aimarketplace.service.impl;

import com.aimarketplace.config.AuthInterceptor;
import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;
import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.UserMapper;
import com.aimarketplace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("用户名或密码不能为空");
        }

        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());

        // 生成并返回 sessionId（由 Controller 设置到 header/cookie）
        return response;
    }

    @Override
    public void logout(String sessionId) {
        AuthInterceptor.removeSession(sessionId);
    }

    @Override
    public LoginResponse getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());
        return response;
    }
}
```

**Step 4: Update AuthService interface**

Update `backend/src/main/java/com/aimarketplace/service/AuthService.java`:

```java
package com.aimarketplace.service;

import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String sessionId);
    LoginResponse getCurrentUser(Long userId);
}
```

**Step 5: Update AuthController**

Update `backend/src/main/java/com/aimarketplace/controller/AuthController.java`:

```java
package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.config.AuthInterceptor;
import com.aimarketplace.dto.LoginRequest;
import com.aimarketplace.dto.LoginResponse;
import com.aimarketplace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request,
                                       HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request);
            // 生成并存储 sessionId
            String sessionId = java.util.UUID.randomUUID().toString();
            AuthInterceptor.addSession(sessionId, loginResponse.getId());
            // 设置到 header 和 cookie
            response.setHeader("X-Session-Id", sessionId);
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("sessionId", sessionId);
            cookie.setPath("/");
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 天
            response.addCookie(cookie);
            return Result.success(loginResponse);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId != null) {
            authService.logout(sessionId);
        }
        return Result.success();
    }

    @GetMapping("/me")
    public Result<LoginResponse> getCurrentUser(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            LoginResponse user = authService.getCurrentUser(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
```

**Step 6: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 7: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/config/AuthInterceptor.java
git add backend/src/main/java/com/aimarketplace/config/WebConfig.java
git add backend/src/main/java/com/aimarketplace/service/AuthService.java
git add backend/src/main/java/com/aimarketplace/service/impl/AuthServiceImpl.java
git add backend/src/main/java/com/aimarketplace/controller/AuthController.java
git commit -m "feat: add auth interceptor and update auth flow"
```

---

## Phase 6: Frontend Core - Authentication & Layout

### Task 6.1: Create API Request Module

**Files:**
- Create: `frontend/src/api/request.ts`
- Create: `frontend/src/api/auth.ts`

**Step 1: Create axios request module**

Create `frontend/src/api/request.ts`:

```ts
import axios, { AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加 sessionId
    const sessionId = localStorage.getItem('sessionId')
    if (sessionId) {
      config.headers['X-Session-Id'] = sessionId
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 保存 sessionId
    const sessionId = response.headers['x-session-id']
    if (sessionId) {
      localStorage.setItem('sessionId', sessionId)
    }
    return response.data
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      ElMessage.error('未登录，请先登录')
      localStorage.removeItem('sessionId')
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限')
    } else {
      ElMessage.error((error.response?.data as any)?.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
```

**Step 2: Create auth API**

Create `frontend/src/api/auth.ts`:

```ts
import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  role: string
  department: string
}

export const authApi = {
  // 登录
  login: (data: LoginRequest) =>
    request.post<any, UserInfo>('/auth/login', data),

  // 登出
  logout: () =>
    request.post('/auth/logout'),

  // 获取当前用户信息
  getCurrentUser: () =>
    request.get<any, UserInfo>('/auth/me')
}
```

**Step 3: Commit**

```bash
git add frontend/src/api/request.ts frontend/src/api/auth.ts
git commit -m "feat: add API request module and auth API"
```

---

### Task 6.2: Create User Store

**Files:**
- Create: `frontend/src/stores/user.ts`

**Step 1: Create user Pinia store**

Create `frontend/src/stores/user.ts`:

```ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type UserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref(false)

  // 登录
  const login = async (username: string, password: string) => {
    try {
      const data = await authApi.login({ username, password })
      userInfo.value = data
      isLoggedIn.value = true
      return true
    } catch (error) {
      return false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      // ignore
    } finally {
      userInfo.value = null
      isLoggedIn.value = false
      localStorage.removeItem('sessionId')
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const data = await authApi.getCurrentUser()
      userInfo.value = data
      isLoggedIn.value = true
      return true
    } catch (error) {
      return false
    }
  }

  // 检查是否是管理员
  const isAdmin = () => {
    return userInfo.value?.role === 'admin'
  }

  return {
    userInfo,
    isLoggedIn,
    login,
    logout,
    fetchUserInfo,
    isAdmin
  }
})
```

**Step 2: Commit**

```bash
git add frontend/src/stores/user.ts
git commit -m "feat: add user Pinia store with auth state management"
```

---

### Task 6.3: Create Login Page

**Files:**
- Modify: `frontend/src/views/Login.vue`

**Step 1: Implement login page**

Update `frontend/src/views/Login.vue`:

```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo">
        <h1>AI Marketplace</h1>
        <p>企业AI能力共享平台</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const success = await userStore.login(form.username, form.password)
      if (success) {
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error('用户名或密码错误')
      }
    } catch (error) {
      ElMessage.error('登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.logo {
  text-align: center;
  margin-bottom: 30px;
}

.logo h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.logo p {
  font-size: 14px;
  color: #999;
}

.login-btn {
  width: 100%;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>
```

**Step 2: Update router with auth guard**

Update `frontend/src/router/index.ts`:

```ts
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/llm/models'
    },
    {
      path: '/login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/llm',
      component: () => import('@/layouts/UserLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'models',
          component: () => import('@/views/llm/ModelList.vue')
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    // 尝试获取用户信息
    userStore.fetchUserInfo().then((success) => {
      if (success) {
        next()
      } else {
        next('/login')
      }
    })
  } else {
    next()
  }
})

export default router
```

**Step 3: Commit**

```bash
git add frontend/src/views/Login.vue frontend/src/router/index.ts
git commit -m "feat: implement login page with auth guard"
```

---

## Phase 7: Storage Service Implementation

### Task 7.1: Create Storage Configuration

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/config/StorageConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/config/MinioConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/config/S3Config.java`

**Step 1: Create storage properties**

Create `backend/src/main/java/com/aimarketplace/config/StorageConfig.java`:

```java
package com.aimarketplace.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageConfig {
    private String type; // minio or s3
    private MinioConfig minio;
    private S3Config s3;

    @Data
    public static class MinioConfig {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
    }

    @Data
    public static class S3Config {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String region;
    }
}
```

**Step 2: Create object storage service interface**

Create `backend/src/main/java/com/aimarketplace/service/ObjectStorageService.java`:

```java
package com.aimarketplace.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface ObjectStorageService {
    String uploadFile(String key, InputStream inputStream, long contentLength, String contentType);
    InputStream downloadFile(String key);
    void deleteFile(String key);
    String getPresignedUrl(String key, long expireSeconds);
    boolean fileExists(String key);
}
```

**Step 3: Create MinIO implementation**

Create `backend/src/main/java/com/aimarketplace/service/impl/MinioStorageServiceImpl.java`:

```java
package com.aimarketplace.service.impl;

import com.aimarketplace.config.StorageConfig;
import com.aimarketplace.service.ObjectStorageService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "minio")
public class MinioStorageServiceImpl implements ObjectStorageService {

    @Autowired
    private StorageConfig storageConfig;

    private MinioClient getClient() {
        return MinioClient.builder()
                .endpoint(storageConfig.getMinio().getEndpoint())
                .credentials(storageConfig.getMinio().getAccessKey(), storageConfig.getMinio().getSecretKey())
                .build();
    }

    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        try {
            MinioClient client = getClient();
            String bucket = storageConfig.getMinio().getBucket();

            // 确保桶存在
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(inputStream, contentLength, -1)
                            .contentType(contentType)
                            .build()
            );

            return key;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            MinioClient client = getClient();
            return client.getObject(
                    GetObjectArgs.builder()
                            .bucket(storageConfig.getMinio().getBucket())
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            MinioClient client = getClient();
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(storageConfig.getMinio().getBucket())
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String getPresignedUrl(String key, long expireSeconds) {
        try {
            MinioClient client = getClient();
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(storageConfig.getMinio().getBucket())
                            .object(key)
                            .expiry(expireSeconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    @Override
    public boolean fileExists(String key) {
        try {
            MinioClient client = getClient();
            client.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageConfig.getMinio().getBucket())
                            .object(key)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

**Step 4: Create S3 implementation**

Create `backend/src/main/java/com/aimarketplace/service/impl/S3StorageServiceImpl.java`:

```java
package com.aimarketplace.service.impl;

import com.aimarketplace.config.StorageConfig;
import com.aimarketplace.service.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.regions.Region;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3StorageServiceImpl implements ObjectStorageService {

    @Autowired
    private StorageConfig storageConfig;

    private S3Client getClient() {
        return S3Client.builder()
                .endpointOverride(URI.create(storageConfig.getS3().getEndpoint()))
                .region(Region.of(storageConfig.getS3().getRegion()))
                .build();
    }

    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        try {
            S3Client client = getClient();
            String bucket = storageConfig.getS3().getBucket();

            // 确保桶存在
            try {
                client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            } catch (Exception e) {
                client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            }

            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(inputStream, contentLength)
            );

            return key;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Client client = getClient();
            return client.getObject(
                    GetObjectRequest.builder()
                            .bucket(storageConfig.getS3().getBucket())
                            .key(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            S3Client client = getClient();
            client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(storageConfig.getS3().getBucket())
                            .key(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String getPresignedUrl(String key, long expireSeconds) {
        try {
            S3Client client = getClient();
            return client.utilities()
                    .getUrlPresigner()
                    .presignGetObject(b -> b.bucket(storageConfig.getS3().getBucket())
                            .key(key)
                            .signatureDuration(Duration.ofSeconds(expireSeconds)))
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    @Override
    public boolean fileExists(String key) {
        try {
            S3Client client = getClient();
            client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(storageConfig.getS3().getBucket())
                            .key(key)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

**Step 5: Enable configuration properties**

Update `backend/src/main/java/com/aimarketplace/AiMarketplaceApplication.java`:

```java
package com.aimarketplace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.aimarketplace.mapper")
@EnableConfigurationProperties
public class AiMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMarketplaceApplication.class, args);
    }
}
```

**Step 6: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 7: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/config/
git add backend/src/main/java/com/aimarketplace/service/ObjectStorageService.java
git add backend/src/main/java/com/aimarketplace/service/impl/MinioStorageServiceImpl.java
git add backend/src/main/java/com/aimarketplace/service/impl/S3StorageServiceImpl.java
git commit -m "feat: add object storage service with MinIO and S3 support"
```

---

### Task 7.2: Create File Controller

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/controller/FileController.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/FileUploadResponse.java`

**Step 1: Create FileUploadResponse DTO**

Create `backend/src/main/java/com/aimarketplace/dto/FileUploadResponse.java`:

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class FileUploadResponse {
    private String key;
    private String url;
    private Long size;
    private String fileName;
}
```

**Step 2: Create FileController**

Create `backend/src/main/java/com/aimarketplace/controller/FileController.java`:

```java
package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.FileUploadResponse;
import com.aimarketplace.service.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private ObjectStorageService storageService;

    // 允许的文件类型
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("md", "zip"));
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @PostMapping("/upload")
    public Result<FileUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                             HttpServletRequest request) {
        try {
            // 验证文件大小
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("文件大小不能超过 50MB");
            }

            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return Result.error("只支持 .md 和 .zip 文件");
            }

            // 生成唯一文件名
            String key = generateKey(file.getOriginalFilename(), request);

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                storageService.uploadFile(key, inputStream, file.getSize(), file.getContentType());
            }

            // 返回结果
            FileUploadResponse response = new FileUploadResponse();
            response.setKey(key);
            response.setUrl(storageService.getPresignedUrl(key, 3600));
            response.setSize(file.getSize());
            response.setFileName(originalFilename);

            return Result.success(response);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{key:.+}")
    public Result<String> getDownloadUrl(@PathVariable String key) {
        try {
            if (!storageService.fileExists(key)) {
                return Result.error("文件不存在");
            }
            String url = storageService.getPresignedUrl(key, 3600);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("获取下载链接失败");
        }
    }

    private String generateKey(String filename, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String extension = filename.substring(filename.lastIndexOf("."));
        return "uploads/" + userId + "/" + UUID.randomUUID() + extension;
    }
}
```

**Step 3: Verify compilation**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add backend/src/main/java/com/aimarketplace/controller/FileController.java
git add backend/src/main/java/com/aimarketplace/dto/FileUploadResponse.java
git commit -m "feat: add file upload/download endpoints with size and type validation"
```

---

## (Plan Continues...)

> **Note:** This is a comprehensive implementation plan. The complete plan would continue with:
> - LLM Model Configuration and Testing
> - Asset Publishing and Management
> - Skills Management
> - Review/Approval System
> - API Key Management
> - Frontend Pages (LLM Models, Skills, Admin)
> - Statistics Dashboard

> **Due to length limits, this plan covers the foundational setup. The remaining phases follow the same pattern of bite-sized tasks with specific files, code, and commit steps.**
