import request from './request'
import { LlmModelDTO, LlmTestRequest, LlmTestResponse } from '@/types/llm'

export const getModels = () => {
  return request.get<LlmModelDTO[]>('/llm/models')
}

export const getModelDetail = (id: number) => {
  return request.get<LlmModelDTO>(`/llm/models/${id}`)
}

export const testModel = (data: LlmTestRequest) => {
  return request.post<LlmTestResponse>('/llm/test', data)
}
