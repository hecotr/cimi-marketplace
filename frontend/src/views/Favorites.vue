<template>
  <div class="favorites">
    <el-page-header @back="goBack" title="Back">
      <template #content>
        <span class="text-large font-600">My Favorites</span>
      </template>
    </el-page-header>

    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="Type">
          <el-select v-model="filterType" @change="loadFavorites" placeholder="All">
            <el-option label="All" value="" />
            <el-option label="LLM Models" value="llm_model" />
            <el-option label="Skills" value="skill" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" v-if="favorites.length > 0">
      <el-col :span="6" v-for="item in favorites" :key="item.id">
        <el-card class="asset-card" @click="goToDetail(item)">
          <div class="asset-type">
            <el-tag :type="item.type === 'llm_model' ? 'primary' : 'success'" size="small">
              {{ item.type === 'llm_model' ? 'LLM Model' : 'Skill' }}
            </el-tag>
          </div>
          <div class="asset-name">{{ item.name }}</div>
          <div class="asset-category">{{ item.categoryName }}</div>
          <div class="asset-desc">{{ item.description }}</div>
          <div class="asset-stats">
            <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
            <span><el-icon><Star /></el-icon> {{ item.likeCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-else description="No favorites yet" />

    <el-pagination
      v-if="favorites.length > 0"
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      :page-sizes="[12, 24, 48]"
      layout="total, sizes, prev, pager, next"
      @current-change="loadFavorites"
      @size-change="loadFavorites"
      style="margin-top: 20px; text-align: center"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { View, Star } from '@element-plus/icons-vue'
import { useAssetStore } from '@/stores/asset'
import type { Asset } from '@/stores/asset'

const router = useRouter()
const assetStore = useAssetStore()

const page = ref(1)
const size = ref(12)
const total = ref(0)
const filterType = ref('')
const favorites = ref<Asset[]>([])

onMounted(async () => {
  await loadFavorites()
})

async function loadFavorites() {
  await assetStore.fetchFavorites(filterType.value || undefined)
  favorites.value = assetStore.favorites
  total.value = favorites.value.length
}

function goBack() {
  router.push('/')
}

function goToDetail(asset: Asset) {
  if (asset.type === 'llm_model') {
    router.push(`/llm/models/${asset.id}`)
  } else {
    router.push(`/skills/${asset.id}`)
  }
}
</script>

<style scoped>
.favorites {
  padding: 20px;
}

.filter-card {
  margin: 20px 0;
}

.asset-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.asset-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.asset-type {
  margin-bottom: 8px;
}

.asset-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}

.asset-category {
  color: #409eff;
  font-size: 12px;
  margin-bottom: 8px;
}

.asset-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 12px;
}

.asset-stats {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 12px;
}

.asset-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
