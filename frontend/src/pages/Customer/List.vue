<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>客户管理</h3>
          <el-button type="primary" @click="handleAdd">新增客户</el-button>
        </div>
      </template>

      <!-- 搜索框 -->
      <div style="margin-bottom: 20px">
        <el-input 
          v-model="searchKey" 
          placeholder="搜索客户名或手机号..." 
          @keyup.enter="search"
          clearable
          style="width: 300px; margin-right: 10px"
        />
        <el-button @click="search" type="primary">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <el-table :data="customers" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="客户名" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="budgetLevel" label="预算档" width="100" />
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
      :title="isEdit ? '编辑客户' : '新增客户'" 
      v-model="dialogVisible"
      @close="closeDialog"
      width="500px"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="客户名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-select v-model="form.source" clearable>
            <el-option label="抖音" value="抖音" />
            <el-option label="小红书" value="小红书" />
            <el-option label="老客推荐" value="老客推荐" />
            <el-option label="线下" value="线下" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算档" prop="budgetLevel">
          <el-select v-model="form.budgetLevel" clearable>
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MID" />
            <el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="note">
          <el-input v-model="form.note" type="textarea" rows="3" />
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
import { customerApi, type Customer } from '@/utils/api/customer'
import type { FormInstance } from 'element-plus'

const customers = ref<Customer[]>([])
const pageSize = ref(10)
const total = ref(0)
const currentPage = ref(1)
const searchKey = ref('')
const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = ref<Partial<Customer>>({
  name: '',
  phone: '',
  email: '',
  source: '',
  budgetLevel: '',
  note: ''
})

const rules = {
  name: [{ required: true, message: '请输入客户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  source: [],
  budgetLevel: [],
  note: []
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await customerApi.list(currentPage.value, pageSize.value, searchKey.value || undefined)
    if (res) {
      customers.value = res.list || []
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
    name: '',
    phone: '',
    email: '',
    source: '',
    budgetLevel: '',
    note: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: Customer) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Customer) => {
  try {
    await ElMessageBox.confirm(
      `确定删除客户 "${row.name}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await customerApi.delete(row.id)
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
      await customerApi.update(form.value.id!, form.value)
      ElMessage.success('更新成功')
    } else {
      await customerApi.create(form.value as any)
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
    name: '',
    phone: '',
    email: '',
    source: '',
    budgetLevel: '',
    note: ''
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
