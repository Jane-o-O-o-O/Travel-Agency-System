import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('@/pages/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    component: () => import('@/pages/Dashboard.vue'),
    meta: { title: '仪表盘' }
  },
  {
    path: '/customers',
    component: () => import('@/pages/Customer/List.vue'),
    meta: { title: '客户管理' }
  },
  {
    path: '/templates',
    component: () => import('@/pages/Template/List.vue'),
    meta: { title: '行程模板' }
  },
  {
    path: '/products',
    component: () => import('@/pages/Product/List.vue'),
    meta: { title: '旅游产品' }
  },
  {
    path: '/orders',
    component: () => import('@/pages/Order/List.vue'),
    meta: { title: '订单管理' }
  },
  {
    path: '/resources',
    component: () => import('@/pages/Resource/List.vue'),
    meta: { title: '资源管理' }
  },
  {
    path: '/tickets',
    component: () => import('@/pages/Ticket/List.vue'),
    meta: { title: '工单管理' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查认证
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
