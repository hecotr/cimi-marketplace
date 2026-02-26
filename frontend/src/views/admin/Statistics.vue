<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalModels }}</div>
          <div class="stat-label">LLM 模型</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalSkills }}</div>
          <div class="stat-label">Skills</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">用户数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.pendingReview }}</div>
          <div class="stat-label">待审核</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <h2>最近活动</h2>
      </template>
      <el-empty description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import request from '@/api/request'

const stats = reactive({
  totalModels: 0,
  totalSkills: 0,
  totalUsers: 0,
  pendingReview: 0
})

const fetchStats = async () => {
  try {
    // 获取模型数量
    const modelsRes = await request.get<any, any>('/llm/models')
    if (modelsRes.code === 200) {
      stats.totalModels = modelsRes.data?.length || 0
    }

    // 获取 Skills 数量
    const skillsRes = await request.get<any, any>('/assets?assetType=skill&status=approved')
    if (skillsRes.code === 200) {
      stats.totalSkills = skillsRes.data?.total || 0
    }

    // 获取待审核数量
    const reviewRes = await request.get<any, any>('/assets?status=pending_review')
    if (reviewRes.code === 200) {
      stats.pendingReview = reviewRes.data?.total || 0
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.statistics {
  max-width: 1200px;
  margin: 0 auto;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>
