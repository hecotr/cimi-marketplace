<template>
  <div class="favorites">
    <h2>我的收藏</h2>
    <el-row :gutter="20" v-if="skills.length > 0">
      <el-col :span="6" v-for="skill in skills" :key="skill.id">
        <el-card class="skill-card" @click="goToDetail(skill.id)">
          <div class="skill-name">{{ skill.name }}</div>
          <div class="skill-category">{{ skill.category }}</div>
          <div class="skill-desc">{{ skill.description }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-else description="暂无收藏" />

    <el-pagination
      v-if="skills.length > 0"
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="loadFavorites"
      style="margin-top: 20px; text-align: center"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFavorites } from '@/api/skills'
import type { SkillDTO } from '@/types/skill'

const router = useRouter()
const page = ref(1)
const size = ref(20)
const total = ref(0)

const skills = ref<SkillDTO[]>([])

onMounted(async () => {
  await loadFavorites()
})

const loadFavorites = async () => {
  const data = await getFavorites(page.value, size.value)
  skills.value = data.data.records || []
  total.value = data.data.total || 0
}

const goToDetail = (id: number) => {
  router.push(`/skills/${id}`)
}
</script>

<style scoped>
.favorites {
  padding: 20px;
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
}
</style>
