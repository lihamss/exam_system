<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><Calendar /></el-icon>
      <span>考试管理</span>
    </div>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <div class="card-header-row">
          <span class="card-header"><el-icon><EditPen /></el-icon> 考试列表</span>
        <el-button type="primary" @click="openEdit()"><el-icon><Plus /></el-icon> 新建考试</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="paperId" label="试卷ID" width="90" />
        <el-table-column label="开始时间" width="170">
          <template #default="{ row }">{{ row.startTime?.slice(0, 19) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="170">
          <template #default="{ row }">{{ row.endTime?.slice(0, 19) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="del(row)">删除</el-button>
            <el-button type="primary" link @click="goScore(row)">成绩</el-button>
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
    <el-dialog v-model="dialogVisible" title="考试" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="试卷">
          <el-select v-model="form.paperId" placeholder="选择试卷" filterable style="width: 100%">
            <el-option v-for="p in papers" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="草稿" value="draft" />
            <el-option label="已发布" value="published" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-form-item>
        <el-form-item label="乱序"><el-switch v-model="form.shuffle" /></el-form-item>
        <el-form-item label="显示答案"><el-switch v-model="form.showAnswer" /></el-form-item>
        <el-form-item label="允许补考"><el-switch v-model="form.allowRetake" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { examPage, saveExam, deleteExam } from '@/api/exam'
import { paperPage } from '@/api/paper'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, EditPen, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const papers = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null, name: '', paperId: null, startTime: '', endTime: '',
  status: 'draft', shuffle: true, showAnswer: true, allowRetake: false,
})

async function load() {
  loading.value = true
  try {
    const res = await examPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res?.records || []
    total.value = res?.total || 0
  } catch (_) {}
  finally { loading.value = false }
}

function openEdit(row) {
  if (row) {
    form.id = row.id
    form.name = row.name
    form.paperId = row.paperId
    form.startTime = row.startTime
    form.endTime = row.endTime
    form.status = row.status || 'draft'
    form.shuffle = row.shuffle !== false
    form.showAnswer = row.showAnswer !== false
    form.allowRetake = row.allowRetake === true
  } else {
    form.id = null
    form.name = ''
    form.paperId = null
    form.startTime = ''
    form.endTime = ''
    form.status = 'draft'
    form.shuffle = true
    form.showAnswer = true
    form.allowRetake = false
  }
  dialogVisible.value = true
}

async function save() {
  await saveExam(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该考试？', '提示', { type: 'warning' })
  await deleteExam(row.id)
  ElMessage.success('已删除')
  load()
}

function goScore(row) {
  router.push({ path: '/admin/score', query: { examId: row.id } })
}

onMounted(() => {
  load()
  paperPage({ pageNum: 1, pageSize: 500 }).then(res => { papers.value = res?.records || [] })
})
</script>

<style scoped>
.page { padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.content-card { border-radius: 12px; }
.card-header-row { display: flex; justify-content: space-between; align-items: center; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { color: var(--el-color-primary); }
</style>
