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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ticketNo" label="工单编号" width="140" />
        <el-table-column prop="title" label="工单标题" width="160" />
        <el-table-column prop="theme" label="意向主题" width="90" />
        <el-table-column label="来源" width="80">
          <template #default="{ row }">{{ row.portalUserId ? '用户端' : '管理端' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" />
        <el-table-column prop="createdAt" label="创建时间" width="165" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
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

    <!-- 工单详情（含回复） -->
    <el-dialog v-model="detailVisible" title="工单详情" width="640px" class="ticket-detail-dialog">
      <template v-if="detailTicket">
        <dl class="detail-dl">
          <dt>工单编号</dt><dd>{{ detailTicket.ticketNo }}</dd>
          <dt>标题</dt><dd>{{ detailTicket.title }}</dd>
          <dt>意向主题</dt><dd>{{ detailTicket.theme || '-' }}</dd>
          <dt>出行人数/天数</dt><dd>{{ detailTicket.peopleCount ?? '-' }}人 / {{ detailTicket.days ?? '-' }}天</dd>
          <dt>期望日期</dt><dd>{{ detailTicket.expectedDate || '-' }}</dd>
          <dt>联系方式</dt><dd>{{ detailTicket.contactInfo || '-' }}</dd>
          <dt>特殊要求</dt><dd>{{ detailTicket.specialRequirement || '-' }}</dd>
          <dt>状态</dt><dd>{{ detailTicket.status }}</dd>
        </dl>
        <h4 class="reply-title">对话记录</h4>
        <div class="reply-list">
          <div v-for="r in detailReplies" :key="r.id" :class="['reply-row', r.fromType === 'ADMIN' ? 'admin' : 'user']">
            <span class="reply-from">{{ r.fromType === 'ADMIN' ? '客服' : '用户' }}</span>
            <span class="reply-time">{{ r.createdAt }}</span>
            <div class="reply-content">{{ r.content }}</div>
          </div>
        </div>
        <div class="reply-form">
          <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="输入回复内容..." />
          <el-button type="primary" size="small" :loading="replyLoading" @click="sendReply" style="margin-top:8px">发送回复</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      :title="isEdit ? '编辑工单' : '新增工单'" 
      v-model="dialogVisible"
      @close="closeDialog"
      width="600px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="客户ID" prop="customerId">
          <el-input v-model.number="form.customerId" type="number" placeholder="可选，用户端提交的工单可空" />
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
import { ticketApi, type Ticket, type TicketReply } from '@/utils/api/ticket'
import type { FormInstance } from 'element-plus'

const tickets = ref<Ticket[]>([])
const pageSize = ref(10)
const total = ref(0)
const currentPage = ref(1)
const searchKey = ref('')
const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const detailVisible = ref(false)
const detailTicket = ref<Ticket | null>(null)
const detailReplies = ref<TicketReply[]>([])
const replyContent = ref('')
const replyLoading = ref(false)
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
  customerId: [],
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

const openDetail = async (row: Ticket) => {
  detailTicket.value = { ...row }
  replyContent.value = ''
  detailVisible.value = true
  try {
    const res: TicketReply[] = await ticketApi.getReplies(row.id)
    detailReplies.value = res || []
  } catch {
    detailReplies.value = []
  }
}

const sendReply = async () => {
  if (!detailTicket.value || !replyContent.value.trim()) return
  replyLoading.value = true
  try {
    await ticketApi.addReply(detailTicket.value.id, { content: replyContent.value.trim() })
    replyContent.value = ''
    ElMessage.success('回复成功')
    const res: TicketReply[] = await ticketApi.getReplies(detailTicket.value.id)
    detailReplies.value = res || []
  } finally {
    replyLoading.value = false
  }
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
  } catch {
    // 错误信息已由 api 响应拦截器统一展示，此处不再重复提示
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

.ticket-detail-dialog .detail-dl { display: grid; grid-template-columns: 100px 1fr; gap: 8px 16px; margin: 0 0 16px; font-size: 14px; }
.ticket-detail-dialog .detail-dl dt { color: var(--voyage-ink); opacity: 0.8; }
.ticket-detail-dialog .detail-dl dd { margin: 0; }
.reply-title { font-size: 14px; margin: 16px 0 8px; color: var(--voyage-navy); }
.reply-list { max-height: 240px; overflow-y: auto; margin-bottom: 12px; }
.reply-row { padding: 10px 12px; border-radius: 8px; margin-bottom: 8px; border-left: 4px solid var(--voyage-sand); background: var(--voyage-pearl); }
.reply-row.admin { border-left-color: var(--voyage-sunset); }
.reply-row .reply-from { font-weight: 600; margin-right: 12px; font-size: 13px; }
.reply-row .reply-time { font-size: 12px; color: var(--voyage-ink); opacity: 0.7; }
.reply-row .reply-content { margin-top: 6px; font-size: 13px; line-height: 1.5; white-space: pre-wrap; }
.reply-form .el-textarea__inner { border-radius: 8px; }
</style>
