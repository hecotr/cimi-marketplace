<template>
  <div class="file-uploader">
    <el-upload
      ref="uploadRef"
      class="upload-dragger"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :multiple="false"
      :limit="1"
      :file-list="fileList"
      :auto-upload="autoUpload"
      :show-file-list="false"
      :accept="acceptTypes"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-progress="handleProgress"
      drag
    >
      <div v-if="!fileInfo" class="upload-content">
        <el-icon class="upload-icon"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处，或 <em>点击上传</em>
        </div>
        <div class="el-upload__tip">
          支持 {{ acceptTypesDisplay }} 文件，最大 {{ maxSizeMB }}MB
        </div>
      </div>
      <div v-else class="uploaded-content">
        <el-icon class="file-icon"><Document /></el-icon>
        <div class="file-info">
          <div class="file-name">{{ fileInfo.fileName }}</div>
          <div class="file-size">{{ formatSize(fileInfo.size) }}</div>
        </div>
        <el-button
          type="danger"
          text
          :icon="Delete"
          @click.stop="handleRemove"
        >
          删除
        </el-button>
      </div>
    </el-upload>

    <el-progress
      v-if="uploading"
      :percentage="progress"
      :status="progressStatus"
      class="upload-progress"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document, Delete } from '@element-plus/icons-vue'
import type { UploadInstance, UploadFile } from 'element-plus'

interface FileInfo {
  key: string
  url: string
  size: number
  fileName: string
}

interface Props {
  modelValue?: FileInfo | null
  acceptTypes?: string
  maxSizeMB?: number
  autoUpload?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: null,
  acceptTypes: '.md,.zip',
  maxSizeMB: 50,
  autoUpload: true
})

const emit = defineEmits<{
  'update:modelValue': [value: FileInfo | null]
  'success': [file: FileInfo]
  'error': [error: Error]
}>()

const uploadRef = ref<UploadInstance>()
const fileInfo = ref<FileInfo | null>(props.modelValue)
const fileList = ref<UploadFile[]>([])
const uploading = ref(false)
const progress = ref(0)
const progressStatus = ref<'success' | 'exception' | 'warning' | ''>('')

// 上传地址和请求头
const uploadUrl = '/api/files/upload'
const uploadHeaders = computed(() => {
  const sessionId = localStorage.getItem('sessionId')
  return sessionId ? { 'X-Session-Id': sessionId } : {}
})

const acceptTypesDisplay = computed(() => {
  return props.acceptTypes.split(',').map(t => t.trim().replace('.', '').toUpperCase()).join(', ')
})

// 监听外部值变化
watch(() => props.modelValue, (val) => {
  fileInfo.value = val
})

// 上传前验证
const handleBeforeUpload = (file: File) => {
  // 验证文件大小
  const maxSize = props.maxSizeMB * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSizeMB}MB`)
    return false
  }

  // 验证文件类型
  const allowedExtensions = props.acceptTypes.split(',').map(t => t.trim().toLowerCase())
  const fileName = file.name.toLowerCase()
  const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext))

  if (!hasValidExtension) {
    ElMessage.error(`只支持 ${acceptTypesDisplay.value} 文件`)
    return false
  }

  uploading.value = true
  progress.value = 0
  progressStatus.value = ''
  return true
}

// 上传进度
const handleProgress = (event: { percent: number }) => {
  progress.value = Math.floor(event.percent)
}

// 上传成功
const handleSuccess = (response: any, _file: UploadFile) => {
  uploading.value = false
  progressStatus.value = 'success'

  if (response.code === 200) {
    fileInfo.value = response.data
    emit('update:modelValue', fileInfo.value)
    if (fileInfo.value) {
      emit('success', fileInfo.value)
    }
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
    emit('error', new Error(response.message))
  }
}

// 上传失败
const handleError = (error: Error) => {
  uploading.value = false
  progressStatus.value = 'exception'
  ElMessage.error('上传失败: ' + error.message)
  emit('error', error)
}

// 删除文件
const handleRemove = () => {
  fileInfo.value = null
  fileList.value = []
  emit('update:modelValue', null)
}

// 格式化文件大小
const formatSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 手动提交上传（非自动上传时使用）
const submit = () => {
  uploadRef.value?.submit()
}

// 暴露方法给父组件
defineExpose({
  submit,
  clearFiles: () => {
    uploadRef.value?.clearFiles()
    handleRemove()
  }
})
</script>

<style scoped>
.file-uploader {
  width: 100%;
}

.upload-dragger {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: auto;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-content {
  text-align: center;
  padding: 30px;
}

.upload-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.el-upload__text {
  color: #606266;
  margin-bottom: 8px;
}

.el-upload__text em {
  color: #409eff;
  font-style: normal;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
}

.uploaded-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  width: 100%;
}

.file-icon {
  font-size: 40px;
  color: #409eff;
}

.file-info {
  flex: 1;
}

.file-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.upload-progress {
  margin-top: 16px;
}
</style>
