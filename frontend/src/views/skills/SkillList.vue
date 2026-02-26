<template>
  <div class="skill-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>Skills 列表</h2>
          <el-button type="primary" @click="$router.push('/skills/publish')">
            发布 Skill
          </el-button>
        </div>
      </template>

      <div class="filters">
        <el-select v-model="filters.categoryId" placeholder="选择分类" clearable @change="fetchSkills">
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        <el-input
          v-model="filters.keyword"
          placeholder="搜索关键词"
          clearable
          style="width: 200px; margin-left: 16px;"
          @keyup.enter="fetchSkills"
        />
      </div>

      <el-table :data="skills" v-loading="loading" style="margin-top: 16px;">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createdBy" label="发布者" width="100" />
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text type="primary" @click="$router.push(`/skills/${row.id}`)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'

interface Skill {
  id: number
  name: string
  description: string
  categoryName: string
  createdBy: string
  createdAt: string
}

interface Category {
  id: number
  name: string
}

const skills = ref<Skill[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const filters = reactive({
  categoryId: null as number | null,
  keyword: ''
})

const fetchCategories = async () => {
  try {
    const res = await request.get<any, any>('/categories?assetType=skill')
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const fetchSkills = async () => {
  loading.value = true
  try {
    const params: any = { assetType: 'skill', status: 'approved' }
    if (filters.categoryId) params.categoryId = filters.categoryId
    if (filters.keyword) params.keyword = filters.keyword

    const res = await request.get<any, any>('/assets', { params })
    if (res.code === 200) {
      skills.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取 Skills 列表失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchSkills()
})
</script>

<style scoped>
.skill-list {
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

.filters {
  display: flex;
  align-items: center;
}
</style>
