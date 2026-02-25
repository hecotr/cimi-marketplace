<template>
  <div id="app">
    <el-container>
      <el-header class="header">
        <div class="logo">AI Marketplace</div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="menu"
          router
        >
          <el-menu-item index="/llm/models">LLM 模型</el-menu-item>
          <el-menu-item index="/skills/list">Skills</el-menu-item>
          <el-menu-item index="/favorites">我的收藏</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => {
  if (route.path.startsWith('/llm')) return '/llm/models'
  if (route.path.startsWith('/skills')) return '/skills/list'
  return route.path
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  min-height: 100vh;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px;
  color: #409eff;
}

.menu {
  flex: 1;
  border: none;
}
</style>
