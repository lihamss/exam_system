<template>
  <div class="layout-container">
    <TopHeader
      :title="layoutConfig.title"
      :menu-items="layoutConfig.menuItems"
      :active-key="activeKey"
      @nav-change="handleNavChange"
      @logout="handleLogout"
    />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import TopHeader from '@/components/common/TopHeader.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const layoutConfig = computed(() => {
  const role = userStore.role
  if (role === 'admin' || role === 'teacher') {
    return {
      title: '在线考试系统 - 管理端',
      menuItems: [
        { key: 'dashboard', label: '控制台', path: '/admin/dashboard' },
        { key: 'exam', label: '考试管理', path: '/admin/exam' },
        { key: 'question', label: '题库管理', path: '/admin/question' },
        { key: 'user', label: '用户管理', path: '/admin/user' },
        { key: 'score', label: '成绩管理', path: '/admin/score' },
        { key: 'paper', label: '试卷管理', path: '/admin/paper' },
        { key: 'settings', label: '系统设置', path: '/admin/settings' },
      ],
    }
  }
  return {
    title: '在线考试系统',
    menuItems: [
      { key: 'home', label: '首页', path: '/user/home' },
      { key: 'exam-center', label: '考试中心', path: '/user/exam-center' },
      { key: 'my-exams', label: '我的考试', path: '/user/my-exams' },
      { key: 'score', label: '成绩查询', path: '/user/score' },
      { key: 'profile', label: '个人中心', path: '/user/profile' },
      { key: 'help', label: '帮助中心', path: '/user/help' },
    ],
  }
})

const activeKey = computed(() => {
  const path = route.path
  const match = path.match(/\/(user|admin)\/([^/]+)/)
  return match ? match[2] : ''
})

function handleNavChange(key) {
  const menuItem = layoutConfig.value.menuItems.find(item => item.key === key)
  if (menuItem) router.push(menuItem.path)
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}
.main-content {
  flex: 1;
  overflow-x: hidden;
  padding-bottom: 32px;
}
</style>
