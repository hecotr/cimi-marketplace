<template>
  <div class="model-detail">
    <el-card v-if="model">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>{{ model.name }}</h3>
            <VersionSelector
              v-if="model.assetId"
              :asset-id="model.assetId"
              v-model="selectedVersion"
              @change="handleVersionChange"
            />
          </div>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div class="model-info">
        <el-tag>{{ model.provider }}</el-tag>
        <el-tag type="info">{{ model.status }}</el-tag>
      </div>

      <p class="description">{{ model.description }}</p>

      <div class="info-grid">
        <div class="info-item">
          <label>模型名称:</label>
          <span>{{ model.modelName }}</span>
        </div>
        <div class="info-item">
          <label>最大 Token:</label>
          <span>{{ model.maxTokens }}</span>
        </div>
      </div>

      <el-divider />

      <div class="test-section">
        <h4>在线测试</h4>
        <el-form :model="testForm" label-width="100px">
          <el-form-item label="Temperature">
            <el-slider v-model="testForm.temperature" :min="0" :max="1" :step="0.1" />
            <span class="slider-value">{{ testForm.temperature }}</span>
          </el-form-item>
          <el-form-item label="Max Tokens">
            <el-input-number v-model="testForm.maxTokens" :min="1" :max="model.maxTokens" />
          </el-form-item>
          <el-form-item label="Top P">
            <el-slider v-model="testForm.topP" :min="0" :max="1" :step="0.1" />
            <span class="slider-value">{{ testForm.topP }}</span>
          </el-form-item>
          <el-form-item label="提示词">
            <el-input
              v-model="testForm.prompt"
              type="textarea"
              :rows="4"
              placeholder="请输入提示词..."
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="sendTest" :loading="loading">
              发送测试
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-divider />

      <div class="version-history" v-if="model.assetId">
        <VersionHistory :asset-id="model.assetId" />
      </div>

      <el-divider />

      <div class="response-section" v-if="response">
        <h4>测试结果</h4>
        <div class="chat-messages">
          <div class="message">
            <div class="message-label">用户:</div>
            <div class="message-content user">{{ testForm.prompt }}</div>
          </div>
          <div class="message">
            <div class="message-label">助手:</div>
            <div class="message-content assistant">{{ response.response }}</div>
          </div>
        </div>
        <div class="metrics">
          <el-tag type="info">响应时间: {{ response.responseTime }}ms</el-tag>
          <el-tag type="success">Input Tokens: {{ response.tokenUsage.inputTokens }}</el-tag>
          <el-tag type="warning">Output Tokens: {{ response.tokenUsage.outputTokens }}</el-tag>
          <el-tag>Total Tokens: {{ response.tokenUsage.totalTokens }}</el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getModelDetail, testModel } from '@/api/llm'
import type { LlmModelDTO, LlmTestRequest } from '@/types/llm'
import { ElMessage } from 'element-plus'
import VersionSelector from '@/components/VersionSelector.vue'
import VersionHistory from '@/components/VersionHistory.vue'

const route = useRoute()
const router = useRouter()
const modelId = Number(route.params.id)
const model = ref<LlmModelDTO>()
const loading = ref(false)

const testForm = ref<LlmTestRequest>({
  modelId: modelId,
  prompt: '',
  temperature: 0.7,
  maxTokens: 2048,
  topP: 0.9
})

const response = ref<any>()
const selectedVersion = ref('')

onMounted(async () => {
  const data = await getModelDetail(modelId)
  model.value = data.data
  testForm.value.maxTokens = data.data.maxTokens
})

const sendTest = async () => {
  if (!testForm.value.prompt.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }

  loading.value = true
  try {
    const data = await testModel(testForm.value)
    response.value = data.data
  } catch (error) {
    ElMessage.error('测试失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const handleVersionChange = async (version: string) => {
  // Reload model data for the selected version
  if (model.value?.assetId) {
    // Implementation depends on how version-specific data is loaded
    console.log('Version changed to:', version)
  }
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

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.model-info {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.description {
  color: #666;
  line-height: 1.8;
  margin: 15px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.info-item label {
  color: #999;
  margin-right: 8px;
}

.info-item span {
  font-weight: 500;
}

.test-section {
  margin-top: 20px;
}

.slider-value {
  margin-left: 10px;
  color: #409eff;
  font-weight: bold;
}

.version-history {
  margin: 20px 0;
}

.response-section {
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

.message-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
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

.metrics {
  display: flex;
  gap: 10px;
}
</style>
