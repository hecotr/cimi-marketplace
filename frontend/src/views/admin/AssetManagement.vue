<template>
  <div class="asset-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>Asset Management</h3>
        </div>
      </template>

      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="Type">
          <el-select v-model="filters.type" clearable placeholder="All">
            <el-option label="LLM Model" value="llm_model" />
            <el-option label="Skill" value="skill" />
          </el-select>
        </el-form-item>
        <el-form-item label="Status">
          <el-select v-model="filters.status" clearable placeholder="All">
            <el-option label="Draft" value="draft" />
            <el-option label="Pending" value="pending" />
            <el-option label="Published" value="published" />
            <el-option label="Rejected" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="Keyword">
          <el-input v-model="filters.keyword" placeholder="Search..." clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchAssets">Search</el-button>
          <el-button @click="resetFilters">Reset</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="assets" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="Name" />
        <el-table-column prop="type" label="Type" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.type === 'llm_model' ? 'LLM Model' : 'Skill' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="Category" />
        <el-table-column prop="status" label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="Creator" />
        <el-table-column prop="viewCount" label="Views" width="80" />
        <el-table-column prop="downloadCount" label="Downloads" width="100" />
        <el-table-column prop="likeCount" label="Likes" width="80" />
        <el-table-column prop="createdAt" label="Created At" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="Actions" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewAsset(row.id)">View</el-button>
            <el-button type="warning" size="small" @click="editAsset(row.id)">Edit</el-button>
            <el-button type="danger" size="small" @click="deleteAsset(row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="searchAssets"
        @current-change="searchAssets"
        class="mt-4"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assetApi } from '@/api/asset'

const router = useRouter()

const loading = ref(false)
const assets = ref<any[]>([])

const filters = reactive({
  type: '',
  status: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

onMounted(() => {
  searchAssets()
})

async function searchAssets() {
  loading.value = true
  try {
    const result = await assetApi.searchAssets(
      filters.keyword,
      filters.type || undefined,
      undefined,
      pagination.page,
      pagination.size
    )
    assets.value = result
    pagination.total = result.length
  } catch (error) {
    console.error('Failed to search assets:', error)
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.type = ''
  filters.status = ''
  filters.keyword = ''
  pagination.page = 1
  searchAssets()
}

function viewAsset(id: number) {
  router.push(`/asset/${id}`)
}

function editAsset(id: number) {
  router.push(`/admin/asset/${id}/edit`)
}

async function deleteAsset(id: number) {
  try {
    await ElMessageBox.confirm('Are you sure to delete this asset?', 'Warning', {
      type: 'warning'
    })
    await assetApi.deleteAsset(id)
    ElMessage.success('Asset deleted')
    searchAssets()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('Delete failed')
    }
  }
}

function getStatusType(status: string) {
  switch (status) {
    case 'published': return 'success'
    case 'pending': return 'warning'
    case 'rejected': return 'danger'
    case 'draft': return 'info'
    default: return 'info'
  }
}

function getStatusText(status: string) {
  switch (status) {
    case 'published': return 'Published'
    case 'pending': return 'Pending'
    case 'rejected': return 'Rejected'
    case 'draft': return 'Draft'
    default: return status
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

.filter-form {
  margin-bottom: 20px;
}

.mt-4 {
  margin-top: 16px;
}
</style>
