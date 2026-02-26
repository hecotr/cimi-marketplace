import axios, { AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加 sessionId
    const sessionId = localStorage.getItem('sessionId')
    if (sessionId) {
      config.headers['X-Session-Id'] = sessionId
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 保存 sessionId
    const sessionId = response.headers['x-session-id']
    if (sessionId) {
      localStorage.setItem('sessionId', sessionId)
    }
    return response.data
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      ElMessage.error('未登录，请先登录')
      localStorage.removeItem('sessionId')
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限')
    } else {
      ElMessage.error((error.response?.data as any)?.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
