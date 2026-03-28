<template>
  <el-drawer
    v-model="drawerVisible"
    :with-header="false"
    size="960px"
    class="support-drawer"
    append-to-body
  >
    <div class="support-shell">
      <aside class="support-sidebar">
        <div class="support-brand">
          <div>
            <p class="support-kicker">在线客服</p>
            <h2>旅行顾问会话中心</h2>
          </div>
          <el-button v-if="isLoggedIn" text @click="startNewConversation">新建咨询</el-button>
        </div>

        <div v-if="!isLoggedIn" class="support-login-state">
          <p>登录后即可像淘宝一样直接和后台客服对话，查看历史咨询记录。</p>
          <el-button type="primary" @click="goLogin">前往登录</el-button>
        </div>

        <div v-else v-loading="listLoading" class="session-list">
          <button
            type="button"
            class="session-create"
            :class="{ active: draftMode }"
            @click="startNewConversation"
          >
            <span class="session-title">发起新咨询</span>
            <span class="session-desc">新问题会自动生成一个客服会话</span>
          </button>

          <button
            v-for="ticket in sessions"
            :key="ticket.id"
            type="button"
            class="session-card"
            :class="{ active: currentTicket?.id === ticket.id && !draftMode }"
            @click="openTicket(ticket.id)"
          >
            <div class="session-top">
              <span class="session-status" :class="'status-' + ticket.status">{{ statusText(ticket.status) }}</span>
              <span class="session-time">{{ formatTime(ticket.updatedAt || ticket.createdAt) }}</span>
            </div>
            <div class="session-title">{{ ticket.title }}</div>
            <div class="session-desc">{{ ticket.specialRequirement || ticket.description || ticket.theme || '点击查看聊天记录' }}</div>
          </button>

          <el-empty v-if="!sessions.length && !listLoading" description="暂无咨询记录，先发起一个新会话" />
        </div>
      </aside>

      <section class="support-main">
        <template v-if="!isLoggedIn">
          <div class="state-panel">
            <h3>未登录</h3>
            <p>登录后可以直接联系本社团客服，查看回复进度和历史消息。</p>
            <el-button type="primary" @click="goLogin">去登录</el-button>
          </div>
        </template>

        <template v-else-if="draftMode">
          <div class="chat-header">
            <div>
              <p class="chat-kicker">新建会话</p>
              <h3>和客服发起一次新的咨询</h3>
            </div>
            <router-link to="/portal/submit-ticket" class="header-link">填写详细出行需求</router-link>
          </div>

          <div class="state-panel draft-panel">
            <h3>先发第一条消息</h3>
            <p>第一条消息会自动创建咨询单，并同步出现在聊天记录里。</p>
            <el-input v-model="draftTitle" placeholder="咨询标题，可不填" maxlength="30" show-word-limit />
          </div>

          <div class="composer standalone">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="8"
              resize="none"
              placeholder="例如：五一想带 4 个同学去泉州 3 天，希望预算控制在 2000/人，能否安排亲子或非遗主题路线？"
              @keyup.ctrl.enter="sendMessage"
            />
            <div class="composer-actions">
              <span>Ctrl + Enter 快速发送</span>
              <el-button type="primary" :loading="sending" @click="sendMessage">发送并创建会话</el-button>
            </div>
          </div>
        </template>

        <template v-else-if="currentTicket">
          <div class="chat-header">
            <div>
              <p class="chat-kicker">{{ currentTicket.ticketNo }}</p>
              <h3>{{ currentTicket.title }}</h3>
            </div>
            <div class="chat-meta">
              <span class="chat-tag">{{ statusText(currentTicket.status) }}</span>
              <router-link to="/portal/my-tickets" class="header-link">查看全部咨询</router-link>
            </div>
          </div>

          <div class="ticket-summary">
            <span>主题：{{ currentTicket.theme || '在线咨询' }}</span>
            <span>联系方式：{{ currentTicket.contactInfo || userInfo?.phone || userInfo?.email || '站内联系' }}</span>
            <span>更新时间：{{ formatTime(currentTicket.updatedAt || currentTicket.createdAt) }}</span>
          </div>

          <div ref="replyViewport" v-loading="detailLoading" class="message-list">
            <div
              v-for="message in displayReplies"
              :key="message.id"
              class="message-row"
              :class="message.fromType === 'ADMIN' ? 'message-admin' : 'message-user'"
            >
              <div class="message-avatar">{{ message.fromType === 'ADMIN' ? '客' : userInitial }}</div>
              <div class="message-bubble">
                <div class="message-meta">
                  <span>{{ message.fromType === 'ADMIN' ? '本社团客服' : '我' }}</span>
                  <span>{{ formatTime(message.createdAt) }}</span>
                </div>
                <div class="message-content">{{ message.content }}</div>
              </div>
            </div>
          </div>

          <div class="composer">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="4"
              resize="none"
              placeholder="输入消息，客服会在后台工单中心直接回复你"
              @keyup.ctrl.enter="sendMessage"
            />
            <div class="composer-actions">
              <span>Ctrl + Enter 快速发送</span>
              <el-button type="primary" :loading="sending" @click="sendMessage">发送消息</el-button>
            </div>
          </div>
        </template>

        <template v-else>
          <div class="state-panel">
            <h3>请选择一个会话</h3>
            <p>左侧可以切换历史咨询，或者直接新建一个对话。</p>
          </div>
        </template>
      </section>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { portalTicketApi, type PortalUserProfile, type TicketReply } from '@/utils/api/portal'
