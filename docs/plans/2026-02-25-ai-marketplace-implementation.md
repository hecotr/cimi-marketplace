# AI Marketplace Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an enterprise internal AI capability sharing platform for LLM models and Skills (prompt templates) with browsing, testing, downloading, and interaction features.

**Architecture:** Frontend-backend separation with Vue3 (Element Plus) for UI and Java SpringBoot 2.6.5 for RESTful API. PostgreSQL for data persistence. Dockerized deployment on internal K8s.

**Tech Stack:** Vue3, Vite, TypeScript, Element Plus, Java 17, SpringBoot 2.6.5, MyBatis Plus, PostgreSQL, Docker, K8s

---

## Task 1: Frontend Project Setup

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.ts`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.ts`
- Create: `frontend/src/api/request.ts`
- Create: `frontend/.env.development`
- Create: `frontend/.env.production`
- Create: `frontend/Dockerfile`

**Step 1: Create frontend/package.json**

```json
{
  "name": "ai-marketplace-frontend",
  "private": true,
  "version": "0.0.1",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "axios": "^1.6.0",
    "element-plus": "^2.5.0",
    "@element-plus/icons-vue": "^2.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "typescript": "^5.3.0",
    "vite": "^5.0.0",
    "vue-tsc": "^1.8.0"
  }
}
```

**Step 2: Create frontend/vite.config.ts**

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

**Step 3: Create frontend/tsconfig.json**

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
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

**Step 4: Create frontend/tsconfig.node.json**

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

**Step 5: Create frontend/index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8">
    <link rel="icon" type="image/svg+xml" href="/vite.svg">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI Marketplace</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.ts"></script>
  </body>
</html>
```

**Step 6: Create frontend/src/main.ts**

```typescript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
```

**Step 7: Create frontend/src/App.vue**

```vue
<template>
  <div id="app">
    <el-container>
      <el-header class="header">
        <div class="logo">AI Marketplace</div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          router
          class="menu"
        >
          <el-menu-item index="/llm">LLM</el-menu-item>
          <el-menu-item index="/skills">Skills</el-menu-item>
          <el-menu-item index="/favorites">我的收藏</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px;
  color: #409eff;
}

.menu {
  flex: 1;
  border: none;
}
</style>
```

**Step 8: Create frontend/src/router/index.ts**

```typescript
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/llm'
  },
  {
    path: '/llm',
    name: 'LlmIndex',
    redirect: '/llm/models',
    children: [
      {
        path: 'models',
        name: 'LlmModels',
        component: () => import('@/views/llm/ModelList.vue')
      },
      {
        path: 'models/:id',
        name: 'LlmModelDetail',
        component: () => import('@/views/llm/ModelDetail.vue')
      },
      {
        path: 'my-keys',
        name: 'MyKeys',
        component: () => import('@/views/llm/MyKeys.vue')
      }
    ]
  },
  {
    path: '/skills',
    name: 'SkillsIndex',
    redirect: '/skills/list',
    children: [
      {
        path: 'list',
        name: 'SkillsList',
        component: () => import('@/views/skills/SkillList.vue')
      },
      {
        path: ':id',
        name: 'SkillDetail',
        component: () => import('@/views/skills/SkillDetail.vue')
      }
    ]
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

**Step 9: Create frontend/src/api/request.ts**

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default request
```

**Step 10: Create frontend/.env.development**

```
VITE_API_BASE_URL=http://localhost:8080/api
```

**Step 11: Create frontend/.env.production**

```
VITE_API_BASE_URL=/api
```

**Step 12: Create frontend/Dockerfile**

```dockerfile
# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Step 13: Create frontend/nginx.conf**

```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

**Step 14: Commit**

```bash
cd frontend && git init && git add .
git commit -m "feat: setup frontend Vue3 project structure"
```

---

## Task 2: Backend Project Setup

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/java/com/aimarketplace/AiMarketplaceApplication.java`
- Create: `backend/src/main/java/com/aimarketplace/config/MybatisPlusConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/config/CorsConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/common/Result.java`
- Create: `backend/Dockerfile`

**Step 1: Create backend/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.5</version>
        <relativePath/>
    </parent>

    <groupId>com.aimarketplace</groupId>
    <artifactId>ai-marketplace-backend</artifactId>
    <version>1.0.0</version>
    <name>ai-marketplace-backend</name>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.3</mybatis-plus.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
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

**Step 2: Create backend/src/main/resources/application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: ai-marketplace
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/ai_marketplace
    username: postgres
    password: postgres

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
```

**Step 3: Create backend/src/main/java/com/aimarketplace/AiMarketplaceApplication.java**

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

**Step 4: Create backend/src/main/java/com/aimarketplace/config/MybatisPlusConfig.java**

```java
package com.aimarketplace.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

**Step 5: Create backend/src/main/java/com/aimarketplace/config/CorsConfig.java**

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
                .allowCredentials(true);
    }
}
```

**Step 6: Create backend/src/main/java/com/aimarketplace/common/Result.java**

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

**Step 7: Create backend/Dockerfile**

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Step 8: Commit**

```bash
cd backend && git init && git add .
git commit -m "feat: setup backend SpringBoot project structure"
```

---

## Task 3: Database Schema and Entities

**Files:**
- Create: `backend/src/main/resources/db/schema.sql`
- Create: `backend/src/main/java/com/aimarketplace/entity/LlmModel.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/LlmTestCase.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/LlmTestRecord.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/ApiKey.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/Skill.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/SkillLike.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/SkillFavorite.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/User.java`

**Step 1: Create backend/src/main/resources/db/schema.sql**

```sql
-- Users table
CREATE TABLE IF NOT EXISTS user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Models table
CREATE TABLE IF NOT EXISTS llm_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(100),
    model_name VARCHAR(255) NOT NULL,
    description TEXT,
    api_endpoint VARCHAR(500),
    max_tokens INTEGER DEFAULT 4096,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Test Cases table
