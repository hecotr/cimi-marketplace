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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
