<template>
  <el-select v-model="selectedVersion" @change="handleVersionChange" placeholder="Select Version">
    <el-option
      v-for="version in versions"
      :key="version.version"
      :label="`v${version.version} ${version.isCurrent ? '(Current)' : ''}`"
      :value="version.version"
    >
      <span>v{{ version.version }}</span>
      <el-tag v-if="version.isCurrent" size="small" class="ml-2">Current</el-tag>
    </el-option>
  </el-select>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { assetApi } from '@/api/asset'
import type { AssetVersion } from '@/api/asset'

interface Props {
  assetId: number
  modelValue: string
}

const props = defineProps<Props>()
const emit = defineEmits(['update:modelValue', 'change'])

const versions = ref<AssetVersion[]>([])
const selectedVersion = ref(props.modelValue)

onMounted(async () => {
  await loadVersions()
})

async function loadVersions() {
  versions.value = await assetApi.getAssetVersions(props.assetId)
}

function handleVersionChange(version: string) {
  emit('update:modelValue', version)
  emit('change', version)
}

watch(() => props.modelValue, (val) => {
  selectedVersion.value = val
})
</script>

<style scoped>
.ml-2 {
  margin-left: 8px;
}
</style>
