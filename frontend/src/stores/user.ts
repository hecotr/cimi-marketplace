import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type UserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref(false)

  // 登录
  const login = async (username: string, password: string) => {
    try {
      const res = await authApi.login({ username, password })
      if (res.code === 200 && res.data) {
        userInfo.value = res.data
        isLoggedIn.value = true
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      // ignore
    } finally {
      userInfo.value = null
      isLoggedIn.value = false
      localStorage.removeItem('sessionId')
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const res = await authApi.getCurrentUser()
      if (res.code === 200 && res.data) {
        userInfo.value = res.data
        isLoggedIn.value = true
        return true
      }
      return false
    } catch (error) {
      return false
    }
  }

  // 检查是否为管理员
  const isAdmin = () => {
    return userInfo.value?.role === 'admin'
  }

  return {
    userInfo,
    isLoggedIn,
    login,
    logout,
    fetchUserInfo,
    isAdmin
  }
})
