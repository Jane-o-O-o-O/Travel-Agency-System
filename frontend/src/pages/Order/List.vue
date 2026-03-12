<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>订单管理</h3>
          <el-button type="primary" @click="handleAdd">新建订单</el-button>
        </div>
      </template>

      <!-- 搜索框 -->
      <div style="margin-bottom: 20px">
        <el-input 
          v-model="searchKey" 
          placeholder="搜索订单号..." 
          @keyup.enter="search"
          clearable
          style="width: 300px; margin-right: 10px"
        />
        <el-select 
          v-model="searchStatus" 
          placeholder="搜索状态..." 
          clearable
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="待处理" value="PENDING" />
          <el-option label="处理中" value="IN_PROGRESS" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
        <el-button @click="search" type="primary">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <el-table :data="orders" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerId" label="客户ID" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="peopleCount" label="参与人数" width="120" />
        <el-table-column prop="startDate" label="出发日期" width="150" />
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
      :title="isEdit ? '编辑订单' : '新增订单'" 
      v-model="dialogVisible"
      @close="closeDialog"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="form.orderNo" />
        </el-form-item>
        <el-form-item label="客户ID" prop="customerId">
          <el-input v-model.number="form.customerId" type="number" />
        </el-form-item>
        <el-form-item label="产品ID" prop="productId">
          <el-input v-model.number="form.productId" type="number" />
        </el-form-item>
        <el-form-item label="参与人数" prop="peopleCount">
          <el-input v-model.number="form.peopleCount" type="number" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model.number="form.amount" type="number" />
        </el-form-item>
        <el-form-item label="出发日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态" prop="payStatus">
          <el-select v-model="form.payStatus" clearable>
            <el-option label="未支付" value="UNPAID" />
            <el-option label="部分" value="PARTIAL" />
            <el-option label="已支付" value="PAID" />
          </el-select>
        </el-form-item>
        <el-form-item label="简述" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="3" />
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type TourOrder } from '@/utils/api/order'
import type { FormInstance } from 'element-plus'

const route = useRoute()
const router = useRouter()
const orders = ref<TourOrder[]>([])
const pageSize = ref(10)
const total = ref(0)
const currentPage = ref(1)
const searchKey = ref('')
const searchStatus = ref('')
const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = ref<Partial<TourOrder>>({
  orderNo: '',
  customerId: 0,
  productId: 0,
  peopleCount: 1,
  amount: 0,
  status: 'DRAFT',
  payStatus: 'UNPAID',
  remark: ''
})

const rules = {
  orderNo: [{ required: true, message: '请输入订单号', trigger: 'blur' }],
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  productId: [{ required: true, message: '请选择产品', trigger: 'blur' }],
  peopleCount: [{ required: true, message: '请输入参与人数', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  status: [],
  payStatus: [],
  remark: []
}

onMounted(() => {
  loadData()
  if (route.query.add === '1') {
    handleAdd()
    router.replace({ path: route.path })
  }
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await orderApi.list(currentPage.value, pageSize.value, searchStatus.value || undefined)
    if (res) {
      orders.value = res.list || []
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
  searchStatus.value = ''
  currentPage.value = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    orderNo: '',
    customerId: 0,
    productId: 0,
    peopleCount: 1,
    amount: 0,
    status: 'DRAFT',
    payStatus: 'UNPAID',
    remark: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: TourOrder) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: TourOrder) => {
  try {
    await ElMessageBox.confirm(
      `确定删除订单 "${row.orderNo}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await orderApi.delete(row.id)
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
      await orderApi.update(form.value.id!, form.value)
      ElMessage.success('更新成功')
    } else {
      await orderApi.create(form.value as any)
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
    orderNo: '',
    customerId: 0,
    productId: 0,
    peopleCount: 1,
    amount: 0,
    status: 'DRAFT',
    payStatus: 'UNPAID',
    remark: ''
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