import { getPortalToken, getPortalUserInfo } from '@/utils/portalUser'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const router = useRouter()
const drawerVisible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
})

const listLoading = ref(false)
const detailLoading = ref(false)
const sending = ref(false)
const sessions = ref<any[]>([])
const currentTicket = ref<any | null>(null)
const replies = ref<TicketReply[]>([])
const replyContent = ref('')
const draftTitle = ref('')
const draftMode = ref(false)
const replyViewport = ref<HTMLElement>()

const userInfo = computed<PortalUserProfile | null>(() => getPortalUserInfo())
const isLoggedIn = computed(() => Boolean(getPortalToken() && userInfo.value))
const userInitial = computed(() => (userInfo.value?.realName || userInfo.value?.username || '游').slice(0, 1))

const displayReplies = computed(() => {
  if (replies.value.length) {
    return replies.value
  }
  if (currentTicket.value?.description) {
    return [
      {
        id: `fallback-${currentTicket.value.id}`,
        ticketId: currentTicket.value.id,
        fromType: 'PORTAL',
        fromUserId: currentTicket.value.portalUserId || userInfo.value?.id || 0,
        content: currentTicket.value.description,
        createdAt: currentTicket.value.createdAt
      }
    ] as TicketReply[]
  }
  return []
})

watch(
  () => drawerVisible.value,
  (visible) => {
    if (visible) {
      loadSessions()
    }
  }
)

const loadSessions = async (selectedId?: number | string) => {
  if (!isLoggedIn.value) {
    draftMode.value = false
    currentTicket.value = null
    replies.value = []
    return
  }

  listLoading.value = true
  try {
    const res: any = await portalTicketApi.myList(1, 20)
    sessions.value = res.list || []

    if (!sessions.value.length) {
      startNewConversation()
      return
    }

    const nextId = selectedId || currentTicket.value?.id || sessions.value[0]?.id
    if (nextId) {
      await openTicket(nextId)
    }
  } finally {
    listLoading.value = false
  }
}

const openTicket = async (id: number | string) => {
  draftMode.value = false
  detailLoading.value = true
  try {
    const res: any = await portalTicketApi.getDetail(id)
    currentTicket.value = res.ticket
    replies.value = res.replies || []
    await scrollToBottom()
  } finally {
    detailLoading.value = false
  }
}

const startNewConversation = () => {
  draftMode.value = true
  currentTicket.value = null
  replies.value = []
  replyContent.value = ''
  draftTitle.value = ''
}

const sendMessage = async () => {
  if (!isLoggedIn.value) {
    goLogin()
    return
  }

  const content = replyContent.value.trim()
  if (!content) {
    ElMessage.warning('请输入消息内容')
    return
  }

  sending.value = true
  try {
    if (draftMode.value || !currentTicket.value) {
      const created: any = await portalTicketApi.submit({
        title: draftTitle.value.trim() || content.slice(0, 18) || '在线咨询',
        theme: '在线咨询',
        specialRequirement: content,
        contactInfo: userInfo.value?.phone || userInfo.value?.email || '站内联系'
      })
      replyContent.value = ''
      draftTitle.value = ''
      await loadSessions(created.id)
      ElMessage.success('会话已创建')
      return
    }

    await portalTicketApi.addReply(currentTicket.value.id, content)
    replyContent.value = ''
    await openTicket(currentTicket.value.id)
    await loadSessions(currentTicket.value.id)
  } finally {
    sending.value = false
  }
}

const goLogin = () => {
  drawerVisible.value = false
  router.push('/portal/login')
}

const scrollToBottom = async () => {
  await nextTick()
  const element = replyViewport.value
  if (element) {
    element.scrollTop = element.scrollHeight
  }
}

