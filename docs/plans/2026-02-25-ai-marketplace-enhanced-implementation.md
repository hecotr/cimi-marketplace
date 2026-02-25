# [AI Marketplace Enhanced] Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an enterprise AI capability sharing platform with asset publishing, approval workflow, admin backend, and extended features.

**Architecture:** Frontend-backend separation with Vue3 (Element Plus + frontend-design) for UI, Java SpringBoot for RESTful API, PostgreSQL for data persistence, MinIO/OSS for object storage.

**Tech Stack:** Vue3, Vite, TypeScript, Element Plus, Pinia, Java 17, SpringBoot 2.6.5, MyBatis Plus, PostgreSQL, MinIO

---

## Task 1: Database Schema Initialization

**Files:**
- Create: `backend/src/main/resources/db/enhanced-schema.sql`

**Step 1: Create enhanced database schema** (Create file)

---

## Task 2: Backend Project Structure Setup

**Files:**
- Modify: `backend/pom.xml` - Add new dependencies
- Create: `backend/src/main/java/com/aimarketplace/config/MinioConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/config/ObjectStorageConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/config/SecurityConfig.java`

**Step 1: Add new dependencies to pom.xml** (Modify file)

**Step 2: Create MinIO configuration class** (Create file)

**Step 3: Create object storage config interface** (Create file)

**Step 4: Create security configuration class** (Create file)

**Step 5: Commit** (Run git commands)

---

## Task 3: Enhanced Entity Classes

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/entity/UserRole.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/Asset.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/AssetVersion.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/Category.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/LlmModelConfig.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/ApprovalRecord.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/Favorite.java`
- Create: `backend/src/main/java/com/aimarketplace/entity/LikeRecord.java`

**Step 1: Create entity classes** (9 files)

**Step 2: Commit** (Run git commands)

---

## Task 4: Asset Management Services

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/AssetMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/AssetVersionMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/ApprovalRecordMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/AssetDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/AssetPublishRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/service/AssetService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/AssetServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/AssetController.java`

**Step 1: Create mappers** (3 files)

**Step 2: Create DTOs** (2 files)

**Step 3: Create service interface** (Create file)

**Step 4: Create service implementation** (Create file)

**Step 5: Create controller** (Create file)

**Step 6: Commit** (Run git commands)

---

## Task 5: LLM Configuration Management

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/LlmModelConfigMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmModelConfigDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/LlmModelConfigRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/service/LlmModelConfigService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/LlmModelConfigServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/AdminLlmConfigController.java`

**Step 1: Create mapper** (Create file)

**Step 2: Create DTOs** (2 files)

**Step 3: Create service interface** (Create file)

**Step 4: Create service implementation** (Create file)

**Step 5: Create controller** (Create file)

**Step 6: Commit** (Run git commands)

---

## Task 6: Approval Workflow Integration

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/service/ApprovalService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/ApprovalServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/ApprovalCallbackRequest.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/AdminApprovalController.java`

**Step 1: Create service interface** (Create file)

**Step 2: Create service implementation** (Create file)

**Step 3: Create DTO** (Create file)

**Step 4: Create controller** (Create file)

**Step 5: Commit** (Run git commands)

---

## Task 7: Enhanced Interaction Services

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/FavoriteMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/mapper/LikeRecordMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/service/InteractionService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/InteractionServiceImpl.java`

**Step 1: Create mappers** (2 files)

**Step 2: Create service interface** (Create file)

**Step 3: Create service implementation** (Create file)

**Step 4: Commit** (Run git commands)

---

## Task 8: Statistics Services

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/AssetStatisticsMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/StatisticsDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/service/StatisticsService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/StatisticsServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/AdminStatisticsController.java`

**Step 1: Create mappers** (1 file)

**Step 2: Create DTO** (Create file)

**Step 3: Create service interface** (Create file)

**Step 4: Create service implementation** (Create file)

**Step 5: Create controller** (Create file)

**Step 6: Commit** (Run git commands)

---

