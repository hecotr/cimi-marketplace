<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #1890ff">
              <el-icon :size="24"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAssets || 0 }}</div>
              <div class="stat-label">Total Assets</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #52c41a">
              <el-icon :size="24"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalPublishedAssets || 0 }}</div>
              <div class="stat-label">Published</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #faad14">
              <el-icon :size="24"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingApprovals || 0 }}</div>
              <div class="stat-label">Pending Approval</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #722ed1">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
              <div class="stat-label">Total Users</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card>
          <template #header>
            <h3>Assets by Type</h3>
          </template>
          <div class="type-stats">
            <div v-for="(count, type) in statistics.assetsByType" :key="type" class="type-stat-item">
              <span class="type-name">{{ getTypeName(type) }}</span>
              <el-progress :percentage="getTypePercentage(type)" :color="getTypeColor(type)" />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <h3>Engagement</h3>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="engagement-item">
                <el-icon :size="32" color="#1890ff"><View /></el-icon>
                <div class="engagement-value">{{ statistics.totalViews || 0 }}</div>
                <div class="engagement-label">Views</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="engagement-item">
                <el-icon :size="32" color="#52c41a"><Download /></el-icon>
                <div class="engagement-value">{{ statistics.totalDownloads || 0 }}</div>
                <div class="engagement-label">Downloads</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="engagement-item">
                <el-icon :size="32" color="#f5222d"><Star /></el-icon>
                <div class="engagement-value">{{ statistics.totalLikes || 0 }}</div>
                <div class="engagement-label">Likes</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="24">
        <el-card>
          <template #header>
            <h3>Recent Activity (Last 30 Days)</h3>
          </template>
          <el-table :data="dailyStats" stripe>
            <el-table-column prop="date" label="Date" width="120" />
            <el-table-column prop="count" label="New Assets">
              <template #default="{ row }">
                <el-tag :type="row.count > 0 ? 'success' : 'info'">{{ row.count }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {
  DataLine,
  CircleCheck,
  Clock,
  User,
  View,
  Download,
  Star
} from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const statistics = ref<any>({})
const dailyStats = ref<any[]>([])

onMounted(async () => {
  await loadStatistics()
})

async function loadStatistics() {
  loading.value = true
  try {
    const stats = await adminApi.getOverallStatistics()
    statistics.value = stats

    // Load daily stats
    const daily = await adminApi.getDailyStatistics()
    dailyStats.value = daily.dailyStats || []
  } catch (error) {
    console.error('Failed to load statistics:', error)
  } finally {
    loading.value = false
  }
}

function getTypeName(type: string) {
  const names: Record<string, string> = {
    llm_model: 'LLM Models',
    skill: 'Skills'
  }
  return names[type] || type
}

function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    llm_model: '#1890ff',
    skill: '#52c41a'
  }
  return colors[type] || '#722ed1'
}

function getTypePercentage(type: string) {
  const total = statistics.value.totalAssets || 0
  const count = statistics.value.assetsByType?.[type] || 0
  return total > 0 ? Math.round((count / total) * 100) : 0
}
</script>

<style scoped>
.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.type-stats {
  padding: 10px 0;
}

.type-stat-item {
  margin-bottom: 20px;
}

.type-name {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.engagement-item {
  text-align: center;
  padding: 10px;
}

.engagement-value {
  font-size: 24px;
  font-weight: bold;
  margin: 10px 0;
}

.engagement-label {
  color: #909399;
}

.mt-4 {
  margin-top: 20px;
}
</style>
