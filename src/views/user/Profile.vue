<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><User /></el-icon>
      <span>个人中心</span>
    </div>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <span class="card-header"><el-icon><UserFilled /></el-icon> 个人资料</span>
      </template>
      <el-form v-if="user" :model="form" label-width="80px">
        <el-form-item label="用户名">{{ user.username }}</el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveInfo">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <span class="card-header"><el-icon><Lock /></el-icon> 修改密码</span>
      </template>
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="savePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, UserFilled, Lock } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const user = ref(null)
const form = reactive({ realName: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })

async function load() {
  user.value = await getUserInfo()
  if (user.value) form.realName = user.value.realName || ''
}

async function saveInfo() {
  await updateUserInfo({ realName: form.realName })
  ElMessage.success('保存成功')
  load()
}

async function savePassword() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning('请填写原密码和新密码')
    return
  }
  await updatePassword(pwdForm.oldPassword, pwdForm.newPassword)
  ElMessage.success('密码已修改')
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
}

onMounted(load)
</script>

<style scoped>
.page { max-width: 560px; margin: 0 auto; padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.content-card { margin-bottom: 20px; border-radius: 12px; }
.content-card:last-child { margin-bottom: 0; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { color: var(--el-color-primary); }
</style>