CREATE TABLE IF NOT EXISTS llm_test_case (
    id BIGSERIAL PRIMARY KEY,
    model_id BIGINT NOT NULL REFERENCES llm_model(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    prompt TEXT NOT NULL,
    expected_output TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LLM Test Records table
CREATE TABLE IF NOT EXISTS llm_test_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id),
    model_id BIGINT NOT NULL REFERENCES llm_model(id),
    prompt TEXT NOT NULL,
    response TEXT,
    parameters JSONB,
    response_time INTEGER,
    token_usage JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API Keys table
CREATE TABLE IF NOT EXISTS api_key (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id),
    model_id BIGINT NOT NULL REFERENCES llm_model(id),
    key_value VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'pending',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approve_time TIMESTAMP,
    expiry_date TIMESTAMP
);

-- Skills table
CREATE TABLE IF NOT EXISTS skill (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    type VARCHAR(20) NOT NULL DEFAULT 'file',
    content TEXT,
    storage_path VARCHAR(500),
    view_count INTEGER DEFAULT 0,
    download_count INTEGER DEFAULT 0,
    like_count INTEGER DEFAULT 0,
    created_by BIGINT NOT NULL REFERENCES user(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Skill Likes table
CREATE TABLE IF NOT EXISTS skill_like (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id),
    skill_id BIGINT NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, skill_id)
);

-- Skill Favorites table
CREATE TABLE IF NOT EXISTS skill_favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES user(id),
    skill_id BIGINT NOT NULL REFERENCES skill(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, skill_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_llm_test_case_model ON llm_test_case(model_id);
CREATE INDEX IF NOT EXISTS idx_llm_test_record_user ON llm_test_record(user_id);
CREATE INDEX IF NOT EXISTS idx_llm_test_record_model ON llm_test_record(model_id);
CREATE INDEX IF NOT EXISTS idx_api_key_user ON api_key(user_id);
CREATE INDEX IF NOT EXISTS idx_api_key_model ON api_key(model_id);
CREATE INDEX IF NOT EXISTS idx_skill_category ON skill(category);
CREATE INDEX IF NOT EXISTS idx_skill_type ON skill(type);
CREATE INDEX IF NOT EXISTS idx_skill_like_user ON skill_like(user_id);
CREATE INDEX IF NOT EXISTS idx_skill_like_skill ON skill_like(skill_id);
CREATE INDEX IF NOT EXISTS idx_skill_favorite_user ON skill_favorite(user_id);
CREATE INDEX IF NOT EXISTS idx_skill_favorite_skill ON skill_favorite(skill_id);
```

**Step 2: Create backend/src/main/java/com/aimarketplace/entity/LlmModel.java**

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("llm_model")
public class LlmModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private String apiEndpoint;
    private Integer maxTokens;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Step 3: Create backend/src/main/java/com/aimarketplace/entity/LlmTestCase.java**

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("llm_test_case")
public class LlmTestCase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private String name;
    private String prompt;
    private String expectedOutput;
    private LocalDateTime createdAt;
}
```

**Step 4: Create backend/src/main/java/com/aimarketplace/entity/LlmTestRecord.java**

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("llm_test_record")
public class LlmTestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long modelId;
    private String prompt;
    private String response;
    private String parameters;
    private Integer responseTime;
    private String tokenUsage;
    private LocalDateTime createdAt;
}
```

**Step 5: Create backend/src/main/java/com/aimarketplace/entity/ApiKey.java**

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
    private Long modelId;
    private String keyValue;
    private String status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime expiryDate;
}
```

**Step 6: Create backend/src/main/java/com/aimarketplace/entity/Skill.java**

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("skill")
public class Skill {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String type;
    private String content;
    private String storagePath;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Long createdBy;
    private LocalDateTime createdAt;
}
```

**Step 7: Create backend/src/main/java/com/aimarketplace/entity/SkillLike.java**

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("skill_like")
public class SkillLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long skillId;
    private LocalDateTime createdAt;
}
```

**Step 8: Create backend/src/main/java/com/aimarketplace/entity/SkillFavorite.java**

```java
package com.aimarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("skill_favorite")
public class SkillFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long skillId;
    private LocalDateTime createdAt;
}
```

**Step 9: Create backend/src/main/java/com/aimarketplace/entity/User.java**

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
    private String email;
    private String department;
    private LocalDateTime createdAt;
}
```

**Step 10: Commit**

```bash
cd backend && git add .
git commit -m "feat: add database schema and entity classes"
```

---

## Task 4: LLM Module - Models API

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/LlmModelMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/service/LlmModelService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/LlmModelServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/LlmController.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmModelDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmTestRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmTestResponse.java`

