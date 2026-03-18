<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>工单管理</h3>
          <el-button type="primary" @click="handleAdd">新建工单</el-button>
        </div>
      </template>

      <!-- 搜索框 -->
      <div style="margin-bottom: 20px">
        <el-input 
          v-model="searchKey" 
          placeholder="搜索工单标题..." 
          @keyup.enter="search"
          clearable
          style="width: 300px; margin-right: 10px"
        />
        <el-button @click="search" type="primary">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <el-table :data="tickets" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="ticketNo" label="工单编号" width="150" />
        <el-table-column prop="title" label="工单标题" width="200" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="priority" label="优先级" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      :title="isEdit ? '编辑工单' : '新增工单'" 
      v-model="dialogVisible"
      @close="closeDialog"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="客户ID" prop="customerId">
          <el-input v-model.number="form.customerId" type="number" />
        </el-form-item>
        <el-form-item label="关联订单" prop="orderId">
          <el-input v-model.number="form.orderId" type="number" placeholder="可选" />
        </el-form-item>
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="问题类型" prop="problemType">
          <el-select v-model="form.problemType" clearable>
            <el-option label="投诉" value="COMPLAINT" />
            <el-option label="修改" value="MODIFY" />
            <el-option label="退款" value="REFUND" />
            <el-option label="咨询" value="CONSULT" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" clearable>
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MID" />
            <el-option label="高" value="HIGH" />
            <el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" clearable>
            <el-option label="待处理" value="OPEN" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">{{ isEdit ? '更新' : '新增' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ticketApi, type Ticket } from '@/utils/api/ticket'
import type { FormInstance } from 'element-plus'

const tickets = ref<Ticket[]>([])
const pageSize = ref(10)
const total = ref(0)
const currentPage = ref(1)
const searchKey = ref('')
const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = ref<Partial<Ticket>>({
  customerId: 0,
  orderId: undefined,
  ticketNo: '',
  title: '',
  status: 'OPEN',
  priority: 'LOW',
  description: '',
  problemType: 'CONSULT'
})

const rules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  priority: [],
  status: [],
  problemType: [],
  description: []
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await ticketApi.list(currentPage.value, pageSize.value, searchKey.value || undefined)
    if (res) {
      tickets.value = res.list || []
      total.value = res.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadData()
}

const search = () => {
  currentPage.value = 1
  loadData()
}

const resetSearch = () => {
  searchKey.value = ''
  currentPage.value = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    customerId: 0,
    orderId: undefined,
    ticketNo: '',
    title: '',
    status: 'OPEN',
    priority: 'LOW',
    description: '',
    problemType: 'CONSULT'
  }
  dialogVisible.value = true
}

const handleEdit = (row: Ticket) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Ticket) => {
  try {
    await ElMessageBox.confirm(
      `确定删除工单 "${row.title}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await ticketApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户点击取消
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await ticketApi.update(form.value.id!, form.value)
      ElMessage.success('更新成功')
    } else {
      await ticketApi.create(form.value as any)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const closeDialog = () => {
  form.value = {
    ticketNo: '',
    title: '',
    status: 'OPEN',
    priority: 'LOW',
    description: '',
    problemType: 'CONSULT'
  }
}
</script>

<style scoped>
.page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}
</style>
