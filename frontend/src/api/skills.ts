import request from './request'

export interface Skill {
  id: number
  assetType: string
  name: string
  description: string
  categoryId: number
  categoryName: string
  tags: string
  status: string
  currentVersionId: number
  createdBy: string
  createdAt: string
  updatedAt: string
  versions: SkillVersion[]
}

export interface SkillVersion {
  id: number
  versionNo: number
  content: string
  storagePath: string
  fileType: string
  viewCount: number
  downloadCount: number
  likeCount: number
  createdAt: string
}

export interface SkillPublishRequest {
  assetType: string
  name: string
  description: string
  categoryId: number
  tags: string
  content: string
  fileType: string
}

export interface AssetListParams {
  assetType?: string
  status?: string
  categoryId?: number
  keyword?: string
  page?: number
  size?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export const skillsApi = {
  // 获取 Skills 列表
  getSkills: (params: AssetListParams) =>
    request.get<any, any>('/assets', { params }),

  // 获取 Skill 详情
  getSkillById: (id: number) =>
    request.get<any, any>(`/assets/${id}`),

  // 发布 Skill
  publishSkill: (data: SkillPublishRequest) =>
    request.post<any, any>('/assets', data),

  // 更新 Skill
  updateSkill: (id: number, data: SkillPublishRequest) =>
    request.put<any, any>(`/assets/${id}`, data),

  // 提交审核
  submitForReview: (id: number) =>
    request.post<any, any>(`/assets/${id}/submit`),

  // 下架
  offline: (id: number) =>
    request.post<any, any>(`/assets/${id}/offline`),

  // 删除
  delete: (id: number) =>
    request.delete<any, any>(`/assets/${id}`),

  // 下载
  download: (id: number) =>
    request.post<any, any>(`/assets/${id}/download`),

  // 获取我的资产
  getMyAssets: (assetType?: string, status?: string) =>
    request.get<any, any>('/assets/my', { params: { assetType, status } })
}
