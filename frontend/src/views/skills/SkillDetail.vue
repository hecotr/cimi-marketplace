<template>
  <div class="skill-detail">
    <el-card v-if="skill">
      <template #header>
        <div class="card-header">
          <h3>{{ skill.name }}</h3>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div class="skill-info">
        <el-tag>{{ skill.category }}</el-tag>
        <el-tag type="info">{{ skill.type === 'file' ? '单文件' : '文件夹' }}</el-tag>
      </div>

      <p class="description">{{ skill.description }}</p>

      <div class="stats">
        <span><el-icon><View /></el-icon> {{ skill.viewCount }} 浏览</span>
        <span><el-icon><Download /></el-icon> {{ skill.downloadCount }} 下载</span>
        <span><el-icon><Star /></el-icon> {{ skill.likeCount }} 点赞</span>
      </div>

      <el-divider />

      <div class="content-section">
        <h4>Skill 内容</h4>
        <el-input
          v-model="content"
          type="textarea"
          :rows="20"
          readonly
        />
      </div>

      <el-divider />

      <div class="actions">
        <el-button @click="copyContent">
          <el-icon><DocumentCopy /></el-icon>
          复制
        </el-button>
        <el-button type="primary" @click="downloadSkill">
          <el-icon><Download /></el-icon>
          下载 {{ skill.type === 'file' ? '.md' : '.zip' }}
        </el-button>
        <el-button
          :type="skill.isLiked ? 'danger' : 'default'"
          @click="toggleLike"
        >
          <el-icon><Star /></el-icon>
          {{ skill.isLiked ? '已点赞' : '点赞' }}
        </el-button>
        <el-button
          :type="skill.isFavorited ? 'warning' : 'default'"
          @click="toggleFavorite"
        >
          <el-icon><StarFilled /></el-icon>
          {{ skill.isFavorited ? '已收藏' : '收藏' }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Download, Star, DocumentCopy, StarFilled } from '@element-plus/icons-vue'
import { getSkillDetail, toggleLike as toggleLikeApi, toggleFavorite as toggleFavoriteApi, incrementView, incrementDownload } from '@/api/skills'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const skillId = Number(route.params.id)
const skill = ref<any>()
const content = ref('# Sample Skill\n\nThis is a sample skill content.')

onMounted(async () => {
  const data = await getSkillDetail(skillId)
  skill.value = data.data
  await incrementView(skillId)
})

const copyContent = () => {
  navigator.clipboard.writeText(content.value)
  ElMessage.success('已复制到剪贴板')
}

const downloadSkill = async () => {
  await incrementDownload(skillId)
  const filename = skill.value.type === 'file' ? `${skill.value.name}.md` : `${skill.value.name}.zip`
  const blob = new Blob([content.value], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('下载成功')
}

const toggleLike = async () => {
  await toggleLikeApi(skillId)
  skill.value.isLiked = !skill.value.isLiked
  skill.value.likeCount += skill.value.isLiked ? 1 : -1
}

const toggleFavorite = async () => {
  await toggleFavoriteApi(skillId)
  skill.value.isFavorited = !skill.value.isFavorited
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.skill-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skill-info {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.description {
  color: #666;
  line-height: 1.8;
  margin: 15px 0;
}

.stats {
  display: flex;
  gap: 20px;
  color: #999;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content-section {
  margin-top: 20px;
}

.actions {
  display: flex;
  gap: 10px;
}
</style>
