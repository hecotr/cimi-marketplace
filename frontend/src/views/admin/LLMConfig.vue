<template>
  <div class="llm-config">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <h3>LLM Models</h3>
          </template>
          <el-table :data="models" stripe @row-click="selectModel" style="cursor: pointer">
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="provider" label="Provider" />
            <el-table-column prop="status" label="Status" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>Configuration</h3>
              <el-button type="primary" @click="addConfig" :disabled="!selectedModel">Add Config</el-button>
            </div>
          </template>

          <div v-if="selectedModel">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="Model">{{ selectedModel.name }}</el-descriptions-item>
              <el-descriptions-item label="Provider">{{ selectedModel.provider }}</el-descriptions-item>
              <el-descriptions-item label="API Endpoint">{{ selectedModel.apiEndpoint }}</el-descriptions-item>
            </el-descriptions>

            <el-divider>Configuration Keys</el-divider>

            <el-table :data="configs" v-loading="loading" stripe>
              <el-table-column prop="configKey" label="Key" width="200" />
              <el-table-column prop="configValue" label="Value">
                <template #default="{ row }">
                  <span v-if="row.isEncrypted">******</span>
                  <span v-else>{{ row.configValue }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="configType" label="Type" width="100" />
              <el-table-column prop="isEncrypted" label="Encrypted" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.isEncrypted ? 'danger' : 'success'" size="small">
                    {{ row.isEncrypted ? 'Yes' : 'No' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="Actions" width="150">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="editConfig(row)">Edit</el-button>
                  <el-button type="danger" size="small" @click="deleteConfig(row)">Delete</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <el-empty v-else description="Select a model to view configuration" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="dialogVisible"
      :title="editMode ? 'Edit Config' : 'Add Config'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="Key" prop="configKey">
          <el-input v-model="form.configKey" placeholder="Enter config key" />
        </el-form-item>
        <el-form-item label="Value" prop="configValue">
          <el-input
            v-model="form.configValue"
            :type="form.isEncrypted ? 'password' : 'text'"
            placeholder="Enter config value"
            show-password
          />
        </el-form-item>
        <el-form-item label="Type">
          <el-select v-model="form.configType">
            <el-option label="String" value="string" />
            <el-option label="Number" value="number" />
            <el-option label="Boolean" value="boolean" />
            <el-option label="JSON" value="json" />
          </el-select>
        </el-form-item>
        <el-form-item label="Encrypted">
          <el-switch v-model="form.isEncrypted" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminApi } from '@/api/admin'
import { assetApi } from '@/api/asset'
import type { LlmModelConfigDTO } from '@/api/admin'

const loading = ref(false)
const models = ref<any[]>([])
const selectedModel = ref<any>(null)
const configs = ref<LlmModelConfigDTO[]>([])

const dialogVisible = ref(false)
const editMode = ref(false)
const formRef = ref<FormInstance>()
const editingConfig = ref<LlmModelConfigDTO | null>(null)

const form = reactive({
  configKey: '',
  configValue: '',
  configType: 'string',
  isEncrypted: false
})

const rules: FormRules = {
  configKey: [{ required: true, message: 'Please enter key', trigger: 'blur' }],
  configValue: [{ required: true, message: 'Please enter value', trigger: 'blur' }]
}

onMounted(async () => {
  await loadModels()
})

async function loadModels() {
  loading.value = true
  try {
    models.value = await assetApi.getPublishedAssets('llm_model', 1, 100)
  } catch (error) {
    console.error('Failed to load models:', error)
  } finally {
    loading.value = false
  }
}

async function selectModel(model: any) {
  selectedModel.value = model
  await loadConfigs(model.id)
}

async function loadConfigs(modelId: number) {
  loading.value = true
  try {
    configs.value = await adminApi.getConfigsByModelId(modelId)
  } catch (error) {
    console.error('Failed to load configs:', error)
  } finally {
    loading.value = false
  }
}

function addConfig() {
  editMode.value = false
  editingConfig.value = null
  Object.assign(form, {
    configKey: '',
    configValue: '',
    configType: 'string',
    isEncrypted: false
  })
  dialogVisible.value = true
}

function editConfig(config: LlmModelConfigDTO) {
  editMode.value = true
  editingConfig.value = config
  Object.assign(form, config)
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value || !selectedModel.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await adminApi.upsertLlmConfig(selectedModel.value.id, form)
        ElMessage.success('Config saved')
        dialogVisible.value = false
        await loadConfigs(selectedModel.value.id)
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Save failed')
      }
    }
  })
}

async function deleteConfig(config: LlmModelConfigDTO) {
  try {
    await ElMessageBox.confirm('Are you sure to delete this config?', 'Warning', {
      type: 'warning'
    })
    await adminApi.deleteLlmConfig(config.id)
    ElMessage.success('Config deleted')
    if (selectedModel.value) {
      await loadConfigs(selectedModel.value.id)
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Delete failed')
    }
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

:deep(.el-table__row) {
  cursor: pointer;
}
</style>
