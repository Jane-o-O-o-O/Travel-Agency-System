<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <h3>旅游产品</h3>
        <el-button type="primary">新建产品</el-button>
      </div>
    </template>
    <el-empty description="暂无数据, 功能开发中..." />
  </el-card>
</template>

<script setup lang="ts">
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
}

.card-header h3 {
  margin: 0;
}
</style>
