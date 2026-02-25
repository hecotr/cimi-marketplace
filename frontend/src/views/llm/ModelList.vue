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
  transition: transform 0.2s;
  margin-bottom: 20px;
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
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
