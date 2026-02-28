<template>
  <div class="page">
    <el-card>
      <template #header>
        <span>用户管理</span>
        <el-button type="primary" style="float: right" @click="openEdit()">添加用户</el-button>
      </template>
      <div style="margin-bottom: 12px">
        <el-input v-model="query.keyword" placeholder="用户名/姓名" clearable style="width: 160px; margin-right: 8px" />
        <el-select v-model="query.role" placeholder="角色" clearable style="width: 100px; margin-right: 8px">
          <el-option label="学生" value="student" />
          <el-option label="教师" value="teacher" />
          <el-option label="管理员" value="admin" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="90" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="openResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="load"
      />
    </el-card>
    <el-dialog v-model="dialogVisible" title="用户" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="新建时填写" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="不修改请留空" />
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="pwdDialogVisible" title="重置密码" width="400px">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="新密码"><el-input v-model="pwdForm.password" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userPage, saveUser, deleteUser, resetUserPassword } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const query = reactive({ keyword: '', role: '' })
const dialogVisible = ref(false)
const form = reactive({ id: null, username: '', password: '', realName: '', role: 'student' })
const pwdDialogVisible = ref(false)
const pwdForm = reactive({ userId: null, password: '' })

async function load() {
  loading.value = true
  try {
    const res = await userPage({
      pageNum: pageNum.value, pageSize: pageSize.value,
      keyword: query.keyword || undefined, role: query.role || undefined,
    })
    list.value = res?.records || []
    total.value = res?.total || 0
  } catch (_) {}
  finally { loading.value = false }
}

function openEdit(row) {
  if (row) {
    form.id = row.id
    form.username = row.username
    form.password = ''
    form.realName = row.realName || ''
    form.role = row.role || 'student'
  } else {
    form.id = null
    form.username = ''
    form.password = ''
    form.realName = ''
    form.role = 'student'
  }
  dialogVisible.value = true
}

async function save() {
  await saveUser(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

function openResetPwd(row) {
  pwdForm.userId = row.id
  pwdForm.password = ''
  pwdDialogVisible.value = true
}

async function submitResetPwd() {
  await resetUserPassword(pwdForm.userId, pwdForm.password)
  ElMessage.success('密码已重置')
  pwdDialogVisible.value = false
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.page { padding: 24px; }
</style>
