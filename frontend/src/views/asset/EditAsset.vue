<template>
  <div class="edit-asset">
    <el-page-header @back="goBack" title="Back">
      <template #content>
        <span class="text-large font-600 mr-3"> Edit Asset</span>
      </template>
    </el-page-header>

    <el-card class="form-card" v-loading="loading">
      <AssetForm v-if="asset" :edit-id="asset.id" :initial-data="asset" @submit="handleSubmit" @cancel="goBack" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AssetForm from '@/components/AssetForm.vue'
import { assetApi } from '@/api/asset'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const asset = ref<any>(null)

onMounted(async () => {
  loading.value = true
  try {
    asset.value = await assetApi.getAsset(Number(route.params.id))
  } catch (error) {
    console.error('Failed to load asset:', error)
  } finally {
    loading.value = false
  }
})

function goBack() {
  router.back()
}

function handleSubmit() {
  router.push('/my/assets')
}
</script>

<style scoped>
.edit-asset {
  padding: 20px;
}

.form-card {
  margin-top: 20px;
}
</style>
