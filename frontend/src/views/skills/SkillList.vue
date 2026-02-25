<template>
  <div class="skill-list">
    <div class="filters">
      <el-input
        v-model="keyword"
        placeholder="搜索 Skills..."
        clearable
        style="width: 300px"
        @change="loadSkills"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="category"
        placeholder="选择分类"
        clearable
        style="width: 200px"
        @change="loadSkills"
      >
        <el-option
          v-for="cat in categories"
          :key="cat"
          :label="cat"
          :value="cat"
        />
      </el-select>

      <el-select
        v-model="sortBy"
        placeholder="排序方式"
        style="width: 150px"
        @change="loadSkills"
      >
        <el-option label="最新" value="created_at" />
        <el-option label="浏览量" value="view_count" />
        <el-option label="下载量" value="download_count" />
        <el-option label="点赞数" value="like_count" />
      </el-select>
    </div>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6" v-for="skill in skills" :key="skill.id">
        <el-card class="skill-card" @click="goToDetail(skill.id)">
          <div class="skill-name">{{ skill.name }}</div>
          <div class="skill-category">{{ skill.category }}</div>
          <div class="skill-desc">{{ skill.description }}</div>
          <div class="skill-stats">
            <span><el-icon><View /></el-icon> {{ skill.viewCount }}</span>
            <span><el-icon><Download /></el-icon> {{ skill.downloadCount }}</span>
            <span><el-icon><Star /></el-icon> {{ skill.likeCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="loadSkills"
      style="margin-top: 20px; text-align: center"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, Download, Star } from '@element-plus/icons-vue'
import { querySkills, getCategories } from '@/api/skills'
import type { SkillDTO } from '@/types/skill'

const router = useRouter()
const keyword = ref('')
const category = ref('')
const sortBy = ref('created_at')
const page = ref(1)
const size = ref(20)
const total = ref(0)

const skills = ref<SkillDTO[]>([])
const categories = ref<string[]>([])

onMounted(async () => {
  await loadCategories()
  await loadSkills()
})

const loadCategories = async () => {
  const data = await getCategories()
  categories.value = data.data || []
}

const loadSkills = async () => {
  const data = await querySkills({
    keyword: keyword.value,
    category: category.value,
    sortBy: sortBy.value,
    page: page.value,
    size: size.value
  })
  skills.value = data.data.records || []
  total.value = data.data.total || 0
}

const goToDetail = (id: number) => {
  router.push(`/skills/${id}`)
}
</script>

<style scoped>
.skill-list {
  padding: 20px;
}

.filters {
  display: flex;
  gap: 15px;
}

.skill-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.skill-card:hover {
  transform: translateY(-5px);
}

.skill-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}

.skill-category {
  color: #409eff;
  font-size: 12px;
  margin-bottom: 8px;
}

.skill-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 12px;
}

.skill-stats {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}

.skill-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
