<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>资源管理</h3>
          <el-button type="primary" @click="handleAdd">新建资源</el-button>
        </div>
      </template>

      <!-- 搜索框 -->
      <div style="margin-bottom: 20px">
        <el-input 
          v-model="searchKey" 
          placeholder="搜索资源名或编码..." 
          @keyup.enter="search"
          clearable
          style="width: 300px; margin-right: 10px"
        />
        <el-button @click="search" type="primary">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <el-table :data="resources" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="资源名称" width="180" />
        <el-table-column prop="code" label="资源编码" width="150" />
        <el-table-column prop="type" label="资源类型" width="120" />
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
      :title="isEdit ? '编辑资源' : '新增资源'" 
      v-model="dialogVisible"
      @close="closeDialog"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="资源名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="资源编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="资源类型" prop="type">
          <el-select v-model="form.type" clearable>
            <el-option label="导游" value="GUIDE" />
            <el-option label="车辆" value="VEHICLE" />
            <el-option label="酒店" value="HOTEL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
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
import { resourceApi, type Resource } from '@/utils/api/resource'
import type { FormInstance } from 'element-plus'

const resources = ref<Resource[]>([])
const pageSize = ref(10)
const total = ref(0)
const currentPage = ref(1)
const searchKey = ref('')
const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = ref<Partial<Resource>>({
  name: '',
  code: '',
  type: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入资源编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择资源类型', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await resourceApi.list(currentPage.value, pageSize.value, searchKey.value || undefined)
    if (res) {
      resources.value = res.list || []
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
    code: '',
    type: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: Resource) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Resource) => {
  try {
    await ElMessageBox.confirm(
      `确定删除资源 "${row.name}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await resourceApi.delete(row.id)
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
      await resourceApi.update(form.value.id!, form.value)
      ElMessage.success('更新成功')
    } else {
      await resourceApi.create(form.value as any)
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
    code: '',
    type: '',
    description: ''
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
