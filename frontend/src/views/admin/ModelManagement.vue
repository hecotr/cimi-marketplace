<template>
  <div class="model-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>模型管理</h2>
          <el-button type="primary" @click="showDialog()">添加模型</el-button>
        </div>
      </template>

      <el-table :data="models" v-loading="loading">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="provider" label="提供商" width="120" />
        <el-table-column prop="modelName" label="模型" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="maxTokens" label="Max Tokens" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '活跃' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="showDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingModel ? '编辑模型' : '添加模型'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模型显示名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%;">
                <el-option
                  v-for="cat in categories"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="提供商" prop="provider">
              <el-input v-model="form.provider" placeholder="如: OpenAI, Anthropic" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="form.modelName" placeholder="如: gpt-4, claude-3-opus" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入模型描述" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="API 协议" prop="apiProtocol">
              <el-select v-model="form.apiProtocol" placeholder="请选择协议" style="width: 100%;">
                <el-option label="OpenAI 兼容" value="openai" />
                <el-option label="Anthropic" value="anthropic" />
                <el-option label="Azure OpenAI" value="azure" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Max Tokens" prop="maxTokens">
              <el-input-number v-model="form.maxTokens" :min="1000" :max="1000000" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="API Endpoint" prop="apiEndpoint">
          <el-input v-model="form.apiEndpoint" placeholder="如: https://api.openai.com/v1" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="版本">
              <el-input v-model="form.version" placeholder="如: 1.0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%;">
                <el-option label="活跃" value="active" />
                <el-option label="停用" value="inactive" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">高级配置</el-divider>

        <el-form-item label="默认参数 (JSON)">
          <el-input
            v-model="defaultParamsStr"
            type="textarea"
            :rows="3"
            placeholder='{"temperature": 0.7, "top_p": 1}'
          />
        </el-form-item>

        <el-form-item label="计费规则 (JSON)">
          <el-input
            v-model="billingRuleStr"
            type="textarea"
            :rows="3"
            placeholder='{"inputPrice": 0.01, "outputPrice": 0.03, "unit": "每1K tokens"}'
          />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import request from '@/api/request'

interface Category {
  id: number
  name: string
}

interface LlmModel {
  id: number
  name: string
  provider: string
  modelName: string
  description: string
  categoryId: number
  categoryName: string
  apiProtocol: string
  apiEndpoint: string
  maxTokens: number
  version: string
  status: string
  defaultParams: Record<string, any>
  billingRule: Record<string, any>
}

const models = ref<LlmModel[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingModel = ref<LlmModel | null>(null)
const saving = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  provider: '',
  modelName: '',
  description: '',
  categoryId: null as number | null,
  apiProtocol: 'openai',
  apiEndpoint: '',
  maxTokens: 4096,
  version: '',
  status: 'active',
  defaultParams: {} as Record<string, any>,
  billingRule: {} as Record<string, any>
})

// JSON 字符串用于表单编辑
const defaultParamsStr = computed({
  get: () => JSON.stringify(form.defaultParams, null, 2),
  set: (val: string) => {
    try {
      form.defaultParams = JSON.parse(val)
    } catch {
      // 解析失败时保持原值
    }
  }
})

const billingRuleStr = computed({
  get: () => JSON.stringify(form.billingRule, null, 2),
  set: (val: string) => {
    try {
      form.billingRule = JSON.parse(val)
    } catch {
      // 解析失败时保持原值
    }
  }
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { max: 100, message: '名称不能超过100字符', trigger: 'blur' }
  ],
  provider: [{ required: true, message: '请输入提供商', trigger: 'blur' }],
  modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  apiProtocol: [{ required: true, message: '请选择API协议', trigger: 'change' }],
  apiEndpoint: [{ required: true, message: '请输入API Endpoint', trigger: 'blur' }],
  maxTokens: [{ required: true, message: '请输入Max Tokens', trigger: 'blur' }]
}

const fetchModels = async () => {
  loading.value = true
  try {
    const res = await request.get<any, any>('/llm/admin/models')
    if (res.code === 200) {
      models.value = res.data || []
    }
  } catch (error) {
    console.error('获取模型列表失败', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await request.get<any, any>('/categories?assetType=llm')
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const showDialog = (model?: LlmModel) => {
  editingModel.value = model || null
  if (model) {
    form.name = model.name
    form.provider = model.provider
    form.modelName = model.modelName
    form.description = model.description || ''
    form.categoryId = model.categoryId
    form.apiProtocol = model.apiProtocol || 'openai'
    form.apiEndpoint = model.apiEndpoint || ''
    form.maxTokens = model.maxTokens || 4096
    form.version = model.version || ''
    form.status = model.status || 'active'
    form.defaultParams = model.defaultParams || {}
    form.billingRule = model.billingRule || {}
  } else {
    Object.assign(form, {
      name: '',
      provider: '',
      modelName: '',
      description: '',
      categoryId: null,
      apiProtocol: 'openai',
      apiEndpoint: '',
      maxTokens: 4096,
      version: '',
      status: 'active',
      defaultParams: {},
      billingRule: {}
    })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!formRef.value) return

  let valid = true
  await formRef.value.validate((v) => { valid = v })
  if (!valid) return

  // 验证 JSON 格式
  try {
    if (defaultParamsStr.value && defaultParamsStr.value !== '{}') {
      JSON.parse(defaultParamsStr.value)
    }
    if (billingRuleStr.value && billingRuleStr.value !== '{}') {
      JSON.parse(billingRuleStr.value)
    }
  } catch {
    ElMessage.error('JSON 格式不正确')
    return
  }

  saving.value = true
  try {
    const payload = {
      name: form.name,
      provider: form.provider,
      modelName: form.modelName,
      description: form.description,
      categoryId: form.categoryId,
      apiProtocol: form.apiProtocol,
      apiEndpoint: form.apiEndpoint,
      maxTokens: form.maxTokens,
      version: form.version,
      status: form.status,
      defaultParams: form.defaultParams,
      billingRule: form.billingRule
    }

    if (editingModel.value) {
      await request.put<any, any>(`/llm/admin/models/${editingModel.value.id}`, payload)
      ElMessage.success('更新成功')
    } else {
      await request.post<any, any>('/llm/admin/models', payload)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchModels()
  } catch (error: any) {
    const message = error?.response?.data?.message || '操作失败'
    ElMessage.error(message)
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row: LlmModel) => {
  try {
    await ElMessageBox.confirm('确定要删除此模型配置吗？', '警告', { type: 'warning' })
    await request.delete<any, any>(`/llm/admin/models/${row.id}`)
    ElMessage.success('删除成功')
    fetchModels()
  } catch (error) {
    // cancelled
  }
}

onMounted(() => {
  fetchModels()
  fetchCategories()
})
</script>

<style scoped>
.model-management {
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
</style>
