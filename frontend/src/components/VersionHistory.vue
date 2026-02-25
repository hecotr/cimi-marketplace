<template>
  <el-timeline>
    <el-timeline-item
      v-for="version in versions"
      :key="version.id"
      :timestamp="formatDate(version.createdAt)"
      :type="version.isCurrent ? 'primary' : 'info'"
    >
      <el-card>
        <div class="version-header">
          <h4>v{{ version.version }}</h4>
          <el-tag v-if="version.isCurrent" type="success" size="small">Current</el-tag>
        </div>
        <p v-if="version.changeNotes" class="change-notes">
          <strong>Changes:</strong> {{ version.changeNotes }}
        </p>
      </el-card>
    </el-timeline-item>
  </el-timeline>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { assetApi } from '@/api/asset'
import type { AssetVersion } from '@/api/asset'

interface Props {
  assetId: number
}

const props = defineProps<Props>()

const versions = ref<AssetVersion[]>([])

onMounted(async () => {
  await loadVersions()
})

async function loadVersions() {
  versions.value = await assetApi.getAssetVersions(props.assetId)
}

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.version-header h4 {
  margin: 0;
}

.change-notes {
  margin: 8px 0 0 0;
  color: #606266;
}
</style>
