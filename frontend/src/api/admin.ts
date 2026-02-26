import request from './request'

export interface Category {
  id: number
  name: string
  parentId: number
  assetType: string
  sortOrder: number
}

export interface ApiKey {
  id: number
  userId: number
  modelConfigId: number
  keyValue: string
  apiProtocol: string
  status: string
  expiryType: string
  applyTime: string
  approveTime: string
  expiryDate: string
  approverId: number
  rejectionReason: string
}

export interface ApiKeyApplyRequest {
  modelConfigId: number
  apiProtocol: string
  expiryType: string
}

export const adminApi = {
  // ===== 分类管理 =====
  getCategories: (assetType?: string) =>
    request.get<any, any>('/categories', { params: { assetType } }),

  createCategory: (data: Partial<Category>) =>
    request.post<any, any>('/admin/categories', data),

  updateCategory: (id: number, data: Partial<Category>) =>
    request.put<any, any>(`/admin/categories/${id}`, data),

  deleteCategory: (id: number) =>
    request.delete<any, any>(`/admin/categories/${id}`),

  // ===== 资产审核 =====
  getPendingAssets: (page = 1, size = 10) =>
    request.get<any, any>('/admin/assets/pending', { params: { page, size } }),

  approveAsset: (id: number, comment?: string) =>
    request.post<any, any>(`/admin/assets/${id}/approve`, { comment }),

  rejectAsset: (id: number, comment: string) =>
    request.post<any, any>(`/admin/assets/${id}/reject`, { comment }),

  // ===== API Key 审批 =====
  getPendingApiKeys: () =>
    request.get<any, any>('/admin/api-keys/pending'),

  approveApiKey: (id: number) =>
    request.post<any, any>(`/admin/api-keys/${id}/approve`),

  rejectApiKey: (id: number, reason: string) =>
    request.post<any, any>(`/admin/api-keys/${id}/reject`, { reason })
}

export const apiKeyApi = {
  // 申请 API Key
  apply: (data: ApiKeyApplyRequest) =>
    request.post<any, any>('/api-keys', data),

  // 获取我的 API Keys
  getMyKeys: () =>
    request.get<any, any>('/api-keys/my'),

  // 撤销 API Key
  revoke: (id: number) =>
    request.post<any, any>(`/api-keys/${id}/revoke`)
}

export const interactionApi = {
  // 收藏
  addFavorite: (assetId: number, assetType: string) =>
    request.post<any, any>('/interactions/favorite', { assetId, assetType }),

  removeFavorite: (assetId: number, assetType: string) =>
    request.delete<any, any>(`/interactions/favorite?assetId=${assetId}&assetType=${assetType}`),

  checkFavorite: (assetId: number, assetType: string) =>
    request.get<any, any>(`/interactions/favorite/check?assetId=${assetId}&assetType=${assetType}`),

  // 点赞
  addLike: (assetId: number, assetType: string, versionId?: number) =>
    request.post<any, any>('/interactions/like', { assetId, assetType, versionId }),

  removeLike: (assetId: number, assetType: string, versionId?: number) =>
    request.delete<any, any>(`/interactions/like?assetId=${assetId}&assetType=${assetType}${versionId ? `&versionId=${versionId}` : ''}`),

  checkLike: (assetId: number, assetType: string, versionId?: number) =>
    request.get<any, any>(`/interactions/like/check?assetId=${assetId}&assetType=${assetType}${versionId ? `&versionId=${versionId}` : ''}`)
}
