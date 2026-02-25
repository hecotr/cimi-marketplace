export interface SkillDTO {
  id: number
  name: string
  description: string
  category: string
  type: string
  viewCount: number
  downloadCount: number
  likeCount: number
  isLiked?: boolean
  isFavorited?: boolean
}

export interface SkillQueryRequest {
  keyword?: string
  category?: string
  sortBy?: string
  page?: number
  size?: number
}
