<template>
  <div class="page">
    <div class="page-title">
      <el-icon :size="22"><QuestionFilled /></el-icon>
      <span>帮助中心</span>
    </div>
    <el-card shadow="hover" class="content-card">
      <template #header>
        <span class="card-header"><el-icon><InfoFilled /></el-icon> 帮助与支持</span>
      </template>
      <div class="help-section">
        <h4><el-icon><Document /></el-icon> 使用说明</h4>
        <p>1. 在「考试中心」查看可参加的考试，点击「开始考试」进入答题。</p>
        <p>2. 答题过程中请留意倒计时，超时将自动交卷。</p>
        <p>3. 交卷后客观题自动评分，主观题由教师批改后可查看成绩。</p>
      </div>
      <el-divider />
      <div class="help-section">
        <h4><el-icon><ChatDotRound /></el-icon> 常见问题</h4>
        <p><strong>Q：忘记密码？</strong> A：请联系管理员重置。</p>
        <p><strong>Q：考试中途断网？</strong> A：重新登录后可在「我的考试」中继续作答（未交卷前）。</p>
      </div>
      <el-divider />
      <div class="help-section">
        <h4><el-icon><Phone /></el-icon> 联系方式</h4>
        <p>系统管理员：请通过学校教务系统联系。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { QuestionFilled, InfoFilled, Document, ChatDotRound, Phone } from '@element-plus/icons-vue'
</script>

<style scoped>
.page { max-width: 720px; margin: 0 auto; padding: 24px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #303133; }
.page-title .el-icon { color: var(--el-color-primary); }
.content-card { border-radius: 12px; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 500; }
.card-header .el-icon { color: var(--el-color-primary); }
.help-section h4 { display: flex; align-items: center; gap: 6px; margin-bottom: 12px; font-size: 15px; color: #303133; }
.help-section h4 .el-icon { color: var(--el-color-primary); }
.help-section p { color: #606266; line-height: 1.8; margin: 6px 0; }
</style>
