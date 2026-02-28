<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><List /></el-icon>
      <span>我的考试</span>
    </div>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <span class="card-header"><el-icon><Document /></el-icon> 考试记录</span>
      </template>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="examId" label="考试ID" width="80" />
        <el-table-column label="得分" width="100">
          <template #default="{ row }">
            {{ row.submitTime ? (row.totalScore != null ? row.totalScore : '-') : '未交卷' }}
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">{{ formatTime(row.submitTime) }}</template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag v-if="!row.submitTime" type="warning">进行中</el-tag>
            <el-tag v-else type="success">已交卷</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button v-if="!row.submitTime" type="primary" link @click="goContinue(row)">继续答题</el-button>
            <el-button v-else type="primary" link @click="goScore(row)">查看成绩</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无考试记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { List, Document } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { myRecords } from '@/api/examRecord'

const router = useRouter()
const loading = ref(false)
const list = ref([])

function formatTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 19)
}

function goContinue(row) {
  router.push(`/user/exam/${row.id}`)
}

function goScore(row) {
  router.push(`/user/score?recordId=${row.id}`)
}

onMounted(async () => {
  loading.value = true
  try {
    list.value = await myRecords()
  } catch (_) {}
  finally { loading.value = false }
})
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.content-card { border-radius: 12px; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { color: var(--el-color-primary); }
</style>
