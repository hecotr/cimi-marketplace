<template>
  <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
    <el-form-item label="Asset Type" prop="type">
      <el-select v-model="form.type" placeholder="Select type" :disabled="!!editId">
        <el-option label="LLM Model" value="llm_model" />
        <el-option label="Skill" value="skill" />
      </el-select>
    </el-form-item>

    <el-form-item label="Name" prop="name">
      <el-input v-model="form.name" placeholder="Enter asset name" />
    </el-form-item>

    <el-form-item label="Category" prop="categoryId">
      <el-select v-model="form.categoryId" placeholder="Select category" clearable>
        <el-option
          v-for="cat in categories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="Description" prop="description">
      <el-input
        v-model="form.description"
        type="textarea"
        :rows="3"
        placeholder="Enter description"
      />
    </el-form-item>

    <el-form-item label="Content" prop="content">
      <MarkdownEditor v-model="form.content" />
    </el-form-item>

    <el-form-item label="Version" prop="version">
      <el-input v-model="form.version" placeholder="1.0.0" />
    </el-form-item>

    <el-form-item v-if="editId" label="Change Notes">
      <el-input
        v-model="form.changeNotes"
        type="textarea"
        :rows="2"
        placeholder="Describe what changed in this version"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSubmit">Save Draft</el-button>
      <el-button type="success" @click="handlePublish" v-if="!editId">Publish</el-button>
      <el-button @click="handleCancel">Cancel</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import MarkdownEditor from './MarkdownEditor.vue'
import { assetApi } from '@/api/asset'
import { adminApi } from '@/api/admin'
import type { Category } from '@/api/admin'

interface Props {
  editId?: number
  initialData?: any
}

const props = defineProps<Props>()
const emit = defineEmits(['submit', 'cancel'])

const formRef = ref<FormInstance>()
const categories = ref<Category[]>([])

const form = reactive({
  type: 'skill',
  name: '',
  categoryId: undefined as number | undefined,
  description: '',
  content: '',
  version: '1.0.0',
  changeNotes: ''
})

const rules: FormRules = {
  type: [{ required: true, message: 'Please select type', trigger: 'change' }],
  name: [{ required: true, message: 'Please enter name', trigger: 'blur' }],
  description: [{ required: true, message: 'Please enter description', trigger: 'blur' }],
  content: [{ required: true, message: 'Please enter content', trigger: 'blur' }],
  version: [{ required: true, message: 'Please enter version', trigger: 'blur' }]
}

onMounted(async () => {
  if (props.initialData) {
    Object.assign(form, props.initialData)
  }
  // Load categories
  try {
    categories.value = await adminApi.getCategories('active')
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
})

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (props.editId) {
          await assetApi.updateAsset(props.editId, form)
          ElMessage.success('Asset updated successfully')
        } else {
          await assetApi.createAsset(form)
          ElMessage.success('Draft saved successfully')
        }
        emit('submit')
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Operation failed')
      }
    }
  })
}

async function handlePublish() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const asset = await assetApi.createAsset(form)
        await assetApi.publishAsset(asset.id)
        ElMessage.success('Asset published and submitted for approval')
        emit('submit')
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Publish failed')
      }
    }
  })
}

function handleCancel() {
  emit('cancel')
}
</script>
