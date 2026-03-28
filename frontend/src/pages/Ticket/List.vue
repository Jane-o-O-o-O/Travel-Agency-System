<template>
  <div class="ticket-page">
    <section class="inbox-panel">
      <div class="panel-top">
        <div>
          <p class="panel-kicker">Service Desk</p>
          <h2>客服工单会话台</h2>
        </div>
        <el-button type="primary" @click="handleAdd">新建工单</el-button>
      </div>

      <div class="panel-search">
        <el-input
          v-model="searchKey"
          placeholder="搜索工单标题、主题或咨询关键词"
          clearable
          @keyup.enter="search"
        />
        <el-button @click="search">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <div class="ticket-list" v-loading="listLoading">
        <button
          v-for="ticket in tickets"
          :key="ticket.id"
          type="button"
          class="ticket-card"
          :class="{ active: currentTicket?.id === ticket.id }"
          @click="openTicket(ticket.id)"
        >
          <div class="ticket-card-top">
            <span class="status-badge" :class="'status-' + ticket.status">{{ statusText(ticket.status) }}</span>
            <span class="ticket-time">{{ formatTime(ticket.updatedAt || ticket.createdAt) }}</span>
          </div>
          <strong>{{ ticket.title }}</strong>
          <p>{{ ticket.specialRequirement || ticket.description || ticket.theme || '点击查看对话详情' }}</p>
          <div class="ticket-card-meta">
            <span>{{ priorityText(ticket.priority) }}</span>
            <span>{{ problemTypeText(ticket.problemType) }}</span>
            <span>{{ ticket.ticketNo }}</span>
          </div>
        </button>

        <el-empty v-if="!tickets.length && !listLoading" description="暂无工单，门户咨询会自动进入这里" />
      </div>

      <el-pagination
        v-if="total > pageSize"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </section>

    <section class="conversation-panel">
      <template v-if="currentTicket">
        <div class="conversation-shell" v-loading="detailLoading">
          <div class="conversation-header">
            <div>
              <p class="panel-kicker">{{ currentTicket.ticketNo }}</p>
              <h2>{{ currentTicket.title }}</h2>
            </div>
            <div class="conversation-actions">
              <el-select v-model="currentTicket.status" placeholder="状态" style="width: 140px">
                <el-option label="待处理" value="OPEN" />
                <el-option label="处理中" value="IN_PROGRESS" />
                <el-option label="已解决" value="RESOLVED" />
                <el-option label="已关闭" value="CLOSED" />
              </el-select>
              <el-select v-model="currentTicket.priority" placeholder="优先级" style="width: 140px">
                <el-option label="低" value="LOW" />
                <el-option label="中" value="MID" />
                <el-option label="高" value="HIGH" />
                <el-option label="紧急" value="URGENT" />
              </el-select>
              <el-button type="primary" :loading="saveLoading" @click="saveTicket">保存工单</el-button>
              <el-button type="danger" plain @click="deleteCurrentTicket">删除</el-button>
            </div>
          </div>

          <div class="ticket-facts">
            <span>问题类型：{{ problemTypeText(currentTicket.problemType) }}</span>
            <span>门户用户：{{ currentTicket.portalUserId || '-' }}</span>
            <span>客户 ID：{{ currentTicket.customerId || '-' }}</span>
            <span>联系信息：{{ currentTicket.contactInfo || '-' }}</span>
            <span>主题：{{ currentTicket.theme || '-' }}</span>
          </div>

          <div class="message-board">
            <div
              v-for="message in displayReplies"
              :key="message.id"
              class="message-row"
              :class="message.fromType === 'ADMIN' ? 'message-admin' : 'message-user'"
            >
              <div class="message-avatar">{{ message.fromType === 'ADMIN' ? adminInitial : '游' }}</div>
              <div class="message-bubble">
                <div class="message-meta">
                  <span>{{ message.fromType === 'ADMIN' ? '本社团客服' : '用户' }}</span>
                  <span>{{ formatTime(message.createdAt) }}</span>
                </div>
                <div class="message-content">{{ message.content }}</div>
              </div>
            </div>
          </div>

          <div class="reply-box">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="4"
              resize="none"
              placeholder="输入回复内容，发送后门户用户端会立即在客服对话框里看到"
              @keyup.ctrl.enter="sendReply"
            />
            <div class="reply-actions">
              <span>Ctrl + Enter 快速发送</span>
              <el-button type="primary" :loading="replyLoading" @click="sendReply">发送回复</el-button>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-else description="请选择左侧工单开始处理用户会话" />
    </section>

    <el-dialog v-model="dialogVisible" title="新增工单" width="620px" @close="closeDialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="客户 ID" prop="customerId">
          <el-input v-model.number="form.customerId" type="number" placeholder="可选" />
        </el-form-item>
        <el-form-item label="关联订单" prop="orderId">
          <el-input v-model.number="form.orderId" type="number" placeholder="可选" />
        </el-form-item>
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="问题类型" prop="problemType">
          <el-select v-model="form.problemType" style="width: 100%">
            <el-option label="投诉" value="COMPLAINT" />
            <el-option label="修改" value="MODIFY" />
            <el-option label="退款" value="REFUND" />
            <el-option label="咨询" value="CONSULT" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" style="width: 100%">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MID" />
            <el-option label="高" value="HIGH" />
            <el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="待处理" value="OPEN" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { ticketApi, type Ticket, type TicketReply } from '@/utils/api/ticket'

