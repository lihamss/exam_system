<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><Document /></el-icon>
      <span>考试中心</span>
    </div>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <span class="card-header"><el-icon><EditPen /></el-icon> 可参加的考试</span>
      </template>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="name" label="考试名称" />
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" @click="startExam(row)">开始考试</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无可参加的考试" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, EditPen } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { examAvailable } from '@/api/exam'
import { startExam as startExamApi } from '@/api/examRecord'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const list = ref([])

function formatTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 19)
}

async function startExam(exam) {
  try {
    const record = await startExamApi(exam.id)
    router.push(`/user/exam/${record.id}`)
  } catch (e) {
    ElMessage.error(e.message || '无法开始考试')
  }
}

onMounted(async () => {
  loading.value = true
  try {
    list.value = await examAvailable()
    const startId = route.query.start
    if (startId && list.value.find(e => e.id == startId)) {
      await startExam(list.value.find(e => e.id == startId))
    }
  } catch (_) {}
  finally { loading.value = false }
})
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.content-card { border-radius: 12px; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { color: var(--el-color-primary); }
</style>
