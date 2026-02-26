import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/llm'
    },
    {
      path: '/login',
      component: () => import('@/views/Login.vue'),
      meta: { guest: true }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'llm',
          component: () => import('@/views/llm/ModelList.vue'),
          meta: { title: 'LLM 模型' }
        },
        {
          path: 'llm/:id',
          component: () => import('@/views/llm/ModelDetail.vue'),
          meta: { title: '模型详情' }
        },
        {
          path: 'skills',
          component: () => import('@/views/skills/SkillList.vue'),
          meta: { title: 'Skills' }
        },
        {
          path: 'skills/:id',
          component: () => import('@/views/skills/SkillDetail.vue'),
          meta: { title: 'Skill 详情' }
        },
        {
          path: 'skills/publish',
          component: () => import('@/views/skills/PublishSkill.vue'),
          meta: { title: '发布 Skill' }
        },
        {
          path: 'my/drafts',
          component: () => import('@/views/my/Drafts.vue'),
          meta: { title: '我的草稿' }
        },
        {
          path: 'my/published',
          component: () => import('@/views/my/Published.vue'),
          meta: { title: '我的发布' }
        },
        {
          path: 'favorites',
          component: () => import('@/views/Favorites.vue'),
          meta: { title: '我的收藏' }
        },
        {
          path: 'my-keys',
          component: () => import('@/views/llm/MyKeys.vue'),
          meta: { title: '我的 Keys' }
        }
      ]
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          redirect: '/admin/models'
        },
        {
          path: 'models',
          component: () => import('@/views/admin/ModelManagement.vue'),
          meta: { title: '模型管理' }
        },
        {
          path: 'categories',
          component: () => import('@/views/admin/CategoryManagement.vue'),
          meta: { title: '分类管理' }
        },
        {
          path: 'review',
          component: () => import('@/views/admin/ReviewQueue.vue'),
          meta: { title: '资产审核' }
        },
        {
          path: 'api-keys',
          component: () => import('@/views/admin/ApiKeyReview.vue'),
          meta: { title: 'Key 审批' }
        },
        {
          path: 'statistics',
          component: () => import('@/views/admin/Statistics.vue'),
          meta: { title: '统计报表' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  // 如果有 sessionId 但没有用户信息，尝试获取
  if (localStorage.getItem('sessionId') && !userStore.isLoggedIn) {
    await userStore.fetchUserInfo()
  }

  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !userStore.isAdmin()) {
    next({ path: '/' })
    return
  }

  // 已登录用户不能访问登录页
  if (to.meta.guest && userStore.isLoggedIn) {
    next({ path: '/' })
    return
  }

  next()
})

export default router
