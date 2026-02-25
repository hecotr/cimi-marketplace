<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>Category Management</h3>
          <el-button type="primary" @click="showCreateDialog">New Category</el-button>
        </div>
      </template>

      <el-table :data="categories" v-loading="loading" stripe row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="Name" />
        <el-table-column prop="description" label="Description" />
        <el-table-column prop="icon" label="Icon" width="100" />
        <el-table-column prop="sortOrder" label="Order" width="80" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? 'Active' : 'Inactive' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assetCount" label="Assets" width="80" />
        <el-table-column label="Actions" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editCategory(row)">Edit</el-button>
            <el-button type="success" size="small" @click="moveUp(row)">Up</el-button>
            <el-button type="warning" size="small" @click="moveDown(row)">Down</el-button>
            <el-button type="danger" size="small" @click="deleteCategory(row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="editMode ? 'Edit Category' : 'New Category'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" placeholder="Enter category name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="Enter description"
          />
        </el-form-item>
        <el-form-item label="Icon">
          <el-input v-model="form.icon" placeholder="Enter icon name" />
        </el-form-item>
        <el-form-item label="Parent">
          <el-select v-model="form.parentId" clearable placeholder="None">
            <el-option
              v-for="cat in parentCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Sort Order">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="Status">
          <el-radio-group v-model="form.status">
            <el-radio label="active">Active</el-radio>
            <el-radio label="inactive">Inactive</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitForm">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminApi } from '@/api/admin'
import type { Category } from '@/api/admin'

const loading = ref(false)
const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const editMode = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  description: '',
  icon: '',
  parentId: undefined as number | undefined,
  sortOrder: 0,
  status: 'active'
})

const rules: FormRules = {
  name: [{ required: true, message: 'Please enter name', trigger: 'blur' }]
}

const parentCategories = computed(() => {
  return categories.value.filter(c => c.id !== editingId.value)
})

onMounted(() => {
  loadCategories()
})

async function loadCategories() {
  loading.value = true
  try {
    categories.value = await adminApi.getCategoryTree()
  } catch (error) {
    console.error('Failed to load categories:', error)
  } finally {
    loading.value = false
  }
}

function showCreateDialog() {
  editMode.value = false
  editingId.value = null
  Object.assign(form, {
    name: '',
    description: '',
    icon: '',
    parentId: undefined,
    sortOrder: 0,
    status: 'active'
  })
  dialogVisible.value = true
}

function editCategory(category: Category) {
  editMode.value = true
  editingId.value = category.id
  Object.assign(form, category)
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editMode.value && editingId.value) {
          await adminApi.updateCategory(editingId.value, form)
          ElMessage.success('Category updated')
        } else {
          await adminApi.createCategory(form)
          ElMessage.success('Category created')
        }
        dialogVisible.value = false
        await loadCategories()
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Operation failed')
      }
    }
  })
}

async function deleteCategory(category: Category) {
  if (category.assetCount && category.assetCount > 0) {
    ElMessage.warning('Cannot delete category with assets')
    return
  }

  try {
    await ElMessageBox.confirm('Are you sure to delete this category?', 'Warning', {
      type: 'warning'
    })
    await adminApi.deleteCategory(category.id)
    ElMessage.success('Category deleted')
    await loadCategories()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Delete failed')
    }
  }
}

async function moveUp(category: Category) {
  if (category.sortOrder > 0) {
    try {
      await adminApi.updateCategorySort(category.id, category.sortOrder - 1)
      await loadCategories()
    } catch (error) {
      ElMessage.error('Update failed')
    }
  }
}

async function moveDown(category: Category) {
  try {
    await adminApi.updateCategorySort(category.id, category.sortOrder + 1)
    await loadCategories()
  } catch (error) {
    ElMessage.error('Update failed')
  }
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
</style>
