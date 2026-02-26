<template>
  <el-container class="main-layout">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-text">AI Marketplace</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
        router
      >
        <el-menu-item index="/llm">LLM 模型</el-menu-item>
        <el-menu-item index="/skills">Skills</el-menu-item>
        <el-menu-item index="/my-keys">我的 Keys</el-menu-item>
        <el-menu-item index="/my/drafts">我的草稿</el-menu-item>
        <el-menu-item index="/my/published">我的发布</el-menu-item>
        <el-menu-item index="/favorites">收藏</el-menu-item>
      </el-menu>

      <div class="user-section">
        <el-dropdown v-if="userStore.userInfo" trigger="click">
          <span class="user-info">
            <el-avatar :size="32" class="avatar">
              {{ userStore.userInfo.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <span class="username">{{ userStore.userInfo.username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="userStore.isAdmin()" @click="$router.push('/admin')">
                管理后台
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/llm')) return '/llm'
  if (path.startsWith('/skills')) return '/skills'
  if (path.startsWith('/my/drafts')) return '/my/drafts'
  if (path.startsWith('/my/published')) return '/my/published'
  if (path.startsWith('/favorites')) return '/favorites'
  return path
})

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.logo {
  cursor: pointer;
  margin-right: 40px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
}

.user-section {
  margin-left: auto;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.avatar {
  background: #409eff;
  color: #fff;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}
</style>
