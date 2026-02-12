import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'suppliers',
        name: 'Suppliers',
        component: () => import('@/views/Supplier.vue'),
        meta: { title: '供应商管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'purchase-records',
        name: 'PurchaseRecords',
        component: () => import('@/views/PurchaseRecord.vue'),
        meta: { title: '采购记录', icon: 'Document' }
      },
      {
        path: 'supplier-scores',
        name: 'SupplierScores',
        component: () => import('@/views/SupplierScore.vue'),
        meta: { title: '供应商评分', icon: 'TrendCharts' }
      },
      {
        path: 'score-rules',
        name: 'ScoreRules',
        component: () => import('@/views/ScoreRule.vue'),
        meta: { title: '评分规则', icon: 'Setting' }
      },
      {
        path: 'warnings',
        name: 'Warnings',
        component: () => import('@/views/Warning.vue'),
        meta: { title: '预警管理', icon: 'Warning' }
      },
      {
        path: 'charts',
        name: 'Charts',
        component: () => import('@/views/Charts.vue'),
        meta: { title: '数据可视化', icon: 'DataLine' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
