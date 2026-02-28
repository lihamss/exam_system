<template>
  <header class="header">
    <div class="header-content">
      <div class="logo-wrap">
        <el-icon :size="24"><Reading /></el-icon>
        <span class="logo">{{ title }}</span>
      </div>
      <nav class="nav-menu">
        <button
          v-for="item in menuItems"
          :key="item.key"
          :class="['nav-btn', { active: activeKey === item.key }]"
          @click="handleNavClick(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>
      <div class="user-info">
        <el-avatar :size="32" class="user-avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
        <span class="username">{{ username }}</span>
        <el-button type="primary" link class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出</span>
        </el-button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { Reading, User, SwitchButton } from '@element-plus/icons-vue'

const props = defineProps({
  title: { type: String, default: '项目名称' },
  menuItems: { type: Array, required: true },
  activeKey: { type: String, required: true },
})
const emit = defineEmits(['nav-change', 'logout'])
const userStore = useUserStore()
const username = computed(() => userStore.name || '用户')

function handleNavClick(key) {
  if (key !== props.activeKey) emit('nav-change', key)
}
function handleLogout() {
  emit('logout')
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #1a5fb4 0%, #3584e4 100%);
  color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.header-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}
.logo-wrap .el-icon { opacity: 0.95; }
.logo {
  font-size: 1.2rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}
.nav-menu { display: flex; gap: 4px; }
.nav-btn {
  padding: 8px 16px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
}
.nav-btn:hover { background: rgba(255, 255, 255, 0.15); color: #fff; }
.nav-btn.active { background: rgba(255, 255, 255, 0.25); color: #fff; }
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-avatar { background: rgba(255, 255, 255, 0.3); }
.username { font-size: 14px; color: rgba(255, 255, 255, 0.95); }
.logout-btn { color: rgba(255, 255, 255, 0.9); padding: 0 8px; }
.logout-btn:hover { color: #fff; }
</style>
