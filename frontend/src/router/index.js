import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '@/components/Login.vue'
import Register from '@/components/Register.vue'
import AdminView from '../views/AdminView.vue'
import DashboardView from '../views/DashboardView.vue'
import SearchView from '../views/SearchView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/article/:id',
    name: 'article',
    component: () => import('../views/ArticleDetail.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    meta: { requiresAdmin: true }
  },
  { path: '/admin/dashboard', name: 'dashboard', component: DashboardView, meta: { requiresAdmin: true } },
   { path: '/search', name: 'search', component: SearchView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查管理员权限
router.beforeEach((to, from) => {
  if (to.meta.requiresAdmin) {
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('role')
    if (!token || role !== 'ADMIN') {
      alert('需要管理员权限')
      return '/'
    }
    // 允许访问，不需要 return（或者 return true）
    return true
  }
  // 其他路由直接放行
  return true
})
export default router