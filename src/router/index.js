import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: () => import('@/views/Register.vue') },
  {
    path: '/user',
    component: Layout,
    meta: { role: 'student' },
    redirect: '/user/home',
    children: [
      { path: 'home', component: () => import('@/views/user/Home.vue') },
      { path: 'exam-center', component: () => import('@/views/user/ExamCenter.vue') },
      { path: 'my-exams', component: () => import('@/views/user/MyExams.vue') },
      { path: 'exam/:recordId', component: () => import('@/views/user/ExamDo.vue') },
      { path: 'score', component: () => import('@/views/user/Score.vue') },
      { path: 'profile', component: () => import('@/views/user/Profile.vue') },
      { path: 'help', component: () => import('@/views/user/Help.vue') },
    ],
  },
  {
    path: '/admin',
    component: Layout,
    meta: { role: ['admin', 'teacher'] },
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'exam', component: () => import('@/views/admin/ExamManage.vue') },
      { path: 'question', component: () => import('@/views/admin/QuestionManage.vue') },
      { path: 'user', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'score', component: () => import('@/views/admin/ScoreManage.vue') },
      { path: 'paper', component: () => import('@/views/admin/PaperManage.vue') },
      { path: 'settings', component: () => import('@/views/admin/Settings.vue') },
    ],
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role')
  if (!role && to.path !== '/login' && to.path !== '/register') {
    next('/login')
    return
  }
  if (to.meta.role) {
    const allowed = Array.isArray(to.meta.role) ? to.meta.role.includes(role) : to.meta.role === role
    if (!allowed) {
      if (role === 'student') next('/user')
      else if (role === 'admin' || role === 'teacher') next('/admin')
      else next('/login')
      return
    }
  }
  next()
})

export default router
