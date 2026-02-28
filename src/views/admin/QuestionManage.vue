<template>
  <div class="page">
    <el-card>
      <template #header>
        <span>题库管理</span>
        <el-button type="primary" style="float: right" @click="openEdit()">添加题目</el-button>
      </template>
      <div style="margin-bottom: 12px">
        <el-select v-model="query.type" placeholder="题型" clearable style="width: 120px; margin-right: 8px">
          <el-option label="单选" value="single" />
          <el-option label="多选" value="multiple" />
          <el-option label="判断" value="judge" />
          <el-option label="填空" value="fill" />
          <el-option label="简答" value="short" />
        </el-select>
        <el-input v-model="query.subject" placeholder="科目" clearable style="width: 120px; margin-right: 8px" />
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="type" label="题型" width="80" />
        <el-table-column prop="content" label="题目" show-overflow-tooltip />
        <el-table-column prop="score" label="分值" width="70" />
        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column prop="subject" label="科目" width="90" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
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
    <el-dialog v-model="dialogVisible" title="题目" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="题型">
          <el-select v-model="form.type">
            <el-option label="单选" value="single" />
            <el-option label="多选" value="multiple" />
            <el-option label="判断" value="judge" />
            <el-option label="填空" value="fill" />
            <el-option label="简答" value="short" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容"><el-input v-model="form.content" type="textarea" :rows="2" /></el-form-item>
        <el-form-item v-if="['single','multiple'].includes(form.type)" label="选项A"><el-input v-model="form.optionA" /></el-form-item>
        <el-form-item v-if="['single','multiple'].includes(form.type)" label="选项B"><el-input v-model="form.optionB" /></el-form-item>
        <el-form-item v-if="['single','multiple'].includes(form.type)" label="选项C"><el-input v-model="form.optionC" /></el-form-item>
        <el-form-item v-if="['single','multiple'].includes(form.type)" label="选项D"><el-input v-model="form.optionD" /></el-form-item>
        <el-form-item label="正确答案">
          <el-input v-model="form.correctAnswer" placeholder="单选填A；多选填A,B；判断填T/F" />
        </el-form-item>
        <el-form-item label="分值"><el-input-number v-model="form.score" :min="1" /></el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目"><el-input v-model="form.subject" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { questionPage, saveQuestion, deleteQuestion } from '@/api/question'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const query = reactive({ type: '', subject: '', difficulty: '' })
const dialogVisible = ref(false)
const form = reactive({
  id: null, type: 'single', content: '', optionA: '', optionB: '', optionC: '', optionD: '',
  correctAnswer: '', score: 5, difficulty: 'medium', subject: '',
})

async function load() {
  loading.value = true
  try {
    const res = await questionPage({
      pageNum: pageNum.value, pageSize: pageSize.value,
      type: query.type || undefined, subject: query.subject || undefined, difficulty: query.difficulty || undefined,
    })
    list.value = res?.records || []
    total.value = res?.total || 0
  } catch (_) {}
  finally { loading.value = false }
}

function openEdit(row) {
  if (row) {
    Object.assign(form, { ...row })
  } else {
    form.id = null
    form.type = 'single'
    form.content = ''
    form.optionA = form.optionB = form.optionC = form.optionD = ''
    form.correctAnswer = ''
    form.score = 5
    form.difficulty = 'medium'
    form.subject = ''
  }
  dialogVisible.value = true
}

async function save() {
  await saveQuestion(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该题目？', '提示', { type: 'warning' })
  await deleteQuestion(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.page { padding: 24px; }
</style>
