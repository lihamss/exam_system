<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><DataAnalysis /></el-icon>
      <span>控制台</span>
    </div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon exam"><el-icon><Calendar /></el-icon></div>
          <div class="stat-label">今日考试</div>
          <div class="stat-num">{{ stats.todayExam }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon grade"><el-icon><EditPen /></el-icon></div>
          <div class="stat-label">待批改</div>
          <div class="stat-num">{{ stats.pendingGrade }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon paper"><el-icon><Document /></el-icon></div>
          <div class="stat-label">试卷数</div>
          <div class="stat-num">{{ stats.paperCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon question"><el-icon><List /></el-icon></div>
          <div class="stat-label">题目数</div>
          <div class="stat-num">{{ stats.questionCount }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <div class="card-header-row">
          <span class="card-header"><el-icon><Bell /></el-icon> 系统公告</span>
          <el-button type="primary" size="small" @click="showAddNotice"><el-icon><Plus /></el-icon> 发布公告</el-button>
        </div>
      </template>
      <el-table :data="notices" style="margin-top: 12px">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="delNotice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
import { DataAnalysis, Calendar, EditPen, Document, List, Bell, Plus } from '@element-plus/icons-vue'
import { noticePage, saveNotice, deleteNotice } from '@/api/notice'
import { examPage } from '@/api/exam'
import { paperPage } from '@/api/paper'
import { questionPage } from '@/api/question'
import { recordPageByExam } from '@/api/examRecord'
import { ElMessage, ElMessageBox } from 'element-plus'

const stats = reactive({ todayExam: 0, pendingGrade: 0, paperCount: 0, questionCount: 0 })
const notices = ref([])
const noticeDialog = ref(false)
const noticeForm = reactive({ title: '', content: '' })

function showAddNotice() {
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
  const res = await noticePage({ pageNum: 1, pageSize: 10 })
  notices.value = res?.records || []
}

onMounted(async () => {
  const today = new Date().toISOString().slice(0, 10)
  try {
    const [examRes, paperRes, questionRes] = await Promise.all([
      examPage({ pageNum: 1, pageSize: 1 }),
      paperPage({ pageNum: 1, pageSize: 1 }),
      questionPage({ pageNum: 1, pageSize: 1 }),
    ])
    stats.paperCount = paperRes?.total || 0
    stats.questionCount = questionRes?.total || 0
    const examList = (await examPage({ pageNum: 1, pageSize: 100, status: 'published' }))?.records || []
    stats.todayExam = examList.filter(e => e.startTime && e.startTime.startsWith(today)).length
    let pending = 0
    for (const e of examList) {
      const rec = await recordPageByExam(e.id, { pageNum: 1, pageSize: 1 })
      pending += rec?.total || 0
    }
    stats.pendingGrade = pending
  } catch (_) {}
  loadNotices()
})
</script>

<style scoped>
.page { padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.stat-card { border-radius: 12px; text-align: center; padding: 20px 0; }
.stat-icon { width: 48px; height: 48px; margin: 0 auto 12px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; }
.stat-icon.exam { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-icon.grade { background: linear-gradient(135deg, #e6a23c, #f0c78a); }
.stat-icon.paper { background: linear-gradient(135deg, #67c23a, #95d475); }
.stat-icon.question { background: linear-gradient(135deg, #909399, #b1b3b8); }
.stat-label { font-size: 14px; color: #909399; }
.stat-num { font-size: 28px; font-weight: 600; color: #303133; margin-top: 4px; }
.content-card { margin-top: 20px; border-radius: 12px; }
.card-header-row { display: flex; justify-content: space-between; align-items: center; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { color: var(--el-color-primary); }
</style>