## Task 9: Category Management

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/mapper/CategoryMapper.java`
- Create: `backend/src/main/java/com/aimarketplace/dto/CategoryDTO.java`
- Create: `backend/src/main/java/com/aimarketplace/service/CategoryService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/CategoryServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/AdminCategoryController.java`

**Step 1: Create mapper** (Create file)

**Step 2: Create DTOs** (2 files)

**Step 3: Create service interface** (Create file)

**Step 4: Create service implementation** (Create file)

**Step 5: Create controller** (Create file)

**Step 6: Commit** (Run git commands)

---

## Task 10: Frontend Project Setup (Enhanced)

**Files:**
- Modify: `frontend/package.json` - Add new dependencies
- Create: `frontend/src/stores/user.ts` - Pinia store
- Create: `frontend/src/stores/asset.ts` - Pinia store
- Create: `frontend/src/composables/useAuth.ts` - Auth composable
- Create: `frontend/src/api/asset.ts` - Asset API
- Create: `frontend/src/api/admin.ts` - Admin API

**Step 1: Update package.json** (Modify file)

**Step 2: Create Pinia stores** (2 files)

**Step 3: Create auth composable** (Create file)

**Step 4: Create API modules** (2 files)

**Step 5: Commit** (Run git commands)

---

## Task 11: Frontend - Asset Publishing Pages

**Files:**
- Create: `frontend/src/views/asset/PublishAsset.vue`
- Create: `frontend/src/views/asset/EditAsset.vue`
- Create: `frontend/src/views/asset/MyDrafts.vue`
- Create: `frontend/src/views/asset/MyPublished.vue`
- Create: `frontend/src/components/AssetForm.vue`
- Create: `frontend/src/components/MarkdownEditor.vue`
- Create: `frontend/src/components/FileUploader.vue`

**Step 1: Create asset form component** (Create file)

**Step 2: Create markdown editor component** (Create file)

**Step 3: Create file uploader component** (Create file)

**Step 4: Create publish/edit pages** (3 files)

**Step 5: Update router** (Modify file)

**Step 6: Commit** (Run git commands)

---

## Task 12: Frontend - Admin Backend Pages

**Files:**
- Create: `frontend/src/views/admin/AssetManagement.vue`
- Create: `frontend/src/views/admin/ReviewQueue.vue`
- Create: `frontend/src/views/admin/CategoryManagement.vue`
- Create: `frontend/src/views/admin/Statistics.vue`
- Create: `frontend/src/views/admin/LLMConfig.vue`
- Create: `frontend/src/layouts/AdminLayout.vue`

**Step 1: Create admin layout** (Create file)

**Step 2: Create asset management page** (Create file)

**Step 3: Create review queue page** (Create file)

**Step 4: Create category management page** (Create file)

**Step 5: Create statistics page** (Create file)

**Step 6: Create LLM config page** (Create file)

**Step 7: Update router** (Modify file)

**Step 8: Commit** (Run git commands)

---

## Task 13: Frontend - Enhanced Asset Detail

**Files:**
- Modify: `frontend/src/views/llm/ModelDetail.vue` - Add version selector
- Modify: `frontend/src/views/skills/SkillDetail.vue` - Add version selector
- Create: `frontend/src/components/VersionSelector.vue`
- Create: `frontend/src/components/VersionHistory.vue`

**Step 1: Create version selector component** (Create file)

**Step 2: Create version history component** (Create file)

**Step 3: Update asset detail pages** (Modify 2 files)

**Step 4: Commit** (Run git commands)

---

## Task 14: Frontend - Enhanced Favorites

**Files:**
- Modify: `frontend/src/views/Favorites.vue` - Add type filter
- Update: `frontend/src/stores/asset.ts` - Add favorites logic
- Update: `frontend/src/api/asset.ts` - Add favorite API calls

**Step 1: Update favorites store** (Modify file)

**Step 2: Update favorites page** (Modify file)

**Step 3: Commit** (Run git commands)

---

## Task 15: Object Storage Integration

**Files:**
- Create: `backend/src/main/java/com/aimarketplace/service/ObjectStorageService.java`
- Create: `backend/src/main/java/com/aimarketplace/service/impl/ObjectStorageServiceImpl.java`
- Create: `backend/src/main/java/com/aimarketplace/controller/FileController.java`

**Step 1: Create service interface** (Create file)

**Step 2: Create service implementation** (Create file)

**Step 3: Create controller** (Create file)

**Step 4: Update application.yml** (Modify file)

**Step 5: Commit** (Run git commands)

---

## Task 16: UI/UX Enhancement

**Files:**
- Use frontend-design skill to create design system
- Create: `frontend/src/styles/variables.css`
- Create: `frontend/src/styles/common.css`

**Step 1: Apply frontend-design skill** (Use skill)

**Step 2: Create global styles** (2 files)

**Step 3: Update existing components** (Update files as needed)

**Step 4: Commit** (Run git commands)

---

## Task 17: Testing & Verification

**Files:**
- Create: `backend/src/test/java/com/aimarketplace/service/AssetServiceTest.java`
- Create: `backend/src/test/java/com/aimarketplace/controller/AssetControllerTest.java`
- Create: `frontend/src/tests/e2e/asset-publish.spec.ts`
- Create: `frontend/src/tests/e2e/admin-flow.spec.ts`

**Step 1: Write unit tests** (backend) (2 files)

**Step 2: Write E2E tests** (frontend) (2 files)

**Step 3: Run tests** (Run mvn test and npm run test)

**Step 4: Commit** (Run git commands)

---

## Task 18: Documentation & Deployment Config

**Files:**
- Update: `README.md`
- Create: `DEPLOYMENT.md`
- Update: `docker-compose.yml` - Add MinIO service

**Step 1: Update README** (Modify file)

**Step 2: Create deployment guide** (Create file)

**Step 3: Update docker-compose** (Modify file)

**Step 4: Final commit** (Run git commands)

---

## Task 19: Final Verification

**Step 1: Full system test** (Manual testing)

**Step 2: Performance test** (Manual testing)

**Step 3: Database cleanup** (Run SQL commands)

**Step 4: Final commit** (Run git command)
