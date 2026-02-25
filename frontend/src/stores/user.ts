import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export interface User {
  id: number
  username: string
  email: string
  department: string
  role?: string
}

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const isAuthenticated = ref(!!token.value)

  function setUser(u: User | null) {
    user.value = u
  }

  function setToken(t: string | null) {
    token.value = t
    isAuthenticated.value = !!t
    if (t) {
      localStorage.setItem('token', t)
      axios.defaults.headers.common['Authorization'] = `Bearer ${t}`
    } else {
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
    }
  }

  function logout() {
    setUser(null)
    setToken(null)
  }

  function hasRole(role: string): boolean {
    return user.value?.role === role
  }

  function isAdmin(): boolean {
    return hasRole('admin')
  }

  return {
    user,
    token,
    isAuthenticated,
    setUser,
    setToken,
    logout,
    hasRole,
    isAdmin
  }
})
