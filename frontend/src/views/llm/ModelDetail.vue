<template>
  <div class="model-detail">
    <el-card v-if="model">
      <template #header>
        <div class="card-header">
          <el-button text @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h2>{{ model.name }}</h2>
          <div class="actions">
            <el-button
              :type="isFavorited ? 'warning' : 'default'"
              :icon="Star"
              @click="handleFavorite"
              :loading="favoriteLoading"
            >
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button
              :type="isLiked ? 'danger' : 'default'"
              :icon="isLiked ? 'heart-filled' : 'heart'"
              @click="handleLike"
              :loading="likeLoading"
            >
              {{ isLiked ? '已点赞' : '点赞' }}
            </el-button>
            <el-button type="primary" @click="$router.push('/my-keys')">
              申请 API Key
            </el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="提供商">{{ model.provider }}</el-descriptions-item>
        <el-descriptions-item label="模型名称">{{ model.modelName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ model.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="最大 Tokens">{{ model.maxTokens }}</el-descriptions-item>
        <el-descriptions-item label="API 协议">{{ model.apiProtocol }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="model.status === 'active' ? 'success' : 'info'">
            {{ model.status === 'active' ? '活跃' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ model.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <h3>测试模型</h3>
      <el-form :model="testForm" label-position="top">
        <el-form-item label="输入提示词">
          <el-input
            v-model="testForm.prompt"
            type="textarea"
            :rows="4"
            placeholder="请输入要测试的提示词"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="testing" @click="handleTest">
            发送测试
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="testResult" class="test-result">
        <h4>响应结果</h4>
        <el-card shadow="never">
          <pre>{{ testResult.response }}</pre>
          <div class="meta">
            <span>响应时间: {{ testResult.responseTime }}ms</span>
            <span>Tokens: {{ testResult.totalTokens }}</span>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star } from '@element-plus/icons-vue'
import request from '@/api/request'

interface LlmModel {
  id: number
  name: string
  provider: string
  modelName: string
  description: string
  categoryName: string
  apiProtocol: string
  maxTokens: number
  status: string
}

const route = useRoute()
const model = ref<LlmModel | null>(null)
const testForm = ref({
  prompt: ''
})
const testing = ref(false)
const testResult = ref<any>(null)

// 收藏/点赞状态
const isFavorited = ref(false)
const isLiked = ref(false)
const favoriteLoading = ref(false)
const likeLoading = ref(false)

const fetchModel = async () => {
  const id = route.params.id
  try {
    const res = await request.get<any, any>(`/llm/models/${id}`)
    if (res.code === 200) {
      model.value = res.data
      // 检查收藏/点赞状态
      checkInteractionStatus()
    }
  } catch (error) {
    console.error('获取模型详情失败', error)
  }
}

const checkInteractionStatus = async () => {
  if (!model.value) return

  try {
    // 检查收藏状态
    const favRes = await request.get<any, any>(
      `/interactions/favorite/check?assetId=${model.value.id}&assetType=llm`
    )
    if (favRes.code === 200) {
      isFavorited.value = favRes.data?.isFavorited || false
    }

    // 检查点赞状态
    const likeRes = await request.get<any, any>(
      `/interactions/like/check?assetId=${model.value.id}&assetType=llm`
    )
    if (likeRes.code === 200) {
      isLiked.value = likeRes.data?.isLiked || false
    }
  } catch (error) {
    console.error('检查状态失败', error)
  }
}

const handleFavorite = async () => {
  if (!model.value) return

  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      await request.delete(
        `/interactions/favorite?assetId=${model.value.id}&assetType=llm`
      )
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await request.post('/interactions/favorite', {
        assetId: model.value.id,
        assetType: 'llm'
      })
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const handleLike = async () => {
  if (!model.value) return

  likeLoading.value = true
  try {
    if (isLiked.value) {
      await request.delete(
        `/interactions/like?assetId=${model.value.id}&assetType=llm`
      )
      isLiked.value = false
      ElMessage.success('已取消点赞')
    } else {
      await request.post('/interactions/like', {
        assetId: model.value.id,
        assetType: 'llm'
      })
      isLiked.value = true
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    likeLoading.value = false
  }
}

const handleTest = async () => {
  if (!testForm.value.prompt.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }

  testing.value = true
  try {
    const res = await request.post<any, any>('/llm/test', {
      modelConfigId: model.value?.id,
      prompt: testForm.value.prompt
    })
    if (res.code === 200) {
      testResult.value = res.data
    }
  } catch (error) {
    ElMessage.error('测试失败')
  } finally {
    testing.value = false
  }
}

onMounted(() => {
  fetchModel()
})
</script>

<style scoped>
.model-detail {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header h2 {
  margin: 0;
  flex: 1;
}

.actions {
  display: flex;
  gap: 8px;
}

.test-result {
  margin-top: 20px;
}

.test-result pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
}

.test-result .meta {
  margin-top: 12px;
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 12px;
}
</style>