const pageSize = 12
const tickets = ref<Ticket[]>([])
const total = ref(0)
const currentPage = ref(1)
const searchKey = ref('')
const listLoading = ref(false)
const detailLoading = ref(false)
const saveLoading = ref(false)
const replyLoading = ref(false)
const submitLoading = ref(false)

const currentTicket = ref<Partial<Ticket> | null>(null)
const replies = ref<TicketReply[]>([])
const replyContent = ref('')

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = ref<Partial<Ticket>>({
  customerId: undefined,
  orderId: undefined,
  title: '',
  status: 'OPEN',
  priority: 'MID',
  description: '',
  problemType: 'CONSULT'
})

const rules = {
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }]
}

const adminInfo = computed(() => {
  const stored = localStorage.getItem('userInfo')
  return stored ? JSON.parse(stored) : {}
})

const adminInitial = computed(() => (adminInfo.value?.realName || adminInfo.value?.username || '客').slice(0, 1))

const displayReplies = computed(() => {
  if (replies.value.length) {
    return replies.value
  }
  if (currentTicket.value?.description) {
    return [
      {
        id: `fallback-${currentTicket.value.id}`,
        ticketId: currentTicket.value.id || '',
        fromType: 'PORTAL',
        fromUserId: currentTicket.value.portalUserId || 0,
        content: currentTicket.value.description,
        createdAt: currentTicket.value.createdAt || ''
      }
    ] as TicketReply[]
  }
  return []
})

onMounted(() => {
  loadTickets()
})

const loadTickets = async (selectedId?: number | string) => {
  listLoading.value = true
  try {
    const res = await ticketApi.list(currentPage.value, pageSize, searchKey.value || undefined)
    tickets.value = res.list || []
    total.value = res.total || 0

    const nextId = selectedId || currentTicket.value?.id || tickets.value[0]?.id
    if (nextId) {
      await openTicket(nextId)
    } else {
      currentTicket.value = null
      replies.value = []
    }
  } catch (error) {
    ElMessage.error('工单列表加载失败')
  } finally {
    listLoading.value = false
  }
}

