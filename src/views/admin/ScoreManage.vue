<template>
  <div class="page">
    <el-card>
      <template #header>成绩管理</template>
      <div style="margin-bottom: 12px">
        <el-select v-model="examId" placeholder="选择考试" clearable filterable style="width: 260px; margin-right: 8px" @change="loadRecords">
          <el-option v-for="e in exams" :key="e.id" :label="e.name" :value="e.id" />
        </el-select>
        <el-button type="primary" @click="loadRecords">查询</el-button>
      </div>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="记录ID" width="90" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column label="得分" width="90">
          <template #default="{ row }">{{ row.totalScore != null ? row.totalScore : '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">{{ row.submitTime?.slice(0, 19) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" link @click="openGrade(row)">批改/详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadRecords"
      />
    </el-card>
    <el-dialog v-model="gradeDialogVisible" title="答题详情 / 批改" width="700px">
      <div v-if="currentRecord">
        <p>记录ID: {{ currentRecord.id }}，总分: {{ currentRecord.totalScore != null ? currentRecord.totalScore : '-' }}</p>
        <el-table :data="answerList" border size="small">
          <el-table-column prop="questionId" label="题号" width="70" />
          <el-table-column label="用户答案" width="140">
            <template #default="{ row }">{{ row.userAnswer || '-' }}</template>
          </el-table-column>
          <el-table-column label="得分" width="80">
            <template #default="{ row }">
              <el-input-number v-if="isSubjective(row.questionId)" v-model="scoreInputs[row.questionId]" :min="0" size="small" />
              <span v-else>{{ row.score != null ? row.score : '-' }}</span>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="Object.keys(scoreInputs || {}).length" style="margin-top: 12px">
          <el-button type="primary" @click="submitGrade">保存批改</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { examPage } from '@/api/exam'
import { recordPageByExam, getRecordAnswers, gradeRecord } from '@/api/examRecord'
import { getPaperQuestions } from '@/api/paper'
import { ElMessage } from 'element-plus'

const route = useRoute()
const examId = ref(null)
const exams = ref([])
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const gradeDialogVisible = ref(false)
const currentRecord = ref(null)
const answerList = ref([])
const scoreInputs = ref({})
const subjectiveQuestionIds = ref(new Set())

function isSubjective(qid) {
  return subjectiveQuestionIds.value.has(qid)
}

async function loadRecords() {
  if (!examId.value) { list.value = []; total.value = 0; return }
  loading.value = true
  try {
    const res = await recordPageByExam(examId.value, { pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res?.records || []
    total.value = res?.total || 0
  } catch (_) {}
  finally { loading.value = false }
}

async function openGrade(row) {
  currentRecord.value = row
  answerList.value = await getRecordAnswers(row.id) || []
  const exam = exams.value.find(e => e.id === row.examId)
  let paperId = exam?.paperId
  if (paperId) {
    const questions = await getPaperQuestions(paperId)
    subjectiveQuestionIds.value = new Set(
      questions.filter(q => q.type === 'fill' || q.type === 'short').map(q => q.id)
    )
  } else subjectiveQuestionIds.value = new Set()
  scoreInputs.value = {}
  for (const a of answerList.value) {
    if (isSubjective(a.questionId)) scoreInputs.value[a.questionId] = a.score != null ? a.score : 0
  }
  gradeDialogVisible.value = true
}

async function submitGrade() {
  const scores = {}
  const inputs = scoreInputs.value || {}
  for (const qid of subjectiveQuestionIds.value) {
    if (inputs[qid] != null) scores[qid] = inputs[qid]
  }
  await gradeRecord(currentRecord.value.id, scores)
  ElMessage.success('批改已保存')
  gradeDialogVisible.value = false
  loadRecords()
}

onMounted(async () => {
  const res = await examPage({ pageNum: 1, pageSize: 500 })
  exams.value = res?.records || []
  const q = route.query.examId
  if (q) examId.value = Number(q)
  if (examId.value) loadRecords()
})
</script>

<style scoped>
.page { padding: 24px; }
</style>
