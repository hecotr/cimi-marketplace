<template>
  <el-upload
    :action="uploadUrl"
    :headers="uploadHeaders"
    :before-upload="beforeUpload"
    :on-success="handleSuccess"
    :on-error="handleError"
    :file-list="fileList"
    :limit="limit"
    :on-exceed="handleExceed"
    :on-remove="handleRemove"
    drag
    multiple
  >
    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
    <div class="el-upload__text">
      Drag file here or <em>click to upload</em>
    </div>
    <template #tip>
      <div class="el-upload__tip">
        {{ tip }}
      </div>
    </template>
  </el-upload>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

interface Props {
  modelValue: string[]
  accept?: string
  limit?: number
  maxSize?: number
  tip?: string
}

const props = withDefaults(defineProps<Props>(), {
  accept: '*',
  limit: 5,
  maxSize: 10 * 1024 * 1024, // 10MB
  tip: 'Files up to 10MB'
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()

const fileList = ref<any[]>([])

const uploadUrl = computed(() => '/api/file/upload')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

function beforeUpload(file: File) {
  if (file.size > props.maxSize) {
    ElMessage.error(`File size cannot exceed ${props.maxSize / 1024 / 1024}MB`)
    return false
  }
  return true
}

function handleSuccess(response: any, file: any) {
  if (response.code === 200) {
    const urls = [...props.modelValue, response.data.url]
    emit('update:modelValue', urls)
    ElMessage.success('Upload successful')
  } else {
    ElMessage.error(response.message || 'Upload failed')
  }
}

function handleError(error: any) {
  ElMessage.error('Upload failed')
  console.error('Upload error:', error)
}

function handleExceed() {
  ElMessage.warning(`Cannot upload more than ${props.limit} files`)
}

function handleRemove(file: any) {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    const urls = [...props.modelValue]
    urls.splice(index, 1)
    emit('update:modelValue', urls)
  }
}
</script>

<style scoped>
:deep(.el-upload-dragger) {
  padding: 40px 0;
}
</style>
