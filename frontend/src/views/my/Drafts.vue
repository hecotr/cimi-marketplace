<template>
  <div class="drafts">
    <el-card>
      <template #header>
        <h2>我的草稿</h2>
      </template>

      <el-table :data="drafts" v-loading="loading">
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
        <el-table-column prop="updatedAt" label="更新时间" width="160" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="success" @click="handleSubmit(row)" v-if="row.status === 'draft' || row.status === 'rejected'">
              提交审核
            </el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
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
const drafts = ref<any[]>([])
const loading = ref(false)

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    draft: 'info',
    pending_review: 'warning',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    draft: '草稿',
    pending_review: '待审核',
    rejected: '已拒绝'
  }
  return texts[status] || status
}

const fetchDrafts = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/assets/my?status=draft,pending_review,rejected')
    if (res.code === 200) {
      drafts.value = res.data
    }
  } catch (error) {
    console.error('获取草稿失败', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (row: any) => {
  if (row.assetType === 'skill') {
    router.push(`/skills/publish?id=${row.id}`)
  }
}

const handleSubmit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要提交审核吗？', '提示')
    const res = await request.post<any, any>(`/assets/${row.id}/submit`)
    if (res.code === 200) {
      ElMessage.success('已提交审核')
      fetchDrafts()
    }
  } catch (error) {
    // cancelled
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '警告', { type: 'warning' })
    const res = await request.delete<any, any>(`/assets/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchDrafts()
    }
  } catch (error) {
    // cancelled
  }
}

onMounted(() => {
  fetchDrafts()
})
</script>

<style scoped>
.drafts {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
