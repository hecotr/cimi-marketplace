<template>
  <div class="review-queue">
    <el-row :gutter="20">
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>Pending Reviews</h3>
              <el-button @click="loadPending">Refresh</el-button>
            </div>
          </template>

          <el-table :data="pendingAssets" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="type" label="Type" width="120">
              <template #default="{ row }">
                <el-tag>{{ row.type === 'llm_model' ? 'LLM Model' : 'Skill' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="categoryName" label="Category" />
            <el-table-column prop="creatorName" label="Creator" />
            <el-table-column prop="createdAt" label="Submitted At" width="180">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="Actions" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="reviewAsset(row.id)">Review</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <template #header>
            <h3>Review Details</h3>
          </template>

          <div v-if="selectedAsset">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="Name">{{ selectedAsset.name }}</el-descriptions-item>
              <el-descriptions-item label="Type">{{ selectedAsset.type === 'llm_model' ? 'LLM Model' : 'Skill' }}</el-descriptions-item>
              <el-descriptions-item label="Category">{{ selectedAsset.categoryName }}</el-descriptions-item>
              <el-descriptions-item label="Creator">{{ selectedAsset.creatorName }}</el-descriptions-item>
              <el-descriptions-item label="Description">{{ selectedAsset.description }}</el-descriptions-item>
            </el-descriptions>

            <div class="mt-4">
              <h4>Content Preview</h4>
              <div class="content-preview">{{ selectedAsset.content?.substring(0, 200) }}...</div>
            </div>

            <div class="mt-4">
              <el-form :model="reviewForm">
                <el-form-item label="Comment">
                  <el-input
                    v-model="reviewForm.comment"
                    type="textarea"
                    :rows="4"
                    placeholder="Enter your review comment"
                  />
                </el-form-item>
              </el-form>

              <el-button type="success" @click="approveAsset">Approve</el-button>
              <el-button type="danger" @click="rejectAsset">Reject</el-button>
              <el-button type="warning" @click="requestChanges">Request Changes</el-button>
            </div>
          </div>

          <el-empty v-else description="Select an asset to review" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assetApi } from '@/api/asset'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const pendingAssets = ref<any[]>([])
const selectedAsset = ref<any>(null)

const reviewForm = reactive({
  comment: ''
})

onMounted(() => {
  loadPending()
})

async function loadPending() {
  loading.value = true
  try {
    const result = await assetApi.searchAssets('', '', undefined, 1, 100)
    pendingAssets.value = result.filter(a => a.status === 'pending')
  } catch (error) {
    console.error('Failed to load pending assets:', error)
  } finally {
    loading.value = false
  }
}

async function reviewAsset(id: number) {
  try {
    selectedAsset.value = await assetApi.getAsset(id)
    reviewForm.comment = ''
  } catch (error) {
    console.error('Failed to load asset:', error)
  }
}

async function approveAsset() {
  if (!selectedAsset.value) return

  try {
    await adminApi.approveAsset(selectedAsset.value.id, reviewForm.comment)
    ElMessage.success('Asset approved')
    selectedAsset.value = null
    await loadPending()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || 'Approval failed')
  }
}

async function rejectAsset() {
  if (!selectedAsset.value) return

  if (!reviewForm.comment) {
    ElMessage.warning('Please provide a rejection reason')
    return
  }

  try {
    await adminApi.rejectAsset(selectedAsset.value.id, reviewForm.comment)
    ElMessage.success('Asset rejected')
    selectedAsset.value = null
    await loadPending()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || 'Rejection failed')
  }
}

async function requestChanges() {
  if (!selectedAsset.value) return

  if (!reviewForm.comment) {
    ElMessage.warning('Please provide change request details')
    return
  }

  try {
    await adminApi.requestChanges(selectedAsset.value.id, reviewForm.comment)
    ElMessage.success('Changes requested')
    selectedAsset.value = null
    await loadPending()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || 'Request failed')
  }
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.content-preview {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}

.mt-4 {
  margin-top: 16px;
}
</style>