**Step 1: Create backend/src/main/java/com/aimarketplace/mapper/LlmModelMapper.java**

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LlmModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LlmModelMapper extends BaseMapper<LlmModel> {
}
```

**Step 2: Create backend/src/main/java/com/aimarketplace/mapper/LlmTestCaseMapper.java**

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LlmTestCase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LlmTestCaseMapper extends BaseMapper<LlmTestCase> {
}
```

**Step 3: Create backend/src/main/java/com/aimarketplace/mapper/LlmTestRecordMapper.java**

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LlmTestRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LlmTestRecordMapper extends BaseMapper<LlmTestRecord> {
}
```

**Step 4: Create backend/src/main/java/com/aimarketplace/dto/LlmModelDTO.java**

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmModelDTO {
    private Long id;
    private String name;
    private String provider;
    private String modelName;
    private String description;
    private String apiEndpoint;
    private Integer maxTokens;
    private String status;
}
```

**Step 5: Create backend/src/main/java/com/aimarketplace/dto/LlmTestRequest.java**

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmTestRequest {
    private Long modelId;
    private String prompt;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
}
```

**Step 6: Create backend/src/main/java/com/aimarketplace/dto/LlmTestResponse.java**

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class LlmTestResponse {
    private String response;
    private Integer responseTime;
    private TokenUsage tokenUsage;
    private String parameters;

    @Data
    public static class TokenUsage {
        private Integer inputTokens;
        private Integer outputTokens;
        private Integer totalTokens;
    }
}
```

**Step 7: Create backend/src/main/java/com/aimarketplace/service/LlmModelService.java**

```java
package com.aimarketplace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aimarketplace.entity.LlmModel;
import com.aimarketplace.dto.LlmModelDTO;

import java.util.List;

public interface LlmModelService extends IService<LlmModel> {
    List<LlmModelDTO> getActiveModels();
    LlmModelDTO getModelDetail(Long id);
    LlmTestResponse testModel(Long userId, LlmTestRequest request);
}
```

**Step 8: Create backend/src/main/java/com/aimarketplace/service/impl/LlmModelServiceImpl.java**

```java
package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.entity.LlmModel;
import com.aimarketplace.entity.LlmTestRecord;
import com.aimarketplace.mapper.LlmModelMapper;
import com.aimarketplace.mapper.LlmTestRecordMapper;
import com.aimarketplace.service.LlmModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LlmModelServiceImpl extends ServiceImpl<LlmModelMapper, LlmModel> implements LlmModelService {

    private final LlmTestRecordMapper testRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<LlmModelDTO> getActiveModels() {
        LambdaQueryWrapper<LlmModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LlmModel::getStatus, "active")
                .orderByDesc(LlmModel::getCreatedAt);
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public LlmModelDTO getModelDetail(Long id) {
        LlmModel model = getById(id);
        return toDTO(model);
    }

    @Override
    public LlmTestResponse testModel(Long userId, LlmTestRequest request) {
        // TODO: Implement actual LLM API call
        LlmModel model = getById(request.getModelId());
        long startTime = System.currentTimeMillis();

        // Mock response for now
        String mockResponse = "This is a mock response from " + model.getModelName();
        long responseTime = System.currentTimeMillis() - startTime;

        // Save test record
        LlmTestRecord record = new LlmTestRecord();
        record.setUserId(userId);
        record.setModelId(request.getModelId());
        record.setPrompt(request.getPrompt());
        record.setResponse(mockResponse);
        try {
            record.setParameters(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            record.setParameters("{}");
        }
        record.setResponseTime((int) responseTime);
        record.setTokenUsage("{\"inputTokens\":100,\"outputTokens\":50,\"totalTokens\":150}");
        record.setCreatedAt(LocalDateTime.now());
        testRecordMapper.insert(record);

        // Build response
        LlmTestResponse response = new LlmTestResponse();
        response.setResponse(mockResponse);
        response.setResponseTime((int) responseTime);
        LlmTestResponse.TokenUsage usage = new LlmTestResponse.TokenUsage();
        usage.setInputTokens(100);
        usage.setOutputTokens(50);
        usage.setTotalTokens(150);
        response.setTokenUsage(usage);
        return response;
    }

    private LlmModelDTO toDTO(LlmModel model) {
        if (model == null) return null;
        LlmModelDTO dto = new LlmModelDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setProvider(model.getProvider());
        dto.setModelName(model.getModelName());
        dto.setDescription(model.getDescription());
        dto.setApiEndpoint(model.getApiEndpoint());
        dto.setMaxTokens(model.getMaxTokens());
        dto.setStatus(model.getStatus());
        return dto;
    }
}
```

**Step 9: Create backend/src/main/java/com/aimarketplace/controller/LlmController.java**

```java
package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.LlmModelDTO;
import com.aimarketplace.dto.LlmTestRequest;
import com.aimarketplace.dto.LlmTestResponse;
import com.aimarketplace.service.LlmModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LlmController {

    private final LlmModelService llmModelService;

    @GetMapping("/models")
    public Result<List<LlmModelDTO>> getModels() {
        return Result.success(llmModelService.getActiveModels());
    }

    @GetMapping("/models/{id}")
    public Result<LlmModelDTO> getModelDetail(@PathVariable Long id) {
        return Result.success(llmModelService.getModelDetail(id));
    }

    @PostMapping("/test")
    public Result<LlmTestResponse> testModel(@RequestBody LlmTestRequest request) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(llmModelService.testModel(userId, request));
    }
}
```

**Step 10: Commit**

