<template>
  <div class="favorites">
    <el-card>
      <template #header>
        <h2>我的收藏</h2>
      </template>

      <el-empty v-if="favorites.length === 0" description="暂无收藏" />

      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="item in favorites" :key="item.id">
          <el-card class="favorite-card" shadow="hover" @click="handleClick(item)">
            <h3>{{ item.name }}</h3>
            <p class="description">{{ item.description }}</p>
            <div class="meta">
              <el-tag size="small" :type="item.assetType === 'skill' ? 'success' : 'primary'">
                {{ item.assetType === 'skill' ? 'Skill' : 'LLM' }}
              </el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const favorites = ref<any[]>([])

// TODO: 实现收藏 API
const fetchFavorites = async () => {
  // 暂时返回空数组
  favorites.value = []
}

const handleClick = (item: any) => {
  if (item.assetType === 'skill') {
    router.push(`/skills/${item.id}`)
  } else {
    router.push(`/llm/${item.id}`)
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites {
  max-width: 1200px;
  margin: 0 auto;
}

.favorite-card {
  margin-bottom: 20px;
  cursor: pointer;
}

.favorite-card h3 {
  margin: 0 0 8px;
}

.description {
  color: #909399;
  font-size: 13px;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
