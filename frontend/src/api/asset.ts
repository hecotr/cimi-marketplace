import axios from 'axios'
import type { Asset } from '@/stores/asset'

export interface AssetPublishRequest {
  type: string
  name: string
  description?: string
  categoryId?: number
  content?: string
  storagePath?: string
  version?: string
}

export interface AssetVersion {
  id: number
  assetId: number
  version: string
  content?: string
  storagePath?: string
  changeNotes?: string
  createdAt: string
  isCurrent: boolean
}

export const assetApi = {
  async createAsset(request: AssetPublishRequest) {
    const response = await axios.post('/api/asset', request)
    return response.data.data
  },

  async updateAsset(id: number, request: AssetPublishRequest) {
    const response = await axios.put(`/api/asset/${id}`, request)
    return response.data.data
  },

  async publishAsset(id: number) {
    const response = await axios.post(`/api/asset/${id}/publish`)
    return response.data.data
  },

  async createVersion(id: number, request: AssetPublishRequest) {
    const response = await axios.post(`/api/asset/${id}/version`, request)
    return response.data.data
  },

  async getAsset(id: number): Promise<Asset> {
    const response = await axios.get(`/api/asset/${id}`)
    return response.data.data
  },

  async getAssetByVersion(id: number, version: string): Promise<Asset> {
    const response = await axios.get(`/api/asset/${id}/version/${version}`)
    return response.data.data
  },

  async getAssetVersions(id: number): Promise<AssetVersion[]> {
    const response = await axios.get(`/api/asset/${id}/versions`)
    return response.data.data
  },

  async getPublishedAssets(type?: string, page = 1, size = 20): Promise<Asset[]> {
    const response = await axios.get('/api/asset/list', {
      params: { type, page, size }
    })
    return response.data.data || []
  },

  async searchAssets(
    keyword?: string,
    type?: string,
    categoryId?: number,
    page = 1,
    size = 20
  ): Promise<Asset[]> {
    const response = await axios.get('/api/asset/search', {
      params: { keyword, type, categoryId, page, size }
    })
    return response.data.data || []
  },

  async getMyDrafts(): Promise<Asset[]> {
    const response = await axios.get('/api/asset/my/drafts')
    return response.data.data || []
  },

  async getMyPublished(): Promise<Asset[]> {
    const response = await axios.get('/api/asset/my/published')
    return response.data.data || []
  },

  async deleteAsset(id: number) {
    await axios.delete(`/api/asset/${id}`)
  },

  async incrementDownload(id: number) {
    await axios.post(`/api/asset/${id}/download`)
  },

  // Interaction
  async toggleLike(assetId: number, assetType: string): Promise<boolean> {
    const response = await axios.post('/api/interaction/like', {
      assetId,
      assetType
    })
    return response.data.data
  },

  async toggleFavorite(assetId: number, assetType: string): Promise<void> {
    await axios.post('/api/interaction/favorite', {
      assetId,
      assetType
    })
  },

  async removeFavorite(assetId: number, assetType: string): Promise<void> {
    await axios.delete('/api/interaction/favorite', {
      params: { assetId, assetType }
    })
  },

  async getFavorites(assetType?: string, page = 1, size = 50): Promise<Asset[]> {
    const response = await axios.get('/api/interaction/favorites', {
      params: { assetType, page, size }
    })
    return response.data.data || []
  }
}
