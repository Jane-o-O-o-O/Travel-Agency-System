<template>
  <div class="portal-page">
    <div class="card-head">
      <span class="card-icon">🎫</span>
      <h1 class="card-title">我的工单</h1>
      <p class="card-desc">查看您提交的出行意向及客服回复</p>
    </div>
    <div v-loading="loading" class="ticket-list">
      <template v-if="list.length">
        <div
          v-for="t in list"
          :key="t.id"
          class="ticket-item"
          @click="goDetail(t.id)"
        >
          <div class="ticket-no">{{ t.ticketNo }}</div>
          <div class="ticket-title">{{ t.title }}</div>
          <div class="ticket-meta">
            <span>{{ t.theme || '-' }}</span>
            <span>{{ t.peopleCount }}人 / {{ t.days }}天</span>
            <span :class="'status-' + (t.status || '')">{{ statusText(t.status) }}</span>
          </div>
          <div class="ticket-time">{{ t.createdAt }}</div>
        </div>
      </template>
      <el-empty v-else description="暂无工单，去提交出行意向吧" />
    </div>
    <el-pagination
      v-if="total > pageSize"
      :current-page="pageNo"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="load"
      style="margin-top: 20px; justify-content: center;"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { portalTicketApi } from '@/utils/api/portal'

const router = useRouter()
const loading = ref(false)
const list = ref<any[]>([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
    const res: any = await portalTicketApi.myList(pageNo.value, pageSize.value)
    if (res) {
      list.value = res.list || []
      total.value = res.total || 0
    }
  } finally {
    loading.value = false
  }
}

const goDetail = (id: number | string) => {
  router.push('/portal/ticket/' + id)
}

onMounted(load)
</script>

<style scoped>
.portal-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 40px;
  background: white;
  border: 2px solid var(--voyage-sand);
  border-radius: 12px;
  box-shadow: 0 8px 24px var(--voyage-shadow);
}
.card-head { text-align: center; margin-bottom: 28px; }
.card-icon { font-size: 48px; display: block; margin-bottom: 12px; }
.card-title {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  color: var(--voyage-navy);
  margin: 0 0 8px;
}
.card-desc { color: var(--voyage-ink); font-size: 14px; margin: 0; }
.ticket-list { min-height: 200px; }
.ticket-item {
  padding: 16px 20px;
  border: 2px solid var(--voyage-sand);
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.ticket-item:hover {
  border-color: var(--voyage-sunset);
  box-shadow: 0 4px 12px var(--voyage-shadow);
}
.ticket-no { font-size: 12px; color: var(--voyage-ink); opacity: 0.8; margin-bottom: 4px; }
.ticket-title { font-weight: 600; color: var(--voyage-navy); margin-bottom: 8px; }
.ticket-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--voyage-ink);
  margin-bottom: 4px;
}
.ticket-meta .status-OPEN { color: var(--voyage-sunset); }
.ticket-meta .status-IN_PROGRESS { color: #4299e1; }
.ticket-meta .status-RESOLVED { color: #48bb78; }
.ticket-meta .status-CLOSED { color: #a0aec0; }
.ticket-time { font-size: 12px; color: var(--voyage-ink); opacity: 0.7; }
</style>
