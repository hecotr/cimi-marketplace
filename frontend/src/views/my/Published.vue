<template>
  <div class="published">
    <el-card>
      <template #header>
        <h2>我的发布</h2>
      </template>

      <el-table :data="assets" v-loading="loading">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="assetType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.assetType === 'skill' ? 'success' : 'primary'">
              {{ row.assetType === 'skill' ? 'Skill' : 'LLM' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleView(row)">查看</el-button>
            <el-button text type="warning" @click="handleOffline(row)" v-if="row.status === 'approved'">
              下架
            </el-button>
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
import request from '@/api/request'

const router = useRouter()
const assets = ref<any[]>([])
const loading = ref(false)

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    approved: 'success',
    offline: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    approved: '已发布',
    offline: '已下架'
  }
  return texts[status] || status
}

const fetchAssets = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/assets/my?status=approved,offline')
    if (res.code === 200) {
      assets.value = res.data
    }
  } catch (error) {
    console.error('获取发布列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleView = (row: any) => {
  if (row.assetType === 'skill') {
    router.push(`/skills/${row.id}`)
  }
}

const handleOffline = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要下架吗？', '提示')
    const res = await request.post<any, any>(`/assets/${row.id}/offline`)
    if (res.code === 200) {
      ElMessage.success('已下架')
      fetchAssets()
    }
  } catch (error) {
    // cancelled
  }
}

onMounted(() => {
  fetchAssets()
})
</script>

<style scoped>
.published {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
