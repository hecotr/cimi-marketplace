export interface LlmModelDTO {
  id: number
  name: string
  provider: string
  modelName: string
  description: string
  apiEndpoint: string
  maxTokens: number
  status: string
}

export interface LlmTestRequest {
  modelId: number
  prompt: string
  temperature?: number
  maxTokens?: number
  topP?: number
}

export interface LlmTestResponse {
  response: string
  responseTime: number
  tokenUsage: {
    inputTokens: number
    outputTokens: number
    totalTokens: number
  }
}