const statusText = (status: string) => {
  const map: Record<string, string> = {
    OPEN: '待处理',
    IN_PROGRESS: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return map[status] || status || '-'
}

const formatTime = (value?: string) => {
  if (!value) {
    return '-'
  }
  return value.replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.support-shell {
  display: grid;
  grid-template-columns: 300px 1fr;
  height: 100%;
  min-height: 680px;
  background:
    radial-gradient(circle at top left, rgba(212, 165, 116, 0.16), transparent 30%),
    linear-gradient(180deg, #fbfaf6, #f6f0e6);
}

.support-sidebar {
  padding: 24px;
  border-right: 1px solid rgba(26, 54, 93, 0.08);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(10px);
}

.support-brand {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}

.support-kicker,
.chat-kicker {
  margin: 0 0 6px;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--voyage-sunset);
}

.support-brand h2,
.chat-header h3,
.state-panel h3 {
  margin: 0;
  color: var(--voyage-navy);
  font-family: 'Playfair Display', serif;
}

.support-login-state {
  padding: 20px;
  border-radius: 18px;
  background: white;
  border: 1px solid rgba(212, 165, 116, 0.42);
  box-shadow: 0 12px 28px rgba(26, 54, 93, 0.08);
}

.support-login-state p,
.state-panel p {
  margin: 0 0 16px;
  color: rgba(45, 55, 72, 0.82);
  line-height: 1.7;
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: calc(100vh - 190px);
  overflow-y: auto;
  padding-right: 6px;
}

.session-create,
.session-card {
  width: 100%;
  text-align: left;
  border: 1px solid rgba(212, 165, 116, 0.4);
  background: white;
  border-radius: 18px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.session-create:hover,
.session-card:hover,
.session-create.active,
.session-card.active {
  transform: translateY(-2px);
  border-color: var(--voyage-sunset);
  box-shadow: 0 16px 28px rgba(224, 122, 95, 0.14);
}

.session-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 10px;
}

.session-status {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
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

.session-time,
.message-meta,
.ticket-summary {
  color: rgba(45, 55, 72, 0.6);
  font-size: 12px;
}

.session-title {
  display: block;
  margin-bottom: 6px;
  color: var(--voyage-navy);
  font-size: 15px;
  font-weight: 700;
}

.session-desc {
  display: block;
  color: rgba(45, 55, 72, 0.72);
  line-height: 1.5;
  font-size: 13px;
}

.support-main {
  display: flex;
  flex-direction: column;
  padding: 28px;
}

.chat-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 12px;
}

.chat-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-tag {
  padding: 8px 12px;
  background: rgba(212, 165, 116, 0.16);
  border-radius: 999px;
  color: var(--voyage-navy);
  font-size: 13px;
  font-weight: 600;
}

.header-link {
  color: var(--voyage-sunset);
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
}

.ticket-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-bottom: 18px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 6px 8px 0;
  min-height: 360px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.message-admin {
  flex-direction: row;
}

.message-user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--voyage-sunset), #f4a261);
  color: white;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 8px 18px rgba(224, 122, 95, 0.18);
}

.message-admin .message-avatar {
  background: linear-gradient(135deg, var(--voyage-navy), #2a5b88);
}

.message-bubble {
  max-width: min(72%, 560px);
  padding: 14px 16px;
  border-radius: 20px;
  background: white;
  border: 1px solid rgba(212, 165, 116, 0.34);
  box-shadow: 0 12px 22px rgba(26, 54, 93, 0.08);
}

.message-user .message-bubble {
  background: linear-gradient(135deg, rgba(224, 122, 95, 0.12), rgba(244, 162, 97, 0.12));
}

.message-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.message-content {
  white-space: pre-wrap;
  line-height: 1.75;
  color: var(--voyage-ink);
}

.composer {
  margin-top: 18px;
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(212, 165, 116, 0.4);
  box-shadow: 0 18px 30px rgba(26, 54, 93, 0.08);
}

.composer.standalone {
  margin-top: auto;
}

.composer-actions {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: rgba(45, 55, 72, 0.58);
  font-size: 12px;
}

.state-panel {
  margin: auto;
  max-width: 520px;
  text-align: center;
  padding: 32px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(212, 165, 116, 0.34);
  box-shadow: 0 24px 36px rgba(26, 54, 93, 0.08);
}

.draft-panel {
  margin: 18px 0 0;
  max-width: none;
  text-align: left;
}

@media (max-width: 1024px) {
  .support-shell {
    grid-template-columns: 1fr;
    height: auto;
  }

  .support-sidebar {
    border-right: none;
    border-bottom: 1px solid rgba(26, 54, 93, 0.08);
  }

  .session-list {
    max-height: 320px;
  }

  .message-bubble {
    max-width: 100%;
  }
}

@media (max-width: 640px) {
  .support-main,
  .support-sidebar {
    padding: 18px;
  }

  .chat-header,
  .composer-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .ticket-summary {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
