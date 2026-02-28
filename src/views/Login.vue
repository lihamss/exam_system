<template>
  <div class="login-page">
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon :closable="false" class="error-alert" />
    <div class="login-card">
      <div class="card-header">
        <el-icon :size="32" color="#3584e4"><User /></el-icon>
        <h1 class="card-title">在线考试系统</h1>
        <p class="card-subtitle">请登录您的账号</p>
      </div>
      <el-form :model="form" class="auth-form" label-position="top">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" size="large" clearable @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="tip">管理员：admin / admin123；<router-link to="/register">学生注册</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { loginApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const errorMessage = ref('')
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  errorMessage.value = ''
  if (!form.username.trim() || !form.password) {
    errorMessage.value = '请输入账号和密码'
    return
  }
  loading.value = true
  try {
    const res = await loginApi(form)
    const role = res?.role || 'student'
    const name = res?.realName || res?.username || form.username
    const token = res?.token || ''
    userStore.login(name, role, token)
    if (role === 'admin' || role === 'teacher') router.push('/admin')
    else router.push('/user')
  } catch (e) {
    errorMessage.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a5fb4 0%, #3584e4 50%, #62a0ea 100%);
  padding: 24px;
}
.error-alert {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  max-width: 400px;
  z-index: 100;
}
.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px 36px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12);
}
.card-header {
  text-align: center;
  margin-bottom: 28px;
}
.card-header .el-icon {
  margin-bottom: 12px;
}
.card-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}
.card-subtitle {
  font-size: 13px;
  color: #909399;
}
.auth-form :deep(.el-form-item) { margin-bottom: 20px; }
.auth-form :deep(.el-form-item:last-of-type) { margin-bottom: 0; }
.submit-btn { width: 100%; height: 44px; font-size: 16px; }
.tip {
  margin-top: 20px;
  font-size: 13px;
  color: #909399;
  text-align: center;
}
.tip a { color: #3584e4; text-decoration: none; }
.tip a:hover { text-decoration: underline; }
</style>
