<template>
  <el-container class="admin-layout">
    <el-aside width="250px" class="admin-sidebar">
      <div class="logo">
        <h2>AI Marketplace</h2>
        <span class="badge">Admin</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#1890ff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/admin/assets">
          <el-icon><Folder /></el-icon>
          <span>Asset Management</span>
        </el-menu-item>
        <el-menu-item index="/admin/approval">
          <el-icon><CircleCheck /></el-icon>
          <span>Review Queue</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Collection /></el-icon>
          <span>Categories</span>
        </el-menu-item>
        <el-menu-item index="/admin/llm-config">
          <el-icon><Setting /></el-icon>
          <span>LLM Config</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-content">
          <h1>{{ pageTitle }}</h1>
          <div class="header-actions">
            <el-button @click="goBack">Back to App</el-button>
            <el-button type="danger" @click="logout">Logout</el-button>
          </div>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DataLine,
  Folder,
  CircleCheck,
  Collection,
  Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles: Record<string, string> = {
    '/admin/dashboard': 'Dashboard',
    '/admin/assets': 'Asset Management',
    '/admin/approval': 'Review Queue',
    '/admin/categories': 'Category Management',
    '/admin/llm-config': 'LLM Configuration'
  }
  return titles[route.path] || 'Admin Panel'
})

function goBack() {
  router.push('/')
}

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-sidebar {
  background-color: #001529;
  height: 100vh;
}

.logo {
  padding: 20px;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
}

.badge {
  background: #1890ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.admin-header {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.admin-main {
  background: #f5f5f5;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
