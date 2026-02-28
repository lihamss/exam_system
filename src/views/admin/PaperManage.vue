<template>
  <div class="page">
    <el-card>
      <template #header>
        <span>试卷管理</span>
        <el-button type="primary" style="float: right" @click="openEdit()">新建试卷</el-button>
      </template>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="duration" label="时长(分钟)" width="110" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="preview(row)">预览</el-button>
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
    <el-dialog v-model="dialogVisible" title="试卷" width="720px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="试卷名称"><el-input v-model="form.name" placeholder="请输入试卷名称" /></el-form-item>
        <el-form-item label="考试时长(分钟)"><el-input-number v-model="form.duration" :min="1" /></el-form-item>
        <el-form-item label="选题与分值">
          <div class="question-list">
            <div v-for="(item, index) in form.items" :key="index" class="question-row">
              <el-select
                v-model="item.questionId"
                filterable
                placeholder="选择题目"
                class="question-select"
                @change="onQuestionChange(index)"
              >
                <el-option
                  v-for="q in availableQuestions(index)"
                  :key="q.id"
                  :label="`#${q.id} ${(q.content || '').slice(0, 40)}${(q.content || '').length > 40 ? '...' : ''}`"
                  :value="q.id"
                />
              </el-select>
              <el-input-number v-model="item.score" :min="1" :max="100" size="default" class="score-input" placeholder="分值" />
              <span class="score-unit">分</span>
              <el-button type="danger" link :icon="Delete" @click="removeQuestion(index)" />
            </div>
            <el-button type="primary" plain :icon="Plus" @click="addQuestion">添加题目</el-button>
          </div>
          <div v-if="form.items.length" class="total-score">总分：{{ totalScore }}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="previewVisible" title="试卷预览" width="720px">
      <div v-if="previewQuestions.length" class="preview-content">
        <div class="preview-total">总分：{{ previewTotal }} 分</div>
        <div v-for="(q, i) in previewQuestions" :key="q.id" class="preview-item">
          <div class="preview-title">
            <span class="preview-num">{{ i + 1 }}</span>
            <span class="preview-text">{{ q.content }}</span>
            <span class="preview-score">{{ q.score }} 分</span>
          </div>
          <div v-if="q.optionA" class="preview-options">
            A. {{ q.optionA }} &nbsp; B. {{ q.optionB }} &nbsp; C. {{ q.optionC }} &nbsp; D. {{ q.optionD }}
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无题目" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { paperPage, getPaperQuestions, savePaper, deletePaper } from '@/api/paper'
import { questionPage } from '@/api/question'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const allQuestions = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null, name: '', duration: 60, items: [], // items: [{ questionId, score }]
})
const previewVisible = ref(false)
const previewQuestions = ref([])

const totalScore = computed(() => form.items.reduce((sum, it) => sum + (it.score || 0), 0))
const previewTotal = computed(() => previewQuestions.value.reduce((sum, q) => sum + (q.score || 0), 0))

function availableQuestions(excludeIndex) {
  const selected = form.items
    .filter((it, i) => i !== excludeIndex && it.questionId)
    .map(it => it.questionId)
  return allQuestions.value.filter(q => !selected.includes(q.id))
}

function addQuestion() {
  form.items.push({ questionId: null, score: 5 })
}

function removeQuestion(index) {
  form.items.splice(index, 1)
}

function onQuestionChange(index) {
  const q = allQuestions.value.find(x => x.id === form.items[index].questionId)
  if (q && q.score && !form.items[index].score) form.items[index].score = q.score
}

async function load() {
  loading.value = true
  try {
    const res = await paperPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res?.records || []
    total.value = res?.total || 0
  } catch (_) {}
  finally { loading.value = false }
}

function openEdit(row) {
  if (row) {
    form.id = row.id
    form.name = row.name
    form.duration = row.duration || 60
    getPaperQuestions(row.id).then(questions => {
      form.items = (questions || []).map(q => ({ questionId: q.id, score: q.score || 5 }))
    })
  } else {
    form.id = null
    form.name = ''
    form.duration = 60
    form.items = [{ questionId: null, score: 5 }]
  }
  dialogVisible.value = true
}

async function save() {
  const validItems = form.items.filter(it => it.questionId)
  if (!validItems.length) {
    ElMessage.warning('请至少添加一道题目')
    return
  }
  await savePaper({
    id: form.id,
    name: form.name,
    duration: form.duration,
    questionIds: validItems.map(it => it.questionId),
    scores: validItems.map(it => it.score || 5),
  })
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function preview(row) {
  previewQuestions.value = await getPaperQuestions(row.id)
  previewVisible.value = true
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该试卷？', '提示', { type: 'warning' })
  await deletePaper(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(async () => {
  load()
  const res = await questionPage({ pageNum: 1, pageSize: 1000 })
  allQuestions.value = res?.records || []
})
</script>

<style scoped>
.page { padding: 24px; }
.question-list { width: 100%; }
.question-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.question-select { flex: 1; min-width: 0; }
.score-input { width: 100px; }
.score-unit { color: #909399; font-size: 14px; }
.total-score { margin-top: 12px; font-weight: 500; color: var(--el-color-primary); }
.preview-content { max-height: 60vh; overflow-y: auto; }
.preview-total { margin-bottom: 16px; font-size: 16px; font-weight: 600; color: var(--el-color-primary); }
.preview-item {
  margin-bottom: 20px;
  padding: 16px;
  background: var(--el-fill-color-light);
  border-radius: 10px;
}
.preview-title { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 8px; }
.preview-num {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: var(--el-color-primary);
  color: #fff;
  border-radius: 6px;
  font-size: 13px;
}
.preview-text { flex: 1; font-weight: 500; }
.preview-score { flex-shrink: 0; color: var(--el-color-primary); font-weight: 600; }
.preview-options { padding-left: 32px; font-size: 13px; color: #606266; }
</style>
