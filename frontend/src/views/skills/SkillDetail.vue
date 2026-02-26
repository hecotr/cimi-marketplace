<template>
  <div class="skill-detail">
    <el-card v-if="skill">
      <template #header>
        <div class="card-header">
          <el-button text @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h2>{{ skill.name }}</h2>
          <div class="actions">
            <el-button
              :type="isFavorited ? 'warning' : 'default'"
              :icon="Star"
              @click="handleFavorite"
              :loading="favoriteLoading"
            >
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button
              :type="isLiked ? 'danger' : 'default'"
              @click="handleLike"
              :loading="likeLoading"
            >
              {{ isLiked ? '❤️ 已点赞' : '🤍 点赞' }}
            </el-button>
            <el-button type="primary" @click="handleDownload">
              下载
            </el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="分类">{{ skill.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="发布者">{{ skill.createdBy }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ skill.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(skill.status)">{{ getStatusText(skill.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标签" :span="2">
          <el-tag v-for="tag in (skill.tags || '').split(',').filter(Boolean)" :key="tag" style="margin-right: 4px;">
            {{ tag }}
          </el-tag>
          <span v-if="!skill.tags">无标签</span>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ skill.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <div v-if="currentVersion">
        <div class="content-header">
          <h3>内容</h3>
          <div class="stats">
            <span><el-icon><View /></el-icon> 浏览: {{ currentVersion.viewCount || 0 }}</span>
            <span><el-icon><Download /></el-icon> 下载: {{ currentVersion.downloadCount || 0 }}</span>
            <span><el-icon><Star /></el-icon> 点赞: {{ currentVersion.likeCount || 0 }}</span>
          </div>
        </div>
        <el-card shadow="never" class="content-card">
          <pre>{{ currentVersion.content || '暂无内容' }}</pre>
        </el-card>
      </div>

      <el-empty v-else description="暂无版本内容" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star, View, Download } from '@element-plus/icons-vue'
import request from '@/api/request'

interface Skill {
  id: number
  name: string
  description: string
  categoryName: string
  createdBy: string
  createdAt: string
  status: string
  tags: string
  versions: any[]
  currentVersionId: number
}

const route = useRoute()
const skill = ref<Skill | null>(null)

// 收藏/点赞状态
const isFavorited = ref(false)
const isLiked = ref(false)
const favoriteLoading = ref(false)
const likeLoading = ref(false)

const currentVersion = computed(() => {
  if (!skill.value?.versions?.length) return null
  return skill.value.versions.find((v: any) => v.id === skill.value?.currentVersionId) || skill.value.versions[0]
})

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    draft: 'info',
    pending_review: 'warning',
    approved: 'success',
    rejected: 'danger',
    offline: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    draft: '草稿',
    pending_review: '待审核',
    approved: '已发布',
    rejected: '已拒绝',
    offline: '已下架'
  }
  return texts[status] || status
}

const fetchSkill = async () => {
  const id = route.params.id
  try {
    const res = await request.get<any, any>(`/assets/${id}`)
    if (res.code === 200) {
      skill.value = res.data
      checkInteractionStatus()
    }
  } catch (error) {
    console.error('获取 Skill 详情失败', error)
    ElMessage.error('加载失败')
  }
}

const checkInteractionStatus = async () => {
  if (!skill.value) return

  try {
    // 检查收藏状态
    const favRes = await request.get<any, any>(
      `/interactions/favorite/check?assetId=${skill.value.id}&assetType=skill`
    )
    if (favRes.code === 200) {
      isFavorited.value = favRes.data?.isFavorited || false
    }

    // 检查点赞状态
    const likeRes = await request.get<any, any>(
      `/interactions/like/check?assetId=${skill.value.id}&assetType=skill`
    )
    if (likeRes.code === 200) {
      isLiked.value = likeRes.data?.isLiked || false
    }
  } catch (error) {
    console.error('检查状态失败', error)
  }
}

const handleFavorite = async () => {
  if (!skill.value) return

  favoriteLoading.value = true
  try {
    if (isFavorited.value) {
      await request.delete(
        `/interactions/favorite?assetId=${skill.value.id}&assetType=skill`
      )
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await request.post('/interactions/favorite', {
        assetId: skill.value.id,
        assetType: 'skill'
      })
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const handleLike = async () => {
  if (!skill.value) return

  likeLoading.value = true
  try {
    if (isLiked.value) {
      await request.delete(
        `/interactions/like?assetId=${skill.value.id}&assetType=skill`
      )
      isLiked.value = false
      // 更新点赞数显示
      if (currentVersion.value) {
        currentVersion.value.likeCount = Math.max(0, (currentVersion.value.likeCount || 0) - 1)
      }
      ElMessage.success('已取消点赞')
    } else {
      await request.post('/interactions/like', {
        assetId: skill.value.id,
        assetType: 'skill'
      })
      isLiked.value = true
      // 更新点赞数显示
      if (currentVersion.value) {
        currentVersion.value.likeCount = (currentVersion.value.likeCount || 0) + 1
      }
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    likeLoading.value = false
  }
}

const handleDownload = async () => {
  if (!skill.value) return
  try {
    await request.post(`/assets/${skill.value.id}/download`)
    // 更新下载数显示
    if (currentVersion.value) {
      currentVersion.value.downloadCount = (currentVersion.value.downloadCount || 0) + 1
    }
    // 触发下载
    if (currentVersion.value?.content) {
      const blob = new Blob([currentVersion.value.content], { type: 'text/markdown' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${skill.value.name}.md`
      a.click()
      URL.revokeObjectURL(url)
      ElMessage.success('下载成功')
    } else {
      ElMessage.warning('暂无内容可下载')
    }
  } catch (error) {
    console.error('下载失败', error)
    ElMessage.error('下载失败')
  }
}

onMounted(() => {
  fetchSkill()
})
</script>

<style scoped>
.skill-detail {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header h2 {
  margin: 0;
  flex: 1;
}

.actions {
  display: flex;
  gap: 8px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.content-header h3 {
  margin: 0;
}

.stats {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 13px;
}

.content-card {
  background: #fafafa;
}

.content-card pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}
</style>
