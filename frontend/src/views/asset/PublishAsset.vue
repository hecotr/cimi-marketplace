<template>
  <div class="publish-asset">
    <el-page-header @back="goBack" title="Back">
      <template #content>
        <span class="text-large font-600 mr-3"> Publish Asset</span>
      </template>
    </el-page-header>

    <el-card class="form-card">
      <AssetForm @submit="handleSubmit" @cancel="goBack" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import AssetForm from '@/components/AssetForm.vue'

const router = useRouter()

function goBack() {
  router.back()
}

function handleSubmit() {
  router.push('/my/assets')
}
</script>

<style scoped>
.publish-asset {
  padding: 20px;
}

.form-card {
  margin-top: 20px;
}
</style>
