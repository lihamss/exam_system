<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  min-height: 100%;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 14px;
  color: #303133;
  -webkit-font-smoothing: antialiased;
}

#app {
  width: 100%;
  min-height: 100%;
}

/* 统一页面容器 */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 统一卡片标题样式 */
.card-header-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}
</style>
