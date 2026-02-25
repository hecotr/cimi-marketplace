<template>
  <div class="my-drafts">
    <el-page-header @back="goBack" title="Back">
      <template #content>
        <span class="text-large font-600 mr-3"> My Drafts</span>
      </template>
    </el-page-header>

    <el-card class="content-card">
      <el-button type="primary" @click="goPublish" class="mb-4">Create New</el-button>

      <el-table :data="drafts" v-loading="loading" stripe>
        <el-table-column prop="name" label="Name" />
        <el-table-column prop="type" label="Type">
          <template #default="{ row }">
            <el-tag>{{ row.type === 'llm_model' ? 'LLM Model' : 'Skill' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="Category" />
        <el-table-column prop="createdAt" label="Created At">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="Actions" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editAsset(row.id)">Edit</el-button>
            <el-button type="success" size="small" @click="publishAsset(row.id)">Publish</el-button>
            <el-button type="danger" size="small" @click="deleteAsset(row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAssetStore } from '@/stores/asset'
import { assetApi } from '@/api/asset'

const router = useRouter()
const assetStore = useAssetStore()

const loading = ref(false)
const drafts = ref<any[]>([])

onMounted(async () => {
  await loadDrafts()
})

async function loadDrafts() {
  loading.value = true
  try {
    drafts.value = await assetApi.getMyDrafts()
  } catch (error) {
    console.error('Failed to load drafts:', error)
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

function goPublish() {
  router.push('/publish')
}

function editAsset(id: number) {
  router.push(`/asset/${id}/edit`)
}

async function publishAsset(id: number) {
  try {
    await assetApi.publishAsset(id)
    ElMessage.success('Asset submitted for approval')
    await loadDrafts()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || 'Publish failed')
  }
}

async function deleteAsset(id: number) {
  try {
    await ElMessageBox.confirm('Are you sure to delete this draft?', 'Warning', {
      type: 'warning'
    })
    await assetApi.deleteAsset(id)
    ElMessage.success('Draft deleted')
    await loadDrafts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Delete failed')
    }
  }
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.my-drafts {
  padding: 20px;
}

.content-card {
  margin-top: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>
