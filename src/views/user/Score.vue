<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><Trophy /></el-icon>
      <span>成绩查询</span>
    </div>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <span class="card-header"><el-icon><List /></el-icon> 成绩记录</span>
      </template>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="examId" label="考试ID" width="80" />
        <el-table-column label="得分" width="100">
          <template #default="{ row }">{{ row.totalScore != null ? row.totalScore : '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">{{ formatTime(row.submitTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无成绩" />
    </el-card>
    <el-dialog v-model="detailVisible" title="成绩详情" width="700px">
      <div v-if="detailRecord">
        <p>总分：{{ detailRecord.totalScore != null ? detailRecord.totalScore : '-' }}</p>
        <el-table :data="detailAnswers" border size="small">
          <el-table-column prop="questionId" label="题号" width="80" />
          <el-table-column label="你的答案" width="120">
            <template #default="{ row }">{{ row.userAnswer || '-' }}</template>
          </el-table-column>
          <el-table-column label="得分" width="80">
            <template #default="{ row }">{{ row.score != null ? row.score : '-' }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Trophy, List } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { myRecords, getRecordById, getRecordAnswers } from '@/api/examRecord'

const route = useRoute()
const loading = ref(false)
const list = ref([])
const detailVisible = ref(false)
const detailRecord = ref(null)
const detailAnswers = ref([])

function formatTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 19)
}

async function showDetail(row) {
  detailRecord.value = await getRecordById(row.id)
  detailAnswers.value = await getRecordAnswers(row.id) || []
  detailVisible.value = true
}

onMounted(async () => {
  loading.value = true
  try {
    list.value = await myRecords()
    const recordId = route.query.recordId
    if (recordId) {
      const r = list.value.find(x => x.id == recordId)
      if (r) showDetail(r)
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
