<template>
  <div class="my-keys">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>我的 API Keys</h2>
          <el-button type="primary" @click="showApplyDialog">申请新 Key</el-button>
        </div>
      </template>

      <el-table :data="apiKeys" v-loading="loading">
        <el-table-column label="Key" width="300">
          <template #default="{ row }">
            <div class="key-cell">
              <span v-if="row.showFull">{{ row.keyValue }}</span>
              <span v-else>{{ maskKey(row.keyValue) }}</span>
              <el-button text @click="row.showFull = !row.showFull">
                <el-icon>
                  <View v-if="!row.showFull" />
                  <Hide v-else />
                </el-icon>
              </el-button>
              <el-button text @click="copyKey(row.keyValue)" v-if="row.status === 'approved'">
                <el-icon><CopyDocument /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="apiProtocol" label="协议" width="100" />
        <el-table-column prop="expiryType" label="有效期" width="100">
          <template #default="{ row }">
            {{ getExpiryText(row.expiryType) }}
          </template>
        </el-table-column>
        <el-table-column label="到期时间" width="160">
          <template #default="{ row }">
            {{ row.expiryDate || '永不过期' }}
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
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button
              text
              type="danger"
              @click="handleRevoke(row)"
              v-if="row.status === 'approved'"
            >
              撤销
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="apiKeys.length === 0 && !loading" description="暂无 API Key" />
    </el-card>

    <!-- 申请对话框 -->
    <el-dialog v-model="applyDialogVisible" title="申请 API Key" width="500px">
      <el-form ref="formRef" :model="applyForm" :rules="applyRules" label-position="top">
        <el-form-item label="选择模型" prop="modelConfigId">
          <el-select v-model="applyForm.modelConfigId" placeholder="请选择模型" style="width: 100%;">
            <el-option
              v-for="model in models"
              :key="model.id"
              :label="model.name"
              :value="model.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="API 协议" prop="apiProtocol">
          <el-select v-model="applyForm.apiProtocol" placeholder="请选择协议" style="width: 100%;">
            <el-option label="OpenAI 兼容" value="openai" />
            <el-option label="Anthropic" value="anthropic" />
          </el-select>
        </el-form-item>

        <el-form-item label="有效期" prop="expiryType">
          <el-select v-model="applyForm.expiryType" placeholder="请选择有效期" style="width: 100%;">
            <el-option label="3 个月" value="3m" />
            <el-option label="6 个月" value="6m" />
            <el-option label="1 年" value="1y" />
            <el-option label="永久" value="permanent" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="handleApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { View, Hide, CopyDocument } from '@element-plus/icons-vue'
import request from '@/api/request'

interface ApiKey {
  id: number
  keyValue: string
  apiProtocol: string
  expiryType: string
  expiryDate: string
  status: string
  applyTime: string
  showFull: boolean
}

interface Model {
  id: number
  name: string
}

const apiKeys = ref<ApiKey[]>([])
const models = ref<Model[]>([])
const loading = ref(false)
const applyDialogVisible = ref(false)
const applying = ref(false)
const formRef = ref<FormInstance>()

const applyForm = reactive({
  modelConfigId: null as number | null,
  apiProtocol: 'openai',
  expiryType: '3m'
})

const applyRules: FormRules = {
  modelConfigId: [{ required: true, message: '请选择模型', trigger: 'change' }],
  apiProtocol: [{ required: true, message: '请选择协议', trigger: 'change' }],
  expiryType: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const fetchApiKeys = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/api-keys/my')
    if (res.code === 200) {
      apiKeys.value = (res.data || []).map((key: any) => ({
        ...key,
        showFull: false
      }))
    }
  } catch (error) {
    console.error('获取 API Keys 失败', error)
  } finally {
    loading.value = false
  }
}

const fetchModels = async () => {
  try {
    const res = await request.get<any, any>('/llm/models')
    if (res.code === 200) {
      models.value = res.data || []
    }
  } catch (error) {
    console.error('获取模型列表失败', error)
  }
}

const showApplyDialog = () => {
  applyForm.modelConfigId = null
  applyForm.apiProtocol = 'openai'
  applyForm.expiryType = '3m'
  applyDialogVisible.value = true
}

const handleApply = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    applying.value = true
    try {
      const res = await request.post<any, any>('/api-keys', applyForm)
      if (res.code === 200) {
        ElMessage.success('申请已提交，请等待审批')
        applyDialogVisible.value = false
        fetchApiKeys()
      }
    } catch (error) {
      ElMessage.error('申请失败')
    } finally {
      applying.value = false
    }
  })
}

const handleRevoke = async (row: ApiKey) => {
  try {
    await ElMessageBox.confirm('确定要撤销此 API Key 吗？撤销后无法恢复。', '警告', { type: 'warning' })
    const res = await request.post<any, any>(`/api-keys/${row.id}/revoke`)
    if (res.code === 200) {
      ElMessage.success('已撤销')
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

const copyKey = async (key: string) => {
  try {
    await navigator.clipboard.writeText(key)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
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
  fetchModels()
})
</script>

<style scoped>
.my-keys {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}

.key-cell {
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: monospace;
}
</style>
