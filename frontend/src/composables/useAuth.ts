import { useUserStore } from '@/stores/user'
import axios from 'axios'

export function useAuth() {
  const userStore = useUserStore()

  async function login(username: string, password: string) {
    try {
      const response = await axios.post('/api/auth/login', { username, password })
      const { token, user } = response.data.data
      userStore.setToken(token)
      userStore.setUser(user)
      return true
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }

  async function logout() {
    userStore.logout()
  }

  function checkAuth() {
    return userStore.isAuthenticated
  }

  function getUser() {
    return userStore.user
  }

  return {
    login,
    logout,
    checkAuth,
    getUser
  }
}
