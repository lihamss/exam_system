<template>
  <div class="page">
    <el-card v-loading="loading" shadow="hover" class="exam-card">
      <div class="exam-header">
        <div class="exam-title">
          <el-icon :size="20"><EditPen /></el-icon>
          <span>{{ exam?.name || '考试答题' }}</span>
        </div>
        <el-tag type="danger" size="large" effect="dark" class="countdown">
          <el-icon><Timer /></el-icon>
          剩余 {{ remainText }}
        </el-tag>
      </div>
      <template v-if="record && questions.length">
        <el-form ref="formRef">
          <div v-for="(q, index) in questions" :key="q.id" class="question-block">
            <div class="q-title">
              <span class="q-num">{{ index + 1 }}</span>
              {{ q.content }}
            </div>
            <div v-if="q.type === 'single'" class="options">
              <el-radio-group v-model="answers[q.id]">
                <el-radio label="A">{{ q.optionA }}</el-radio>
                <el-radio label="B">{{ q.optionB }}</el-radio>
                <el-radio label="C">{{ q.optionC }}</el-radio>
                <el-radio label="D">{{ q.optionD }}</el-radio>
              </el-radio-group>
            </div>
            <div v-else-if="q.type === 'multiple'" class="options">
              <el-checkbox-group v-model="multiAnswers[q.id]">
                <el-checkbox label="A">{{ q.optionA }}</el-checkbox>
                <el-checkbox label="B">{{ q.optionB }}</el-checkbox>
                <el-checkbox label="C">{{ q.optionC }}</el-checkbox>
                <el-checkbox label="D">{{ q.optionD }}</el-checkbox>
              </el-checkbox-group>
            </div>
            <div v-else-if="q.type === 'judge'" class="options">
              <el-radio-group v-model="answers[q.id]">
                <el-radio label="T">正确</el-radio>
                <el-radio label="F">错误</el-radio>
              </el-radio-group>
            </div>
            <div v-else class="options">
              <el-input v-model="answers[q.id]" type="textarea" :rows="2" placeholder="请输入答案" />
            </div>
          </div>
        </el-form>
        <div class="submit-bar">
          <el-button type="primary" size="large" :loading="submitting" @click="submit">
            <el-icon><Check /></el-icon>
            提交试卷
          </el-button>
        </div>
      </template>
      <el-empty v-else-if="!loading" description="加载失败或已交卷" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRecordById, getRecordAnswers, submitExam } from '@/api/examRecord'
import { getExamById } from '@/api/exam'
import { getPaperQuestions } from '@/api/paper'
import { ElMessage, ElMessageBox } from 'element-plus'
import { EditPen, Timer, Check } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const recordId = Number(route.params.recordId)
const loading = ref(true)
const submitting = ref(false)
const record = ref(null)
const exam = ref(null)
const questions = ref([])
const answers = reactive({})
const multiAnswers = reactive({})
let timer = null
const endAt = ref(null)

const remainText = computed(() => {
  if (!endAt.value) return '--:--:--'
  const now = Date.now()
  const remain = Math.max(0, endAt.value - now)
  const h = Math.floor(remain / 3600000)
  const m = Math.floor((remain % 3600000) / 60000)
  const s = Math.floor((remain % 60000) / 1000)
  return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
})

function buildSubmitBody() {
  const body = {}
  for (const [id, v] of Object.entries(answers)) {
    if (v != null && String(v).trim() !== '') body[id] = String(v).trim()
  }
  for (const [id, arr] of Object.entries(multiAnswers)) {
    if (Array.isArray(arr) && arr.length) body[id] = arr.sort().join(',')
  }
  return body
}

async function submit() {
  try {
    await ElMessageBox.confirm('确定要提交试卷吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
  } catch {
    return
  }
  submitting.value = true
  try {
    await submitExam(recordId, buildSubmitBody())
    ElMessage.success('提交成功')
    router.push('/user/my-exams')
  } catch (e) {
    ElMessage.error(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    record.value = await getRecordById(recordId)
    if (!record.value || record.value.submitTime) {
      router.replace('/user/my-exams')
      return
    }
    exam.value = await getExamById(record.value.examId)
    const paperId = exam.value?.paperId
    if (paperId) questions.value = await getPaperQuestions(paperId)
    for (const q of questions.value) {
      if (q.type === 'multiple') multiAnswers[q.id] = []
    }
    const ansList = await getRecordAnswers(recordId)
    for (const a of ansList || []) {
      if (!a.userAnswer) continue
      const q = questions.value.find(x => x.id === a.questionId)
      if (q && q.type === 'multiple') {
        multiAnswers[a.questionId] = a.userAnswer.split(',').map(s => s.trim()).filter(Boolean)
      } else {
        answers[a.questionId] = a.userAnswer
      }
    }
    const durationMin = exam.value?.duration || 60
    const start = new Date(record.value.startTime).getTime()
    endAt.value = start + durationMin * 60 * 1000
    timer = setInterval(() => {
      if (endAt.value && Date.now() >= endAt.value) {
        clearInterval(timer)
        submit()
      }
    }, 1000)
  } catch (_) {
    router.replace('/user/my-exams')
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.page { max-width: 840px; margin: 0 auto; padding: 24px; }
.exam-card { border-radius: 12px; }
.exam-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid var(--el-border-color-lighter); }
.exam-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; }
.exam-title .el-icon { color: var(--el-color-primary); }
.countdown { display: flex; align-items: center; gap: 6px; }
.question-block { margin-bottom: 28px; padding: 20px; background: var(--el-fill-color-light); border-radius: 10px; }
.question-block:last-of-type { margin-bottom: 24px; }
.q-title { margin-bottom: 14px; font-weight: 500; line-height: 1.6; }
.q-num { display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; margin-right: 8px; background: var(--el-color-primary); color: #fff; border-radius: 6px; font-size: 13px; }
.options { padding-left: 32px; }
.submit-bar { margin-top: 24px; text-align: center; }
.submit-bar .el-button { padding: 12px 32px; }
</style>
