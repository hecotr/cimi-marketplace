<template>
  <div class="model-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>LLM 模型列表</h2>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8" v-for="model in models" :key="model.id">
          <el-card class="model-card" shadow="hover" @click="$router.push(`/llm/${model.id}`)">
            <div class="model-info">
              <h3>{{ model.name }}</h3>
              <p class="provider">{{ model.provider }}</p>
              <p class="description">{{ model.description }}</p>
              <div class="meta">
                <el-tag size="small">{{ model.categoryName }}</el-tag>
                <el-tag size="small" type="info">{{ model.maxTokens }} tokens</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="models.length === 0" description="暂无模型" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'

interface LlmModel {
  id: number
  name: string
  provider: string
  description: string
  categoryName: string
  maxTokens: number
}

const models = ref<LlmModel[]>([])
const loading = ref(false)

const fetchModels = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/llm/models')
    if (res.code === 200) {
      models.value = res.data
    }
  } catch (error) {
    console.error('获取模型列表失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchModels()
})
</script>

<style scoped>
.model-list {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0;
}

.model-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}

.model-card:hover {
  transform: translateY(-4px);
}

.model-info h3 {
  margin: 0 0 8px;
}

.provider {
  color: #909399;
  font-size: 14px;
  margin: 0 0 8px;
}

.description {
  color: #606266;
  font-size: 13px;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  gap: 8px;
}
</style>
