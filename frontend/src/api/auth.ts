import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  role: string
  department: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export const authApi = {
  // 登录
  login: (data: LoginRequest) =>
    request.post<any, ApiResponse<UserInfo>>('/auth/login', data),

  // 登出
  logout: () =>
    request.post<any, ApiResponse<void>>('/auth/logout'),

  // 获取当前用户信息
  getCurrentUser: () =>
    request.get<any, ApiResponse<UserInfo>>('/auth/me')
}
