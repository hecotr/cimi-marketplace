import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/llm/models'
  },
  {
    path: '/llm',
    name: 'LlmIndex',
    redirect: '/llm/models',
    children: [
      {
        path: 'models',
        name: 'LlmModels',
        component: () => import('@/views/llm/ModelList.vue')
      },
      {
        path: 'models/:id',
        name: 'LlmModelDetail',
        component: () => import('@/views/llm/ModelDetail.vue')
      },
      {
        path: 'my-keys',
        name: 'MyKeys',
        component: () => import('@/views/llm/MyKeys.vue')
      }
    ]
  },
  {
    path: '/skills',
    name: 'SkillsIndex',
    redirect: '/skills/list',
    children: [
      {
        path: 'list',
        name: 'SkillsList',
        component: () => import('@/views/skills/SkillList.vue')
      },
      {
        path: ':id',
        name: 'SkillDetail',
        component: () => import('@/views/skills/SkillDetail.vue')
      }
    ]
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue')
  },
  {
    path: '/publish',
    name: 'PublishAsset',
    component: () => import('@/views/asset/PublishAsset.vue')
  },
  {
    path: '/asset/:id/edit',
    name: 'EditAsset',
    component: () => import('@/views/asset/EditAsset.vue')
  },
  {
    path: '/my/drafts',
    name: 'MyDrafts',
    component: () => import('@/views/asset/MyDrafts.vue')
  },
  {
    path: '/my/assets',
    name: 'MyPublished',
    component: () => import('@/views/asset/MyPublished.vue')
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Statistics.vue')
      },
      {
        path: 'assets',
        name: 'AdminAssets',
        component: () => import('@/views/admin/AssetManagement.vue')
      },
      {
        path: 'approval',
        name: 'AdminApproval',
        component: () => import('@/views/admin/ReviewQueue.vue')
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManagement.vue')
      },
      {
        path: 'llm-config',
        name: 'AdminLlmConfig',
        component: () => import('@/views/admin/LLMConfig.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
