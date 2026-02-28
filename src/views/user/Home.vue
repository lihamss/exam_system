<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><HomeFilled /></el-icon>
      <span>首页</span>
    </div>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <span class="card-header"><el-icon><Bell /></el-icon> 系统公告</span>
          </template>
          <el-empty v-if="!notices.length" description="暂无公告" :image-size="80" />
          <ul v-else class="notice-list">
            <li v-for="n in notices" :key="n.id" class="notice-item">
              <span class="notice-title">{{ n.title }}</span>
              <span class="notice-time">{{ formatTime(n.createTime) }}</span>
            </li>
          </ul>
        </el-card>
        <el-card shadow="hover" class="content-card">
          <template #header>
            <span class="card-header"><el-icon><Document /></el-icon> 可参加考试</span>
          </template>
          <el-empty v-if="!availableExams.length" description="暂无考试" :image-size="80" />
          <el-table v-else :data="availableExams" stripe style="width: 100%">
            <el-table-column prop="name" label="考试名称" min-width="180" />
            <el-table-column label="时间" width="320">
              <template #default="{ row }">
                {{ formatTime(row.startTime) }} ~ {{ formatTime(row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="right">
              <template #default="{ row }">
                <el-button type="primary" @click="goExam(row)">进入考试</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <span class="card-header"><el-icon><Trophy /></el-icon> 最近成绩</span>
          </template>
          <el-empty v-if="!recentRecords.length" description="暂无记录" :image-size="80" />
          <ul v-else class="record-list">
            <li v-for="r in recentRecords" :key="r.id" class="record-item">
              <router-link :to="`/user/score?recordId=${r.id}`">
                <span class="record-score">{{ r.totalScore != null ? r.totalScore : '-' }} 分</span>
                <span class="record-id">记录 #{{ r.id }}</span>
              </router-link>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { HomeFilled, Bell, Document, Trophy } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { noticeLatest } from '@/api/notice'
import { examAvailable } from '@/api/exam'
import { myRecords } from '@/api/examRecord'

const router = useRouter()
const notices = ref([])
const availableExams = ref([])
const recentRecords = ref([])

function formatTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 19)
}

function goExam(exam) {
  router.push({ path: '/user/exam-center', query: { start: exam.id } })
}

onMounted(async () => {
  try {
    const [n, e, r] = await Promise.all([
      noticeLatest(5),
      examAvailable(),
      myRecords(),
    ])
    notices.value = n || []
    availableExams.value = e || []
    recentRecords.value = (r || []).slice(0, 5)
  } catch (_) {}
})
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.content-card { margin-bottom: 20px; border-radius: 12px; }
.content-card:last-child { margin-bottom: 0; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { font-size: 18px; color: var(--el-color-primary); }
.notice-list, .record-list { list-style: none; padding: 0; margin: 0; }
.notice-item, .record-item {
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: background 0.2s;
}
.notice-item:hover, .record-item:hover { background: var(--el-fill-color-light); margin: 0 -16px; padding: 12px 16px; }
.notice-item:last-child, .record-item:last-child { border-bottom: none; }
.notice-title { flex: 1; margin-right: 12px; }
.notice-time { color: #909399; font-size: 12px; flex-shrink: 0; }
.record-list a {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  color: #303133;
  text-decoration: none;
}
.record-list a:hover { color: var(--el-color-primary); }
.record-score { font-weight: 500; color: var(--el-color-primary); }
.record-id { font-size: 12px; color: #909399; }
</style>
