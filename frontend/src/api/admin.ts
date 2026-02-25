import axios from 'axios'

export interface ApprovalCallbackRequest {
  action: 'approve' | 'reject' | 'request_changes'
  comment: string
}

export interface Statistics {
  totalAssets: number
  totalPublishedAssets: number
  pendingApprovals: number
  totalUsers: number
  assetsByType: Record<string, number>
  totalViews: number
  totalDownloads: number
  totalLikes: number
}

export interface Category {
  id: number
  name: string
  description?: string
  icon?: string
  parentId?: number
  parentName?: string
  sortOrder: number
  status: string
  assetCount?: number
  createdAt: string
  children?: Category[]
}

export interface CategoryRequest {
  name: string
  description?: string
  icon?: string
  parentId?: number
  sortOrder?: number
  status?: string
}

export interface LlmModelConfigDTO {
  id: number
  llmModelId: number
  modelName?: string
  configKey: string
  configValue: string
  configType: string
  isEncrypted: boolean
  createdAt: string
  updatedAt: string
}

export const adminApi = {
  // Approval
  async approveAsset(assetId: number, comment: string) {
    const response = await axios.post(`/api/admin/approval/${assetId}/approve`, {
      action: 'approve',
      comment
    })
    return response.data.data
  },

  async rejectAsset(assetId: number, comment: string) {
    const response = await axios.post(`/api/admin/approval/${assetId}/reject`, {
      action: 'reject',
      comment
    })
    return response.data.data
  },

  async requestChanges(assetId: number, comment: string) {
    const response = await axios.post(`/api/admin/approval/${assetId}/request-changes`, {
      action: 'request_changes',
      comment
    })
    return response.data.data
  },

  async getPendingApprovals() {
    const response = await axios.get('/api/admin/approval/pending')
    return response.data.data
  },

  async getApprovalHistory(assetId: number) {
    const response = await axios.get(`/api/admin/approval/history/${assetId}`)
    return response.data.data
  },

  // Statistics
  async getOverallStatistics(): Promise<Statistics> {
    const response = await axios.get('/api/admin/statistics/overall')
    return response.data.data
  },

  async getDailyStatistics(startDate?: string, endDate?: string) {
    const response = await axios.get('/api/admin/statistics/daily', {
      params: { startDate, endDate }
    })
    return response.data.data
  },

  async getCategoryStatistics() {
    const response = await axios.get('/api/admin/statistics/category')
    return response.data.data
  },

  // Categories
  async createCategory(request: CategoryRequest): Promise<Category> {
    const response = await axios.post('/api/admin/category', request)
    return response.data.data
  },

  async updateCategory(id: number, request: CategoryRequest): Promise<Category> {
    const response = await axios.put(`/api/admin/category/${id}`, request)
    return response.data.data
  },

  async deleteCategory(id: number) {
    await axios.delete(`/api/admin/category/${id}`)
  },

  async getCategory(id: number): Promise<Category> {
    const response = await axios.get(`/api/admin/category/${id}`)
    return response.data.data
  },

  async getCategories(status?: string): Promise<Category[]> {
    const response = await axios.get('/api/admin/category', {
      params: { status }
    })
    return response.data.data || []
  },

  async getCategoryTree(): Promise<Category[]> {
    const response = await axios.get('/api/admin/category/tree')
    return response.data.data || []
  },

  async updateCategorySort(id: number, sortOrder: number) {
    await axios.put(`/api/admin/category/${id}/sort`, null, {
      params: { sortOrder }
    })
  },

  // LLM Config
  async upsertLlmConfig(modelId: number, config: any) {
    const response = await axios.post('/api/admin/llm-config', {
      ...config,
      llmModelId: modelId
    })
    return response.data.data
  },

  async deleteLlmConfig(id: number) {
    await axios.delete(`/api/admin/llm-config/${id}`)
  },

  async getConfigsByModelId(modelId: number) {
    const response = await axios.get(`/api/admin/llm-config/model/${modelId}`)
    return response.data.data || []
  }
}