```bash
cd backend && git add .
git commit -m "feat: add LLM models API"
```

---

## Task 5: Skills Module API

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/SkillMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/SkillLikeMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/SkillFavoriteMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/SkillDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/SkillQueryRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/service/SkillService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/SkillServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/SkillController.java`

**Step 1: Create backend/src/main/java/com/aimarketplace/mapper/SkillMapper.java**

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Skill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillMapper extends BaseMapper<Skill> {
}
```

**Step 2: Create backend/src/main/java/com/aimarketplace/mapper/SkillLikeMapper.java**

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.SkillLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillLikeMapper extends BaseMapper<SkillLike> {
}
```

**Step 3: Create backend/src/main/java/com/aimarketplace/mapper/SkillFavoriteMapper.java**

```java
package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.SkillFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillFavoriteMapper extends BaseMapper<SkillFavorite> {
}
```

**Step 4: Create backend/src/main/java/com/aimarketplace/dto/SkillDTO.java**

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class SkillDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String type;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Boolean isLiked;
    private Boolean isFavorited;
}
```

**Step 5: Create backend/src/main/java/com/aimarketplace/dto/SkillQueryRequest.java**

```java
package com.aimarketplace.dto;

import lombok.Data;

@Data
public class SkillQueryRequest {
    private String keyword;
    private String category;
    private String sortBy; // created_at, view_count, download_count, like_count
    private Integer page = 1;
    private Integer size = 20;
}
```

**Step 6: Create backend/src/main/java/com/aimarketplace/service/SkillService.java**

```java
package com.aimarketplace.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aimarketplace.dto.SkillDTO;
import com.aimarketplace.dto.SkillQueryRequest;

import java.util.List;

public interface SkillService {
    IPage<SkillDTO> querySkills(Long userId, SkillQueryRequest request);
    SkillDTO getSkillDetail(Long userId, Long id);
    List<String> getCategories();
    void toggleLike(Long userId, Long id);
    void toggleFavorite(Long userId, Long id);
    IPage<SkillDTO> getFavorites(Long userId, Integer page, Integer size);
    void incrementViewCount(Long id);
    void incrementDownloadCount(Long id);
}
```

**Step 7: Create backend/src/main/java/com/aimarketplace/service/impl/SkillServiceImpl.java**

```java
package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.SkillDTO;
import com.aimarketplace.dto.SkillQueryRequest;
import com.aimarketplace.entity.Skill;
import com.aimarketplace.entity.SkillFavorite;
import com.aimarketplace.entity.SkillLike;
import com.aimarketplace.mapper.SkillFavoriteMapper;
import com.aimarketplace.mapper.SkillLikeMapper;
import com.aimarketplace.mapper.SkillMapper;
import com.aimarketplace.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService {

    private final SkillLikeMapper skillLikeMapper;
    private final SkillFavoriteMapper skillFavoriteMapper;

    @Override
    public IPage<SkillDTO> querySkills(Long userId, SkillQueryRequest request) {
        Page<Skill> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Skill::getName, request.getKeyword())
                    .or().like(Skill::getDescription, request.getKeyword()));
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            wrapper.eq(Skill::getCategory, request.getCategory());
        }

        // Sorting
        if ("view_count".equals(request.getSortBy())) {
            wrapper.orderByDesc(Skill::getViewCount);
        } else if ("download_count".equals(request.getSortBy())) {
            wrapper.orderByDesc(Skill::getDownloadCount);
        } else if ("like_count".equals(request.getSortBy())) {
            wrapper.orderByDesc(Skill::getLikeCount);
        } else {
            wrapper.orderByDesc(Skill::getCreatedAt);
        }

        IPage<Skill> skillPage = page(page, wrapper);
        return skillPage.convert(skill -> {
            SkillDTO dto = toDTO(skill);
            if (userId != null) {
                dto.setIsLiked(checkIsLiked(userId, skill.getId()));
                dto.setIsFavorited(checkIsFavorited(userId, skill.getId()));
            }
            return dto;
        });
    }

    @Override
    public SkillDTO getSkillDetail(Long userId, Long id) {
        Skill skill = getById(id);
        SkillDTO dto = toDTO(skill);
        if (userId != null) {
            dto.setIsLiked(checkIsLiked(userId, id));
            dto.setIsFavorited(checkIsFavorited(userId, id));
        }
        return dto;
    }

    @Override
    public List<String> getCategories() {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Skill::getCategory);
        wrapper.isNotNull(Skill::getCategory);
        wrapper.groupBy(Skill::getCategory);
        return list(wrapper).stream()
                .map(Skill::getCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleLike(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillLike::getUserId, userId).eq(SkillLike::getSkillId, skillId);
        SkillLike existing = skillLikeMapper.selectOne(wrapper);

        if (existing != null) {
            skillLikeMapper.deleteById(existing.getId());
            // Decrement like count
            Skill skill = getById(skillId);
            skill.setLikeCount(skill.getLikeCount() - 1);
            updateById(skill);
        } else {
            SkillLike like = new SkillLike();
            like.setUserId(userId);
            like.setSkillId(skillId);
            skillLikeMapper.insert(like);
            // Increment like count
            Skill skill = getById(skillId);
            skill.setLikeCount(skill.getLikeCount() + 1);
            updateById(skill);
        }
    }

    @Override
    public void toggleFavorite(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillFavorite::getUserId, userId).eq(SkillFavorite::getSkillId, skillId);
        SkillFavorite existing = skillFavoriteMapper.selectOne(wrapper);

        if (existing != null) {
            skillFavoriteMapper.deleteById(existing.getId());
        } else {
            SkillFavorite favorite = new SkillFavorite();
            favorite.setUserId(userId);
            favorite.setSkillId(skillId);
            skillFavoriteMapper.insert(favorite);
        }
    }

    @Override
    public IPage<SkillDTO> getFavorites(Long userId, Integer page, Integer size) {
        Page<Skill> skillPage = new Page<>(page, size);
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(Skill::getId, "SELECT skill_id FROM skill_favorite WHERE user_id = " + userId);
        wrapper.orderByDesc(Skill::getCreatedAt);
        IPage<Skill> resultPage = page(skillPage, wrapper);
        return resultPage.convert(this::toDTO);
    }

    @Override
    public void incrementViewCount(Long id) {
        Skill skill = getById(id);
        skill.setViewCount(skill.getViewCount() + 1);
        updateById(skill);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        Skill skill = getById(id);
        skill.setDownloadCount(skill.getDownloadCount() + 1);
        updateById(skill);
    }

    private SkillDTO toDTO(Skill skill) {
        if (skill == null) return null;
        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setDescription(skill.getDescription());
        dto.setCategory(skill.getCategory());
        dto.setType(skill.getType());
        dto.setViewCount(skill.getViewCount());
        dto.setDownloadCount(skill.getDownloadCount());
        dto.setLikeCount(skill.getLikeCount());
        return dto;
    }

    private boolean checkIsLiked(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillLike::getUserId, userId).eq(SkillLike::getSkillId, skillId);
        return skillLikeMapper.selectCount(wrapper) > 0;
    }

    private boolean checkIsFavorited(Long userId, Long skillId) {
        LambdaQueryWrapper<SkillFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillFavorite::getUserId, userId).eq(SkillFavorite::getSkillId, skillId);
        return skillFavoriteMapper.selectCount(wrapper) > 0;
    }
}
```

