import request from './request'
import type { SkillDTO, SkillQueryRequest } from '@/types/skill'

export const querySkills = (params: SkillQueryRequest) => {
  return request.get<{ records: SkillDTO[], total: number }>('/skills', { params })
}

export const getSkillDetail = (id: number) => {
  return request.get<SkillDTO>(`/skills/${id}`)
}

export const getCategories = () => {
  return request.get<string[]>('/skills/categories')
}

export const toggleLike = (id: number) => {
  return request.post(`/skills/${id}/like`)
}

export const toggleFavorite = (id: number) => {
  return request.post(`/skills/${id}/favorite`)
}

export const getFavorites = (page: number = 1, size: number = 20) => {
  return request.get<{ records: SkillDTO[], total: number }>('/skills/favorites', {
    params: { page, size }
  })
}

export const incrementView = (id: number) => {
  return request.get(`/skills/${id}/view`)
}

export const incrementDownload = (id: number) => {
  return request.get(`/skills/${id}/download`)
}
