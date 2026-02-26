<template>
  <div class="review-queue">
    <el-card>
      <template #header>
        <h2>审核队列</h2>
      </template>

      <el-table :data="items" v-loading="loading">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="assetType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.assetType === 'skill' ? 'success' : 'primary'">
              {{ row.assetType === 'skill' ? 'Skill' : 'LLM' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="提交者" width="120" />
        <el-table-column prop="updatedAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="showDetail(row)">查看</el-button>
            <el-button text type="success" @click="handleApprove(row)">通过</el-button>
            <el-button text type="danger" @click="handleReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="审核详情" width="600px">
      <div v-if="currentItem">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="名称">{{ currentItem.name }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ currentItem.description }}</el-descriptions-item>
          <el-descriptions-item label="提交者">{{ currentItem.createdBy }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>内容预览</h4>
        <el-card shadow="never" class="content-preview">
          <pre>{{ currentItem.versions?.[0]?.content || '暂无内容' }}</pre>
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const items = ref<any[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentItem = ref<any>(null)

const fetchItems = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/assets?status=pending_review')
    if (res.code === 200) {
      items.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取审核列表失败', error)
  } finally {
    loading.value = false
  }
}

const showDetail = async (row: any) => {
  try {
    const res = await request.get<any, any>(`/assets/${row.id}`)
    if (res.code === 200) {
      currentItem.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取详情失败', error)
  }
}

const handleApprove = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定通过审核吗？', '提示')
    await request.post(`/admin/assets/${row.id}/approve`, { comment: '' })
    ElMessage.success('已通过')
    fetchItems()
  } catch (error) {
    // cancelled
  }
}

const handleReject = async (row: any) => {
  try {
    const result = await ElMessageBox.prompt('请输入拒绝原因', '拒绝', {
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    }) as { value: string }
    await request.post<any, any>(`/admin/assets/${row.id}/reject`, { comment: result.value })
    ElMessage.success('已拒绝')
    fetchItems()
  } catch (error) {
    // cancelled
  }
}

onMounted(() => {
  fetchItems()
})
</script>

<style scoped>
.review-queue {
  max-width: 1200px;
  margin: 0 auto;
}

.content-preview {
  max-height: 300px;
  overflow: auto;
}

.content-preview pre {
  white-space: pre-wrap;
  margin: 0;
}
</style>
