<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>客户管理</h3>
          <el-button type="primary" @click="handleAdd">新增客户</el-button>
        </div>
      </template>

      <el-table :data="customers" stripe>
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="客户名" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="budgetLevel" label="预算档" width="100" />
        <el-table-column label="操作" width="200">
          <template #default>
            <el-button size="small" type="primary">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :page-size="pageSize"
        :pager-count="5"
        :total="total"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const customers = ref([])
const pageSize = ref(10)
const total = ref(0)

const handleAdd = () => {
  ElMessage.info('新增功能开发中...')
}

const handlePageChange = (page: number) => {
  ElMessage.info(`跳转到第 ${page} 页`)
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.page {
  padding: 0;
}
</style>
