<template>
  <div class="portal-page detail-page">
    <div v-loading="loading" v-if="ticket">
      <div class="detail-header">
        <router-link to="/portal/my-tickets" class="back-link">← 返回我的工单</router-link>
        <div class="ticket-no">{{ ticket.ticketNo }}</div>
        <div :class="'status-badge status-' + (ticket.status || '')">{{ statusText(ticket.status) }}</div>
      </div>
      <div class="detail-block">
        <h3>工单信息</h3>
        <dl class="info-dl">
          <dt>标题</dt><dd>{{ ticket.title }}</dd>
          <dt>意向主题</dt><dd>{{ ticket.theme || '-' }}</dd>
          <dt>出行人数/天数</dt><dd>{{ ticket.peopleCount }}人 / {{ ticket.days }}天</dd>
          <dt>期望日期</dt><dd>{{ ticket.expectedDate || '-' }}</dd>
          <dt>联系方式</dt><dd>{{ ticket.contactInfo || '-' }}</dd>
          <dt>特殊要求</dt><dd>{{ ticket.specialRequirement || '-' }}</dd>
        </dl>
      </div>
      <div class="detail-block">
        <h3>对话记录</h3>
        <div class="reply-list">
          <div v-for="r in replies" :key="r.id" :class="['reply-item', r.fromType === 'ADMIN' ? 'reply-admin' : 'reply-user']">
            <div class="reply-meta">
              <span class="reply-from">{{ r.fromType === 'ADMIN' ? '客服' : '我' }}</span>
              <span class="reply-time">{{ r.createdAt }}</span>
            </div>
            <div class="reply-content">{{ r.content }}</div>
          </div>
        </div>
        <div class="reply-form">
          <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="输入回复内容..." />
          <el-button type="primary" :loading="replyLoading" @click="sendReply" style="margin-top:10px">发送回复</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else-if="!loading" description="工单不存在或无权查看" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { portalTicketApi, type TicketReply } from '@/utils/api/portal'

const route = useRoute()
// 保持字符串，避免大 ID（雪花）转 Number 精度丢失导致查不到工单
const id = computed(() => String(route.params.id ?? ''))
const loading = ref(true)
const ticket = ref<any>(null)
const replies = ref<TicketReply[]>([])
const replyContent = ref('')
const replyLoading = ref(false)

const statusText = (s: string) => {
  const m: Record<string, string> = {
    OPEN: '待处理',
    IN_PROGRESS: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return m[s] || s || '-'
}

const load = async () => {
  loading.value = true
  try {
    const res: any = await portalTicketApi.getDetail(id.value)
    if (res && res.ticket) {
      ticket.value = res.ticket
      replies.value = res.replies || []
    } else {
      ticket.value = null
    }
  } catch {
    ticket.value = null
  } finally {
    loading.value = false
  }
}

const sendReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    await portalTicketApi.addReply(id.value, replyContent.value.trim())
    replyContent.value = ''
    ElMessage.success('回复成功')
    load()
  } finally {
    replyLoading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.detail-page { max-width: 640px; margin: 0 auto; padding: 32px; background: white; border: 2px solid var(--voyage-sand); border-radius: 12px; box-shadow: 0 8px 24px var(--voyage-shadow); }
.detail-header { margin-bottom: 24px; }
.back-link { color: var(--voyage-sunset); text-decoration: none; font-size: 14px; display: inline-block; margin-bottom: 8px; }
.back-link:hover { text-decoration: underline; }
.ticket-no { font-size: 13px; color: var(--voyage-ink); opacity: 0.8; }
.status-badge { display: inline-block; margin-top: 8px; padding: 4px 12px; border-radius: 6px; font-size: 13px; font-weight: 600; }
.status-OPEN { background: rgba(224,122,95,0.2); color: var(--voyage-sunset); }
.status-IN_PROGRESS { background: rgba(66,153,225,0.2); color: #4299e1; }
.status-RESOLVED { background: rgba(72,187,120,0.2); color: #48bb78; }
.status-CLOSED { background: rgba(160,174,192,0.2); color: #a0aec0; }
.detail-block { margin-bottom: 28px; }
.detail-block h3 { font-family: 'Playfair Display', serif; font-size: 18px; color: var(--voyage-navy); margin: 0 0 12px; }
.info-dl { margin: 0; display: grid; gap: 8px; }
.info-dl dt { font-size: 12px; color: var(--voyage-ink); opacity: 0.8; }
.info-dl dd { margin: 0; font-size: 14px; color: var(--voyage-navy); }
.reply-list { margin-bottom: 20px; }
.reply-item { padding: 12px 16px; border-radius: 8px; margin-bottom: 12px; border: 1px solid var(--voyage-sand); }
.reply-admin { background: var(--voyage-pearl); border-left: 4px solid var(--voyage-sunset); }
.reply-user { background: rgba(26,54,93,0.06); border-left: 4px solid var(--voyage-navy); }
.reply-meta { font-size: 12px; color: var(--voyage-ink); opacity: 0.8; margin-bottom: 6px; }
.reply-from { font-weight: 600; margin-right: 12px; }
.reply-content { font-size: 14px; line-height: 1.6; white-space: pre-wrap; }
.reply-form :deep(.el-textarea__inner) { border-radius: 8px; }
</style>
