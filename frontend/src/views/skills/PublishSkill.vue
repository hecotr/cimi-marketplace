<template>
  <div class="publish-skill">
    <el-card>
      <template #header>
        <h2>{{ isEditMode ? '编辑 Skill' : '发布 Skill' }}</h2>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入 Skill 名称" maxlength="200" show-word-limit />
        </el-form-item>

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

        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" maxlength="1000" show-word-limit />
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔，如：写作,翻译,总结" />
        </el-form-item>

        <el-divider content-position="left">内容</el-divider>

        <el-form-item label="输入方式">
          <el-radio-group v-model="inputMode">
            <el-radio value="editor">在线编辑</el-radio>
            <el-radio value="upload">文件上传</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 在线编辑模式 -->
        <el-form-item v-if="inputMode === 'editor'" label="Prompt 内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="请输入 Prompt 模板内容，可以使用 {{变量名}} 来标记需要用户输入的变量"
          />
        </el-form-item>

        <!-- 文件上传模式 -->
        <el-form-item v-else label="上传文件">
          <FileUploader
            v-model="uploadedFile"
            accept-types=".md, .zip"
            :max-size-mb="50"
            @update:model-value="handleFileChange"
          />
          <div class="upload-tip">
            <p>支持 .md（Markdown）或 .zip（压缩包）格式</p>
          </div>
        </el-form-item>

        <el-divider />

        <el-form-item>
          <el-button @click="router.back()">取消</el-button>
          <el-button type="default" :loading="submitting" @click="handleSaveDraft">
            保存草稿
          </el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEditMode ? '保存修改' : '提交审核' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '@/api/request'
import FileUploader from '@/components/FileUploader.vue'

interface Category {
  id: number
  name: string
}

interface FileInfo {
  key: string
  url: string
  size: number
  fileName: string
}

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const categories = ref<Category[]>([])
const submitting = ref(false)
const inputMode = ref<'editor' | 'upload'>('editor')
const uploadedFile = ref<FileInfo | null>(null)

// 判断是否为编辑模式
const skillId = computed(() => route.query.id ? Number(route.query.id) : null)
const isEditMode = computed(() => !!skillId.value)

const form = reactive({
  name: '',
  categoryId: null as number | null,
  description: '',
  tags: '',
  content: '',
  fileType: 'md',
  storagePath: ''
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { max: 200, message: '名称不能超过200字符', trigger: 'blur' }
  ],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' },
    { max: 1000, message: '描述不能超过1000字符', trigger: 'blur' }
  ]
}

const fetchCategories = async () => {
  try {
    const res = await request.get<any, any>('/categories?assetType=skill')
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const fetchSkill = async () => {
  if (!skillId.value) return

  try {
    const res = await request.get<any, any>(`/assets/${skillId.value}`)
    if (res.code === 200 && res.data) {
      const skill = res.data
      form.name = skill.name
      form.categoryId = skill.categoryId
      form.description = skill.description
      form.tags = skill.tags || ''
      form.fileType = skill.versions?.[0]?.fileType || 'md'
      form.storagePath = skill.versions?.[0]?.storagePath || ''

      // 如果有内容，使用编辑模式；如果有存储路径，使用上传模式
      if (skill.versions?.[0]?.content) {
        form.content = skill.versions[0].content
        inputMode.value = 'editor'
      } else if (form.storagePath) {
        inputMode.value = 'upload'
      }
    }
  } catch (error) {
    console.error('获取 Skill 详情失败', error)
    ElMessage.error('加载失败')
    router.push('/my/drafts')
  }
}

const handleFileChange = (file: FileInfo | null) => {
  if (file) {
    form.storagePath = file.key
    form.fileType = file.fileName.endsWith('.zip') ? 'zip' : 'md'
    form.content = '' // 清空内容，因为使用文件
  } else {
    form.storagePath = ''
    form.fileType = 'md'
  }
}

const handleSaveDraft = async () => {
  await submitAsset(false)
}

const handleSubmit = async () => {
  await submitAsset(!isEditMode.value)
}

const submitAsset = async (submitForReview: boolean) => {
  if (!formRef.value) return

  // 验证
  let valid = true
  await formRef.value.validate((v) => { valid = v })
  if (!valid) return

  // 检查编辑模式下内容是否为空
  if (inputMode.value === 'editor' && !form.content?.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  // 检查上传模式是否有文件
  if (inputMode.value === 'upload' && !form.storagePath) {
    ElMessage.warning('请上传文件')
    return
  }

  submitting.value = true
  try {
    const payload = {
      assetType: 'skill',
      name: form.name,
      categoryId: form.categoryId,
      description: form.description,
      tags: form.tags,
      content: inputMode.value === 'editor' ? form.content : '',
      fileType: form.fileType,
      storagePath: form.storagePath
    }

    let res
    if (isEditMode.value && skillId.value) {
      // 更新
      res = await request.put<any, any>(`/assets/${skillId.value}`, payload)
    } else {
      // 新建
      res = await request.post<any, any>('/assets', payload)
    }

    if (res.code === 200) {
      const assetId = isEditMode.value ? skillId.value : res.data

      if (submitForReview && assetId) {
        await request.post(`/assets/${assetId}/submit`)
        ElMessage.success('已提交审核')
        router.push('/my/published')
      } else {
        ElMessage.success(isEditMode.value ? '保存成功' : '草稿保存成功')
        router.push('/my/drafts')
      }
    }
  } catch (error: any) {
    const message = error?.response?.data?.message || '操作失败'
    ElMessage.error(message)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCategories()
  if (isEditMode.value) {
    fetchSkill()
  }
})
</script>

<style scoped>
.publish-skill {
  max-width: 800px;
  margin: 0 auto;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.upload-tip p {
  margin: 0;
}
</style>