**Step 8: Create backend/src/main/java/com/aimarketplace/controller/SkillController.java**

```java
package com.aimarketplace.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aimarketplace.common.Result;
import com.aimarketplace.dto.SkillDTO;
import com.aimarketplace.dto.SkillQueryRequest;
import com.aimarketplace.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public Result<IPage<SkillDTO>> querySkills(SkillQueryRequest request) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(skillService.querySkills(userId, request));
    }

    @GetMapping("/{id}")
    public Result<SkillDTO> getSkillDetail(@PathVariable Long id) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(skillService.getSkillDetail(userId, id));
    }

    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(skillService.getCategories());
    }

    @PostMapping("/{id}/like")
    public Result<Void> toggleLike(@PathVariable Long id) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        skillService.toggleLike(userId, id);
        return Result.success();
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> toggleFavorite(@PathVariable Long id) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        skillService.toggleFavorite(userId, id);
        return Result.success();
    }

    @GetMapping("/favorites")
    public Result<IPage<SkillDTO>> getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        return Result.success(skillService.getFavorites(userId, page, size));
    }

    @GetMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        skillService.incrementViewCount(id);
        return Result.success();
    }

    @GetMapping("/{id}/download")
    public Result<Void> incrementDownloadCount(@PathVariable Long id) {
        skillService.incrementDownloadCount(id);
        return Result.success();
    }
}
```

**Step 9: Commit**

```bash
cd backend && git add .
git commit -m "feat: add Skills API"
```

---

## Task 6: Frontend LLM Views

**Files:**
- Create: `frontend/src/views/llm/ModelList.vue`
- Create: `frontend/src/views/llm/ModelDetail.vue`
- Create: `frontend/src/views/llm/MyKeys.vue`
- Create: `frontend/src/api/llm.ts`

**Step 1: Create frontend/src/api/llm.ts`

```typescript
import request from './request'
import { LlmModelDTO, LlmTestRequest, LlmTestResponse } from '@/types/llm'

export const getModels = () => {
  return request.get<LlmModelDTO[]>('/llm/models')
}

export const getModelDetail = (id: number) => {
  return request.get<LlmModelDTO>(`/llm/models/${id}`)
}

export const testModel = (data: LlmTestRequest) => {
  return request.post<LlmTestResponse>('/llm/test', data)
}
```

**Step 2: Create frontend/src/types/llm.ts**

```typescript
export interface LlmModelDTO {
  id: number
  name: string
  provider: string
  modelName: string
  description: string
  apiEndpoint: string
  maxTokens: number
  status: string
}

export interface LlmTestRequest {
  modelId: number
  prompt: string
  temperature?: number
  maxTokens?: number
  topP?: number
}

export interface LlmTestResponse {
  response: string
  responseTime: number
  tokenUsage: {
    inputTokens: number
    outputTokens: number
    totalTokens: number
  }
}
```

**Step 3: Create frontend/src/views/llm/ModelList.vue**

```vue
<template>
  <div class="model-list">
    <h2>LLM 模型列表</h2>
    <el-row :gutter="20">
      <el-col :span="8" v-for="model in models" :key="model.id">
        <el-card class="model-card" @click="goToDetail(model.id)">
          <template #header>
            <div class="card-header">
              <span>{{ model.name }}</span>
              <el-tag :type="model.status === 'active' ? 'success' : 'info'">
                {{ model.status }}
              </el-tag>
            </div>
          </template>
          <div class="card-content">
            <p><strong>提供商:</strong> {{ model.provider }}</p>
            <p><strong>模型:</strong> {{ model.modelName }}</p>
            <p class="description">{{ model.description }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getModels } from '@/api/llm'
