<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>分类管理</h2>
          <el-button type="primary" @click="showDialog()">添加分类</el-button>
        </div>
      </template>

      <el-table :data="categories" v-loading="loading">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="assetType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.assetType === 'llm' ? 'primary' : 'success'">
              {{ row.assetType === 'llm' ? 'LLM' : 'Skill' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="showDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingCategory ? '编辑分类' : '添加分类'" width="400px">
      <el-form :model="form" label-position="top">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.assetType">
            <el-option label="LLM" value="llm" />
            <el-option label="Skill" value="skill" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const categories = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingCategory = ref<any>(null)
const saving = ref(false)

const form = reactive({
  name: '',
  assetType: 'llm',
  sortOrder: 0
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/categories')
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类列表失败', error)
  } finally {
    loading.value = false
  }
}

const showDialog = (category?: any) => {
  editingCategory.value = category || null
  if (category) {
    Object.assign(form, category)
  } else {
    Object.assign(form, { name: '', assetType: 'llm', sortOrder: 0 })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    if (editingCategory.value) {
      await request.put(`/categories/admin/${editingCategory.value.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/categories/admin', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchCategories()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '警告', { type: 'warning' })
    await request.delete(`/categories/admin/${row.id}`)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    // cancelled
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-management {
  max-width: 1000px;
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
</style>
