<template>
  <div class="page">
    <el-card>
      <template #header>系统公告管理</template>
      <el-button type="primary" size="small" @click="showAdd">发布公告</el-button>
      <el-table :data="notices" style="margin-top: 12px">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="delNotice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="10"
        :total="noticeTotal"
        layout="total, prev, pager, next"
        @current-change="loadNotices"
      />
    </el-card>
    <el-dialog v-model="noticeDialog" title="发布公告" width="500px">
      <el-form :model="noticeForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="noticeForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="noticeForm.content" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noticeDialog = false">取消</el-button>
        <el-button type="primary" @click="submitNotice">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { noticePage, saveNotice, deleteNotice } from '@/api/notice'
import { ElMessage, ElMessageBox } from 'element-plus'

const notices = ref([])
const noticeTotal = ref(0)
const pageNum = ref(1)
const noticeDialog = ref(false)
const noticeForm = reactive({ title: '', content: '' })

function showAdd() {
  noticeForm.title = ''
  noticeForm.content = ''
  noticeDialog.value = true
}

async function submitNotice() {
  await saveNotice(noticeForm)
  ElMessage.success('发布成功')
  noticeDialog.value = false
  loadNotices()
}

async function delNotice(row) {
  await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
  await deleteNotice(row.id)
  ElMessage.success('已删除')
  loadNotices()
}

async function loadNotices() {
  const res = await noticePage({ pageNum: pageNum.value, pageSize: 10 })
  notices.value = res?.records || []
  noticeTotal.value = res?.total || 0
}

onMounted(loadNotices)
</script>

<style scoped>
.page { padding: 24px; }
</style>