const openTicket = async (id: number | string) => {
  detailLoading.value = true
  try {
    const [ticket, ticketReplies] = await Promise.all([
      ticketApi.getById(id),
      ticketApi.getReplies(id)
    ])
    currentTicket.value = { ...ticket }
    replies.value = ticketReplies || []
  } catch (error) {
    ElMessage.error('工单详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

const search = () => {
  currentPage.value = 1
  loadTickets()
}

const resetSearch = () => {
  searchKey.value = ''
  currentPage.value = 1
  loadTickets()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadTickets()
}

const saveTicket = async () => {
  if (!currentTicket.value?.id) {
    return
  }
  saveLoading.value = true
  try {
    await ticketApi.update(currentTicket.value.id, {
      ...currentTicket.value,
      handlerUserId: currentTicket.value.handlerUserId || adminInfo.value?.id || 1
    })
    ElMessage.success('工单已更新')
    await loadTickets(currentTicket.value.id)
  } catch (error) {
    ElMessage.error('工单更新失败')
  } finally {
    saveLoading.value = false
  }
}

const sendReply = async () => {
  if (!currentTicket.value?.id) {
    return
  }
  const content = replyContent.value.trim()
  if (!content) {
    ElMessage.warning('请输入回复内容')
    return
  }

  replyLoading.value = true
  try {
    await ticketApi.reply(currentTicket.value.id, {
      content,
      handlerUserId: adminInfo.value?.id || 1
    })

    if (currentTicket.value.status === 'OPEN') {
      await ticketApi.update(currentTicket.value.id, {
        ...currentTicket.value,
        status: 'IN_PROGRESS',
        handlerUserId: adminInfo.value?.id || 1
      })
      currentTicket.value.status = 'IN_PROGRESS'
    }

    replyContent.value = ''
    ElMessage.success('回复已发送')
    await loadTickets(currentTicket.value.id)
  } catch (error) {
    ElMessage.error('回复发送失败')
  } finally {
    replyLoading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    customerId: undefined,
    orderId: undefined,
    title: '',
    status: 'OPEN',
    priority: 'MID',
    description: '',
    problemType: 'CONSULT'
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) {
    return
  }
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const created: any = await ticketApi.create(form.value as Ticket)
    ElMessage.success('工单已创建')
    dialogVisible.value = false
    await loadTickets(created.id)
  } catch (error) {
    ElMessage.error('工单创建失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: Ticket) => {
  try {
    await ElMessageBox.confirm(`确定删除工单 "${row.title}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await ticketApi.delete(row.id)
    ElMessage.success('工单已删除')
    if (currentTicket.value?.id === row.id) {
      currentTicket.value = null
      replies.value = []
      replyContent.value = ''
    }
    await loadTickets()
  } catch (error) {
    // ignore cancel
  }
}

const deleteCurrentTicket = () => {
  if (currentTicket.value?.id) {
    handleDelete(currentTicket.value as Ticket)
  }
}

const closeDialog = () => {
  form.value = {
    customerId: undefined,
    orderId: undefined,
    title: '',
    status: 'OPEN',
    priority: 'MID',
    description: '',
    problemType: 'CONSULT'
  }
}

const formatTime = (value?: string) => {
  if (!value) {
    return '-'
  }
  return value.replace('T', ' ').slice(0, 16)
}

const statusText = (status?: string) => {
  const map: Record<string, string> = {
    OPEN: '待处理',
    IN_PROGRESS: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return status ? map[status] || status : '-'
}

const priorityText = (priority?: string) => {
  const map: Record<string, string> = {
    LOW: '低优先级',
    MID: '中优先级',
    HIGH: '高优先级',
    URGENT: '紧急'
  }
  return priority ? map[priority] || priority : '-'
}

const problemTypeText = (problemType?: string) => {
  const map: Record<string, string> = {
    COMPLAINT: '投诉',
    MODIFY: '修改',
    REFUND: '退款',
    CONSULT: '咨询'
  }
  return problemType ? map[problemType] || problemType : '-'
}
</script>

<style scoped>
.ticket-page {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 24px;
  min-height: calc(100vh - 170px);
}

.inbox-panel,
.conversation-panel {
  background: white;
  border-radius: 24px;
  box-shadow: 0 18px 36px rgba(26, 54, 93, 0.08);
  overflow: hidden;
}

.inbox-panel {
  display: flex;
  flex-direction: column;
  padding: 24px 18px;
}

.panel-top,
.conversation-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.panel-kicker {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--voyage-sunset);
}

.panel-top h2,
.conversation-header h2 {
  margin: 0;
  color: var(--voyage-navy);
  font-family: 'Playfair Display', serif;
}

.panel-search {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 10px;
  margin: 18px 0;
}

.ticket-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-right: 4px;
  margin-bottom: 16px;
}

.ticket-card {
  width: 100%;
  text-align: left;
  border: 1px solid rgba(212, 165, 116, 0.34);
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.08), rgba(26, 54, 93, 0.03));
  border-radius: 18px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.ticket-card:hover,
.ticket-card.active {
  transform: translateY(-2px);
  border-color: var(--voyage-sunset);
  box-shadow: 0 18px 28px rgba(224, 122, 95, 0.14);
}

.ticket-card-top,
.ticket-card-meta,
.reply-actions,
.message-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.status-OPEN {
  color: #e76f51;
  background: rgba(231, 111, 81, 0.12);
}

.status-IN_PROGRESS {
  color: #1d6fd6;
  background: rgba(29, 111, 214, 0.12);
}

.status-RESOLVED {
  color: #2aa876;
  background: rgba(42, 168, 118, 0.12);
}

.status-CLOSED {
  color: #6c757d;
  background: rgba(108, 117, 125, 0.12);
}

.ticket-card strong {
  display: block;
  margin: 12px 0 8px;
  color: var(--voyage-navy);
  font-size: 15px;
}

.ticket-card p {
  margin: 0 0 12px;
  color: rgba(45, 55, 72, 0.68);
  line-height: 1.6;
  font-size: 13px;
}

.ticket-time,
.ticket-card-meta,
.message-meta,
.ticket-facts,
.reply-actions span {
  color: rgba(45, 55, 72, 0.58);
  font-size: 12px;
}

.conversation-panel {
  padding: 24px;
}

.conversation-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.conversation-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.ticket-facts {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin: 18px 0 20px;
}

.message-board {
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
  min-height: 360px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.message-user {
  flex-direction: row;
}

.message-admin {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--voyage-sunset), #f4a261);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.message-admin .message-avatar {
  background: linear-gradient(135deg, var(--voyage-navy), #24507b);
}

.message-bubble {
  max-width: min(72%, 600px);
  padding: 14px 16px;
  border-radius: 20px;
  background: white;
  border: 1px solid rgba(212, 165, 116, 0.34);
  box-shadow: 0 14px 24px rgba(26, 54, 93, 0.06);
}

.message-admin .message-bubble {
  background: linear-gradient(135deg, rgba(26, 54, 93, 0.08), rgba(36, 80, 123, 0.08));
}

.message-content {
  white-space: pre-wrap;
  line-height: 1.75;
  color: var(--voyage-ink);
}

.reply-box {
  margin-top: 18px;
  padding: 18px;
  border-radius: 22px;
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.08), rgba(26, 54, 93, 0.04));
}

.reply-actions {
  margin-top: 14px;
}

@media (max-width: 1200px) {
  .ticket-page {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .panel-search,
  .conversation-actions,
  .ticket-card-top,
  .ticket-card-meta,
  .reply-actions,
  .ticket-facts {
    grid-template-columns: 1fr;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .message-bubble {
    max-width: 100%;
  }
}
</style>
