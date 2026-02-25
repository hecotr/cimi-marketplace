<template>
  <div class="my-published">
    <el-page-header @back="goBack" title="Back">
      <template #content>
        <span class="text-large font-600 mr-3"> My Published Assets</span>
      </template>
    </el-page-header>

    <el-card class="content-card">
      <el-table :data="published" v-loading="loading" stripe>
        <el-table-column prop="name" label="Name" />
        <el-table-column prop="type" label="Type">
          <template #default="{ row }">
            <el-tag>{{ row.type === 'llm_model' ? 'LLM Model' : 'Skill' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="Category" />
        <el-table-column prop="status" label="Status">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="Views" />
        <el-table-column prop="downloadCount" label="Downloads" />
        <el-table-column prop="likeCount" label="Likes" />
        <el-table-column prop="createdAt" label="Created At">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="Actions" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewAsset(row.id)">View</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { assetApi } from '@/api/asset'

const router = useRouter()

const loading = ref(false)
const published = ref<any[]>([])

onMounted(async () => {
  await loadPublished()
})

async function loadPublished() {
  loading.value = true
  try {
    published.value = await assetApi.getMyPublished()
  } catch (error) {
    console.error('Failed to load published:', error)
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

function viewAsset(id: number) {
  const asset = published.value.find(a => a.id === id)
  if (asset?.type === 'llm_model') {
    router.push(`/llm/${id}`)
  } else {
    router.push(`/skills/${id}`)
  }
}

function getStatusType(status: string) {
  switch (status) {
    case 'published': return 'success'
    case 'pending': return 'warning'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}

function getStatusText(status: string) {
  switch (status) {
    case 'published': return 'Published'
    case 'pending': return 'Pending Approval'
    case 'rejected': return 'Rejected'
    default: return status
  }
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.my-published {
  padding: 20px;
}

.content-card {
  margin-top: 20px;
}
</style>
