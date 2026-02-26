<template>
  <div class="api-key-review">
    <el-card>
      <template #header>
        <h2>API Key 审批</h2>
      </template>

      <el-table :data="apiKeys" v-loading="loading">
        <el-table-column prop="keyValue" label="Key">
          <template #default="{ row }">
            <span class="key-value">{{ maskKey(row.keyValue) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="apiProtocol" label="协议" width="100" />
        <el-table-column prop="expiryType" label="有效期" width="100">
          <template #default="{ row }">
            {{ getExpiryText(row.expiryType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button
              text
              type="success"
              @click="handleApprove(row)"
              v-if="row.status === 'pending'"
            >
              通过
            </el-button>
            <el-button
              text
              type="danger"
              @click="handleReject(row)"
              v-if="row.status === 'pending'"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="apiKeys.length === 0 && !loading" description="暂无待审批的 API Key" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

interface ApiKey {
  id: number
  keyValue: string
  apiProtocol: string
  expiryType: string
  status: string
  applyTime: string
}

const apiKeys = ref<ApiKey[]>([])
const loading = ref(false)

const fetchApiKeys = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/admin/api-keys/pending')
    if (res.code === 200) {
      apiKeys.value = res.data || []
    }
  } catch (error) {
    console.error('获取待审批列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row: ApiKey) => {
  try {
    await ElMessageBox.confirm('确定通过此 API Key 申请吗？', '提示')
    const res = await request.post<any, any>(`/admin/api-keys/${row.id}/approve`)
    if (res.code === 200) {
      ElMessage.success('已通过')
      fetchApiKeys()
    }
  } catch (error) {
    // cancelled
  }
}

const handleReject = async (row: ApiKey) => {
  try {
    const result = await ElMessageBox.prompt('请输入拒绝原因', '拒绝', {
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    }) as { value: string }
    const res = await request.post<any, any>(`/admin/api-keys/${row.id}/reject`, { reason: result.value })
    if (res.code === 200) {
      ElMessage.success('已拒绝')
      fetchApiKeys()
    }
  } catch (error) {
    // cancelled
  }
}

const maskKey = (key: string) => {
  if (!key || key.length < 12) return key
  return key.substring(0, 8) + '****' + key.substring(key.length - 4)
}

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    expired: 'info',
    revoked: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待审批',
    approved: '已通过',
    rejected: '已拒绝',
    expired: '已过期',
    revoked: '已撤销'
  }
  return texts[status] || status
}

const getExpiryText = (type: string) => {
  const texts: Record<string, string> = {
    '3m': '3 个月',
    '6m': '6 个月',
    '1y': '1 年',
    'permanent': '永久'
  }
  return texts[type] || type
}

onMounted(() => {
  fetchApiKeys()
})
</script>

<style scoped>
.api-key-review {
  max-width: 1200px;
  margin: 0 auto;
}

.key-value {
  font-family: monospace;
}
</style>