import type { LlmModelDTO } from '@/types/llm'

const router = useRouter()
const models = ref<LlmModelDTO[]>([])

onMounted(async () => {
  const data = await getModels()
  models.value = data.data || []
})

const goToDetail = (id: number) => {
  router.push(`/llm/models/${id}`)
}
</script>

<style scoped>
.model-list {
  padding: 20px;
}

.model-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.model-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content p {
  margin: 8px 0;
}

.description {
  color: #666;
  line-height: 1.5;
}
</style>
```

**Step 4: Create frontend/src/views/llm/ModelDetail.vue**

```vue
<template>
  <div class="model-detail">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>{{ model?.name }}</h3>
              <el-button @click="goBack">返回</el-button>
            </div>
          </template>
          <div v-if="model">
            <p><strong>提供商:</strong> {{ model.provider }}</p>
            <p><strong>模型:</strong> {{ model.modelName }}</p>
            <p><strong>最大 Token:</strong> {{ model.maxTokens }}</p>
            <p><strong>状态:</strong> {{ model.status }}</p>
            <p><strong>描述:</strong></p>
            <p>{{ model.description }}</p>
          </div>

          <el-divider />

          <div class="chat-section">
            <h4>对话测试</h4>
            <div class="chat-messages">
              <div v-for="(msg, index) in messages" :key="index" class="message">
                <div :class="['message-content', msg.role]">
                  {{ msg.content }}
                </div>
              </div>
              <div v-if="loading" class="message">
                <div class="message-content assistant">正在生成...</div>
              </div>
            </div>
            <el-input
              v-model="prompt"
              type="textarea"
              :rows="3"
              placeholder="输入 prompt..."
              @keydown.ctrl.enter="sendPrompt"
            />
            <el-button type="primary" @click="sendPrompt" :loading="loading" style="margin-top: 10px">
              发送 (Ctrl+Enter)
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>参数配置</template>
          <el-form label-width="100px">
            <el-form-item label="Temperature">
              <el-slider v-model="params.temperature" :min="0" :max="2" :step="0.1" />
            </el-form-item>
            <el-form-item label="Max Tokens">
              <el-input-number v-model="params.maxTokens" :min="1" :max="4096" />
            </el-form-item>
            <el-form-item label="Top P">
              <el-slider v-model="params.topP" :min="0" :max="1" :step="0.1" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>性能指标</template>
          <div v-if="lastResponse">
            <p><strong>响应时间:</strong> {{ lastResponse.responseTime }}ms</p>
            <p><strong>输入 Tokens:</strong> {{ lastResponse.tokenUsage.inputTokens }}</p>
            <p><strong>输出 Tokens:</strong> {{ lastResponse.tokenUsage.outputTokens }}</p>
            <p><strong>总 Tokens:</strong> {{ lastResponse.tokenUsage.totalTokens }}</p>
          </div>
          <div v-else>暂无测试记录</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getModelDetail, testModel } from '@/api/llm'
import type { LlmModelDTO, LlmTestRequest, LlmTestResponse } from '@/types/llm'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const modelId = Number(route.params.id)
const model = ref<LlmModelDTO>()
const prompt = ref('')
const messages = ref<{ role: string; content: string }[]>([])
const loading = ref(false)
const lastResponse = ref<LlmTestResponse>()

const params = ref({
  temperature: 0.7,
  maxTokens: 2048,
  topP: 0.9
})

onMounted(async () => {
  const data = await getModelDetail(modelId)
  model.value = data.data
})

