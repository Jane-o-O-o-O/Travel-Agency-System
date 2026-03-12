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
  // 用户端门户
  {
    path: '/portal',
    component: () => import('@/pages/portal/PortalLayout.vue'),
    meta: { title: '用户端' },
    children: [
      { path: '', redirect: '/portal/home' },
      { path: 'home', component: () => import('@/pages/portal/PortalHome.vue'), meta: { title: '首页' } },
      { path: 'login', component: () => import('@/pages/portal/PortalLogin.vue'), meta: { title: '登录' } },
      { path: 'register', component: () => import('@/pages/portal/PortalRegister.vue'), meta: { title: '注册' } },
      { path: 'submit-ticket', component: () => import('@/pages/portal/PortalSubmitTicket.vue'), meta: { title: '提交工单', requirePortalAuth: true } },
      { path: 'my-tickets', component: () => import('@/pages/portal/PortalMyTickets.vue'), meta: { title: '我的工单', requirePortalAuth: true } },
      { path: 'ticket/:id', component: () => import('@/pages/portal/PortalTicketDetail.vue'), meta: { title: '工单详情', requirePortalAuth: true } }
    ]
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

// 路由守卫：管理端用 token，用户端门户用 portalToken
router.beforeEach((to, from, next) => {
  const isPortal = to.path.startsWith('/portal')
  if (isPortal) {
    const portalToken = localStorage.getItem('portalToken')
    const noAuthPaths = ['/portal', '/portal/home', '/portal/login', '/portal/register']
    if (noAuthPaths.some(p => to.path === p || to.path === p + '/')) {
      next()
    } else if (to.meta.requirePortalAuth && !portalToken) {
      next('/portal/login')
    } else {
      next()
    }
  } else {
    if (to.path === '/login') {
      next()
    } else {
      const token = localStorage.getItem('token')
      if (!token) next('/login')
      else next()
    }
  }
})

export default router
