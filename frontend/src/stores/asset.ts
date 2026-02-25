import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export interface Asset {
  id: number
  name: string
  type: string
  description: string
  categoryId: number
  categoryName?: string
  content: string
  status: string
  version: string
  viewCount: number
  downloadCount: number
  likeCount: number
  createdBy: number
  creatorName?: string
  createdAt: string
  isFavorite?: boolean
  isLiked?: boolean
}

export const useAssetStore = defineStore('asset', () => {
  const assets = ref<Asset[]>([])
  const myDrafts = ref<Asset[]>([])
  const myPublished = ref<Asset[]>([])
  const favorites = ref<Asset[]>([])
  const loading = ref(false)

  async function fetchAssets(type?: string, page = 1, size = 20) {
    loading.value = true
    try {
      const response = await axios.get('/api/asset/list', {
        params: { type, page, size }
      })
      assets.value = response.data.data || []
    } catch (error) {
      console.error('Failed to fetch assets:', error)
    } finally {
      loading.value = false
    }
  }

  async function searchAssets(keyword: string, type?: string, categoryId?: number) {
    loading.value = true
    try {
      const response = await axios.get('/api/asset/search', {
        params: { keyword, type, categoryId, page: 1, size: 50 }
      })
      assets.value = response.data.data || []
    } catch (error) {
      console.error('Failed to search assets:', error)
    } finally {
      loading.value = false
    }
  }

  async function fetchMyDrafts() {
    loading.value = true
    try {
      const response = await axios.get('/api/asset/my/drafts')
      myDrafts.value = response.data.data || []
    } catch (error) {
      console.error('Failed to fetch drafts:', error)
    } finally {
      loading.value = false
    }
  }

  async function fetchMyPublished() {
    loading.value = true
    try {
      const response = await axios.get('/api/asset/my/published')
      myPublished.value = response.data.data || []
    } catch (error) {
      console.error('Failed to fetch published:', error)
    } finally {
      loading.value = false
    }
  }

  async function fetchFavorites(assetType?: string) {
    loading.value = true
    try {
      const response = await axios.get('/api/interaction/favorites', {
        params: { assetType, page: 1, size: 50 }
      })
      favorites.value = response.data.data || []
    } catch (error) {
      console.error('Failed to fetch favorites:', error)
    } finally {
      loading.value = false
    }
  }

  async function toggleLike(assetId: number, assetType: string) {
    try {
      const response = await axios.post('/api/interaction/like', {
        assetId,
        assetType
      })
      const liked = response.data.data
      // Update local state
      const asset = assets.value.find(a => a.id === assetId)
      if (asset) {
        asset.isLiked = liked
        asset.likeCount = liked ? asset.likeCount + 1 : asset.likeCount - 1
      }
      return liked
    } catch (error) {
      console.error('Failed to toggle like:', error)
      throw error
    }
  }

  async function toggleFavorite(assetId: number, assetType: string) {
    try {
      await axios.post('/api/interaction/favorite', {
        assetId,
        assetType
      })
      return true
    } catch (error) {
      console.error('Failed to toggle favorite:', error)
      throw error
    }
  }

  function clear() {
    assets.value = []
    myDrafts.value = []
    myPublished.value = []
    favorites.value = []
  }

  return {
    assets,
    myDrafts,
    myPublished,
    favorites,
    loading,
    fetchAssets,
    searchAssets,
    fetchMyDrafts,
    fetchMyPublished,
    fetchFavorites,
    toggleLike,
    toggleFavorite,
    clear
  }
})
