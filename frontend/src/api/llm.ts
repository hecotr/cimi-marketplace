import request from './request'

export interface LlmModel {
  id: number
  name: string
  provider: string
  modelName: string
  description: string
  categoryId: number
  categoryName: string
  apiProtocol: string
  maxTokens: number
  defaultParams: Record<string, any>
  status: string
  createdAt: string
}

export interface LlmTestRequest {
  modelConfigId: number
  prompt: string
  parameters?: Record<string, any>
}

export interface LlmTestResponse {
  response: string
  responseTime: number
  promptTokens: number
  completionTokens: number
  totalTokens: number
}

export const llmApi = {
  // 获取模型列表
  getModels: () =>
    request.get<any, any>('/llm/models'),

  // 获取模型详情
  getModelById: (id: number) =>
    request.get<any, any>(`/llm/models/${id}`),

  // 测试模型
  testModel: (data: LlmTestRequest) =>
    request.post<any, any>('/llm/test', data),

  // 管理员 - 创建模型
  createModel: (data: Partial<LlmModel>) =>
    request.post<any, any>('/admin/llm/models', data),

  // 管理员 - 更新模型
  updateModel: (id: number, data: Partial<LlmModel>) =>
    request.put<any, any>(`/admin/llm/models/${id}`, data),

  // 管理员 - 删除模型
  deleteModel: (id: number) =>
    request.delete<any, any>(`/admin/llm/models/${id}`)
}