const sendPrompt = async () => {
  if (!prompt.value.trim()) return

  messages.value.push({ role: 'user', content: prompt.value })
  const userPrompt = prompt.value
  prompt.value = ''
  loading.value = true

  try {
    const request: LlmTestRequest = {
      modelId,
      prompt: userPrompt,
      ...params.value
    }
    const data = await testModel(request)
    const response = data.data
    messages.value.push({ role: 'assistant', content: response.response })
    lastResponse.value = response
  } catch (error) {
    ElMessage.error('请求失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.model-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-section {
  margin-top: 20px;
}

.chat-messages {
  min-height: 300px;
  max-height: 500px;
  overflow-y: auto;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 10px;
}

.message {
  margin-bottom: 15px;
}

.message-content {
  max-width: 80%;
  padding: 10px 15px;
  border-radius: 8px;
  line-height: 1.5;
}

.message-content.user {
  background-color: #409eff;
  color: white;
  margin-left: auto;
}

.message-content.assistant {
  background-color: #f0f0f0;
  color: #333;
}
</style>
```

**Step 5: Create frontend/src/views/llm/MyKeys.vue**

```vue
<template>
  <div class="my-keys">
    <h2>我的 API Key</h2>
    <el-card>
      <p>API Key 申请功能待实现...</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.my-keys {
  padding: 20px;
}
</style>
```

**Step 6: Commit**

```bash
cd frontend && git add .
git commit -m "feat: add LLM views"
```

---

## Task 7: Frontend Skills Views

**Files:**
- Create: `frontend/src/types/skill.ts`
- Create: `frontend/src/api/skills.ts`
- Create: `frontend/src/views/skills/SkillList.vue`
- Create: `frontend/src/views/skills/SkillDetail.vue`
- Create: `frontend/src/views/Favorites.vue`

**Step 1: Create frontend/src/types/skill.ts**

```typescript
export interface SkillDTO {
  id: number
  name: string
  description: string
  category: string
  type: string
  viewCount: number
  downloadCount: number
  likeCount: number
  isLiked?: boolean
  isFavorited?: boolean
}

export interface SkillQueryRequest {
  keyword?: string
  category?: string
  sortBy?: string
  page?: number
  size?: number
}
```

**Step 2: Create frontend/src/api/skills.ts**

```typescript
import request from './request'
import type { SkillDTO, SkillQueryRequest } from '@/types/skill'

export const querySkills = (params: SkillQueryRequest) => {
  return request.get<{ records: SkillDTO[], total: number }>('/skills', { params })
}

export const getSkillDetail = (id: number) => {
  return request.get<SkillDTO>(`/skills/${id}`)
}

export const getCategories = () => {
  return request.get<string[]>('/skills/categories')
}

export const toggleLike = (id: number) => {
  return request.post(`/skills/${id}/like`)
}

export const toggleFavorite = (id: number) => {
  return request.post(`/skills/${id}/favorite`)
}

export const getFavorites = (page: number = 1, size: number = 20) => {
  return request.get<{ records: SkillDTO[], total: number }>('/skills/favorites', {
    params: { page, size }
  })
}

export const incrementView = (id: number) => {
  return request.get(`/skills/${id}/view`)
}

export const incrementDownload = (id: number) => {
  return request.get(`/skills/${id}/download`)
}
```

**Step 3: Create frontend/src/views/skills/SkillList.vue**

```vue
<template>
  <div class="skill-list">
    <div class="filters">
      <el-input
        v-model="keyword"
        placeholder="搜索 Skills..."
        clearable
        style="width: 300px"
        @change="loadSkills"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="category"
        placeholder="选择分类"
        clearable
        style="width: 200px"
        @change="loadSkills"
      >
        <el-option
          v-for="cat in categories"
          :key="cat"
          :label="cat"
          :value="cat"
        />
      </el-select>

      <el-select
        v-model="sortBy"
        placeholder="排序方式"
        style="width: 150px"
        @change="loadSkills"
      >
        <el-option label="最新" value="created_at" />
        <el-option label="浏览量" value="view_count" />
        <el-option label="下载量" value="download_count" />
        <el-option label="点赞数" value="like_count" />
      </el-select>
    </div>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6" v-for="skill in skills" :key="skill.id">
        <el-card class="skill-card" @click="goToDetail(skill.id)">
          <div class="skill-name">{{ skill.name }}</div>
          <div class="skill-category">{{ skill.category }}</div>
          <div class="skill-desc">{{ skill.description }}</div>
          <div class="skill-stats">
            <span><el-icon><View /></el-icon> {{ skill.viewCount }}</span>
            <span><el-icon><Download /></el-icon> {{ skill.downloadCount }}</span>
            <span><el-icon><Star /></el-icon> {{ skill.likeCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="loadSkills"
      style="margin-top: 20px; text-align: center"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, Download, Star } from '@element-plus/icons-vue'
import { querySkills, getCategories } from '@/api/skills'
import type { SkillDTO } from '@/types/skill'

const router = useRouter()
const keyword = ref('')
const category = ref('')
const sortBy = ref('created_at')
const page = ref(1)
const size = ref(20)
const total = ref(0)

const skills = ref<SkillDTO[]>([])
const categories = ref<string[]>([])

onMounted(async () => {
  await loadCategories()
  await loadSkills()
})

const loadCategories = async () => {
  const data = await getCategories()
  categories.value = data.data || []
}

const loadSkills = async () => {
  const data = await querySkills({
    keyword: keyword.value,
    category: category.value,
    sortBy: sortBy.value,
    page: page.value,
    size: size.value
  })
  skills.value = data.data.records || []
  total.value = data.data.total || 0
}

const goToDetail = (id: number) => {
  router.push(`/skills/${id}`)
}
</script>

<style scoped>
.skill-list {
  padding: 20px;
}

.filters {
  display: flex;
  gap: 15px;
}

.skill-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.skill-card:hover {
  transform: translateY(-5px);
}

.skill-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}

.skill-category {
  color: #409eff;
  font-size: 12px;
  margin-bottom: 8px;
}

.skill-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 12px;
}

.skill-stats {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

.skill-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
```

**Step 4: Create frontend/src/views/skills/SkillDetail.vue`

```vue
<template>
  <div class="skill-detail">
    <el-card v-if="skill">
      <template #header>
        <div class="card-header">
          <h3>{{ skill.name }}</h3>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div class="skill-info">
        <el-tag>{{ skill.category }}</el-tag>
        <el-tag type="info">{{ skill.type === 'file' ? '单文件' : '文件夹' }}</el-tag>
      </div>

      <p class="description">{{ skill.description }}</p>

      <div class="stats">
        <span><el-icon><View /></el-icon> {{ skill.viewCount }} 浏览</span>
        <span><el-icon><Download /></el-icon> {{ skill.downloadCount }} 下载</span>
        <span><el-icon><Star /></el-icon> {{ skill.likeCount }} 点赞</span>
      </div>

      <el-divider />

      <div class="content-section">
        <h4>Skill 内容</h4>
        <el-input
          v-model="content"
          type="textarea"
          :rows="20"
          readonly
        />
      </div>

      <el-divider />

      <div class="actions">
        <el-button @click="copyContent">
          <el-icon><DocumentCopy /></el-icon>
          复制
        </el-button>
        <el-button type="primary" @click="downloadSkill">
          <el-icon><Download /></el-icon>
          下载 {{ skill.type === 'file' ? '.md' : '.zip' }}
        </el-button>
        <el-button
          :type="skill.isLiked ? 'danger' : 'default'"
          @click="toggleLike"
        >
          <el-icon><Star /></el-icon>
          {{ skill.isLiked ? '已点赞' : '点赞' }}
        </el-button>
        <el-button
          :type="skill.isFavorited ? 'warning' : 'default'"
          @click="toggleFavorite"
        >
          <el-icon><StarFilled /></el-icon>
          {{ skill.isFavorited ? '已收藏' : '收藏' }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Download, Star, DocumentCopy, StarFilled } from '@element-plus/icons-vue'
import { getSkillDetail, toggleLike as toggleLikeApi, toggleFavorite as toggleFavoriteApi, incrementView, incrementDownload } from '@/api/skills'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const skillId = Number(route.params.id)
const skill = ref<any>()
const content = ref('# Sample Skill\n\nThis is a sample skill content.')

onMounted(async () => {
  const data = await getSkillDetail(skillId)
  skill.value = data.data
  await incrementView(skillId)
})

const copyContent = () => {
  navigator.clipboard.writeText(content.value)
  ElMessage.success('已复制到剪贴板')
}

const downloadSkill = async () => {
  await incrementDownload(skillId)
  const filename = skill.value.type === 'file' ? `${skill.value.name}.md` : `${skill.value.name}.zip`
  const blob = new Blob([content.value], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('下载成功')
}

const toggleLike = async () => {
  await toggleLikeApi(skillId)
  skill.value.isLiked = !skill.value.isLiked
  skill.value.likeCount += skill.value.isLiked ? 1 : -1
}

const toggleFavorite = async () => {
  await toggleFavoriteApi(skillId)
  skill.value.isFavorited = !skill.value.isFavorited
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.skill-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skill-info {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.description {
  color: #666;
  line-height: 1.8;
  margin: 15px 0;
}

.stats {
  display: flex;
  gap: 20px;
  color: #999;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content-section {
  margin-top: 20px;
}

.actions {
  display: flex;
  gap: 10px;
}
</style>
```

**Step 5: Create frontend/src/views/Favorites.vue**

```vue
<template>
  <div class="favorites">
    <h2>我的收藏</h2>
    <el-row :gutter="20" v-if="skills.length > 0">
      <el-col :span="6" v-for="skill in skills" :key="skill.id">
        <el-card class="skill-card" @click="goToDetail(skill.id)">
          <div class="skill-name">{{ skill.name }}</div>
          <div class="skill-category">{{ skill.category }}</div>
          <div class="skill-desc">{{ skill.description }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-else description="暂无收藏" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorites } from '@/api/skills'
import type { SkillDTO } from '@/types/skill'

const router = useRouter()
const skills = ref<SkillDTO[]>([])

onMounted(async () => {
  const data = await getFavorites()
  skills.value = data.data.records || []
})

const goToDetail = (id: number) => {
  router.push(`/skills/${id}`)
}
</script>

<style scoped>
.favorites {
  padding: 20px;
}

.skill-card {
  cursor: pointer;
  margin-bottom: 20px;
}

.skill-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}

.skill-category {
  color: #409eff;
  font-size: 12px;
  margin-bottom: 8px;
}

.skill-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}
</style>
```

**Step 6: Commit**

```bash
cd frontend && git add .
git commit -m "feat: add Skills views"
```

---

## Task 8: Docker Compose for Local Development

**Files:**
- Create: `docker-compose.yml`

**Step 1: Create docker-compose.yml**

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: ai-marketplace-db
    environment:
      POSTGRES_DB: ai_marketplace
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backend/src/main/resources/db/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    networks:
      - ai-marketplace-network

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: ai-marketplace-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ai_marketplace
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    depends_on:
      - postgres
    networks:
      - ai-marketplace-network

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: ai-marketplace-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - ai-marketplace-network

volumes:
  postgres_data:

networks:
  ai-marketplace-network:
    driver: bridge
```

**Step 2: Commit**

```bash
git add docker-compose.yml
git commit -m "feat: add Docker Compose for local development"
```

---

## Task 9: README and Documentation

**Files:**
- Create: `README.md`

**Step 1: Create README.md**

```markdown
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
```

**Step 2: Commit**

```bash
git add README.md
git commit -m "docs: add README documentation"
```

---

## Task 10: Final Verification

**Step 1: Verify frontend starts**

```bash
cd frontend
npm install
npm run dev
```

Expected: Server starts on http://localhost:3000

**Step 2: Verify backend starts**

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Expected: Server starts on http://localhost:8080

**Step 3: Test API endpoints**

```bash
curl http://localhost:8080/api/llm/models
curl http://localhost:8080/api/skills
```

Expected: Returns JSON response with data

**Step 4: Commit final changes**

```bash
git commit -m "feat: complete AI Marketplace MVP implementation"
```
