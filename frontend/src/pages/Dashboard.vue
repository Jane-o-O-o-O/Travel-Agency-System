<template>
  <div class="voyage-dashboard">
    <!-- Map Background -->
    <div class="world-map-bg">
      <svg class="map-grid" viewBox="0 0 1000 600" preserveAspectRatio="none">
        <defs>
          <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="var(--voyage-sand)" stroke-width="0.5" opacity="0.2"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#grid)" />
        <!-- Latitude lines -->
        <line x1="0" y1="150" x2="1000" y2="150" stroke="var(--voyage-sand)" stroke-width="1" opacity="0.3" stroke-dasharray="10,5"/>
        <line x1="0" y1="300" x2="1000" y2="300" stroke="var(--voyage-sand)" stroke-width="1.5" opacity="0.4" stroke-dasharray="10,5"/>
        <line x1="0" y1="450" x2="1000" y2="450" stroke="var(--voyage-sand)" stroke-width="1" opacity="0.3" stroke-dasharray="10,5"/>
        <!-- Longitude lines -->
        <line x1="250" y1="0" x2="250" y2="600" stroke="var(--voyage-sand)" stroke-width="1" opacity="0.3" stroke-dasharray="10,5"/>
        <line x1="500" y1="0" x2="500" y2="600" stroke="var(--voyage-sand)" stroke-width="1" opacity="0.3" stroke-dasharray="10,5"/>
        <line x1="750" y1="0" x2="750" y2="600" stroke="var(--voyage-sand)" stroke-width="1" opacity="0.3" stroke-dasharray="10,5"/>
      </svg>
    </div>

    <!-- Header Banner -->
    <div class="expedition-header">
      <div class="header-emblem">
        <svg width="50" height="50" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="45" fill="none" stroke="var(--voyage-sand)" stroke-width="2"/>
          <circle cx="50" cy="50" r="35" fill="none" stroke="var(--voyage-sunset)" stroke-width="1.5"/>
          <polygon points="50,20 55,48 50,50 45,48" fill="var(--voyage-sunset)"/>
        </svg>
      </div>
      <div class="header-text">
        <h1 class="dashboard-title">运营管理中心</h1>
        <p class="dashboard-subtitle">旅行社经营数据一览</p>
      </div>
      <div class="header-stamp">
        <span class="stamp-text">在线</span>
      </div>
    </div>

    <!-- Statistics as Luggage Tags -->
    <div class="luggage-tags">
      <div class="tag-item" v-for="(stat, index) in statistics" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
        <div class="tag-hole"></div>
        <div class="tag-content">
          <div class="tag-icon">{{ stat.icon }}</div>
          <div class="tag-value">{{ stat.value }}</div>
          <div class="tag-label">{{ stat.label }}</div>
        </div>
        <div class="tag-rope"></div>
      </div>
    </div>

    <!-- Journey Timeline -->
    <div class="journey-section">
      <div class="section-header">
        <div class="section-icon">🧭</div>
        <h2 class="section-title">最近订单</h2>
        <div class="section-line"></div>
      </div>
      
      <div class="timeline-container">
        <div class="timeline-item" v-for="(order, index) in recentOrders" :key="order.orderNo">
          <div class="timeline-marker">
            <div class="marker-dot"></div>
            <div class="marker-line" v-if="index < recentOrders.length - 1"></div>
          </div>
          <div class="timeline-card">
            <div class="card-stamp" :class="`stamp-${getStatusClass(order.status)}`">
              {{ getStatusLabel(order.status) }}
            </div>
            <div class="card-content">
              <h3 class="order-number">{{ order.orderNo }}</h3>
              <div class="order-details">
                <span class="detail-item">
                  <span class="detail-icon">👤</span>
                  {{ order.customerName }}
                </span>
                <span class="detail-item">
                  <span class="detail-icon">💰</span>
                  ¥{{ order.amount.toLocaleString() }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions & Tips -->
    <div class="bottom-section">
      <div class="quick-actions">
        <div class="section-header">
          <div class="section-icon">⚡</div>
          <h2 class="section-title">快速操作</h2>
          <div class="section-line"></div>
        </div>
        <div class="action-buttons">
          <button class="action-btn" @click="navigateToAdd('customers')">
            <span class="btn-icon">👥</span>
            <span class="btn-label">新增旅客</span>
          </button>
          <button class="action-btn" @click="navigateToAdd('products')">
            <span class="btn-icon">🎫</span>
            <span class="btn-label">新增产品</span>
          </button>
          <button class="action-btn" @click="navigateToAdd('orders')">
            <span class="btn-icon">📋</span>
            <span class="btn-label">新增订单</span>
          </button>
          <button class="action-btn" @click="navigateToAdd('templates')">
            <span class="btn-icon">📍</span>
            <span class="btn-label">新增模板</span>
          </button>
        </div>
      </div>

      <div class="travel-tips">
        <div class="section-header">
          <div class="section-icon">💡</div>
          <h2 class="section-title">平台提示</h2>
          <div class="section-line"></div>
        </div>
        <div class="tips-scroll">
          <div class="tip-card" v-for="(tip, index) in tips" :key="index">
            <div class="tip-marker">{{ index + 1 }}</div>
            <div class="tip-text">{{ tip }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Decorative Elements -->
    <div class="floating-elements">
      <div class="float-icon plane">✈️</div>
      <div class="float-icon ship">🚢</div>
      <div class="float-icon balloon">🎈</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const statistics = ref([
  { icon: '📦', value: 0, label: '总订单数' },
  { icon: '👥', value: 0, label: '旅客总数' },
  { icon: '🌍', value: 0, label: '目的地数' },
  { icon: '🎫', value: 0, label: '待处理工单' }
])

const recentOrders = ref([
  {
    orderNo: 'ORD20240227001',
    customerName: '测试客户1',
    status: 'CONFIRMED',
    amount: 5000
  },
  {
    orderNo: 'ORD20240227002',
    customerName: '测试客户2',
    status: 'PENDING_CONFIRM',
    amount: 8500
  },
  {
    orderNo: 'ORD20240227003',
    customerName: '测试客户3',
    status: 'COMPLETED',
    amount: 12000
  }
])

const tips = ref([
  '欢迎使用时拾纪旅行社管理平台 - 开启您的旅程探险',
  '建议先在「旅行者」模块创建客户档案',
  '在「旅程产品」中设计精彩的旅行路线',
  '使用「行程模板」功能快速创建标准化行程',
  '记得定期查看「工单中心」处理客户咨询'
])

const getStatusClass = (status: string): string => {
  const classMap: Record<string, string> = {
    DRAFT: 'draft',
    PENDING_CONFIRM: 'pending',
    CONFIRMED: 'confirmed',
    COMPLETED: 'completed',
    CANCELLED: 'cancelled'
  }
  return classMap[status] || 'draft'
}

const getStatusLabel = (status: string): string => {
  const labelMap: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_CONFIRM: '待确认',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return labelMap[status] || status
}

const navigateToAdd = (path: string) => {
  router.push({ path: `/${path}`, query: { add: '1' } })
}

onMounted(() => {
  // Simulate fetching statistics
  setTimeout(() => {
    statistics.value[0].value = 15
    statistics.value[1].value = 28
    statistics.value[2].value = 12
    statistics.value[3].value = 3
  }, 500)
})
</script>

<style scoped>
.voyage-dashboard {
  min-height: 100%;
  padding: 24px;
  background: var(--voyage-parchment);
  position: relative;
  overflow: hidden;
}

/* Map Background */
.world-map-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.4;
  pointer-events: none;
  z-index: 0;
}

.map-grid {
  width: 100%;
  height: 100%;
}

/* Expedition Header */
.expedition-header {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px 32px;
  background: linear-gradient(135deg, var(--voyage-navy), #0f2744);
  border-radius: 12px;
  margin-bottom: 32px;
  box-shadow: 0 8px 24px rgba(26, 54, 93, 0.3);
  border: 2px solid var(--voyage-sand);
}

.header-emblem svg {
  filter: drop-shadow(0 2px 8px rgba(224, 122, 95, 0.4));
  animation: gentleSpin 20s linear infinite;
}

@keyframes gentleSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.header-text {
  flex: 1;
}

.dashboard-title {
  font-family: 'Playfair Display', serif;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0 0 8px;
  color: var(--voyage-pearl);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.dashboard-subtitle {
  font-family: 'Space Mono', monospace;
  font-size: 13px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: var(--voyage-sand);
  margin: 0;
}

.header-stamp {
  padding: 8px 20px;
  border: 3px dashed var(--voyage-sunset);
  border-radius: 4px;
  transform: rotate(-5deg);
}

.stamp-text {
  font-family: 'Space Mono', monospace;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--voyage-sunset);
}

/* Luggage Tags Statistics */
.luggage-tags {
  position: relative;
  z-index: 10;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.tag-item {
  position: relative;
  background: white;
  border: 3px solid var(--voyage-sand);
  border-radius: 0 16px 16px 0;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  animation: tagDrop 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) backwards;
}

@keyframes tagDrop {
  from {
    opacity: 0;
    transform: translateY(-30px) rotate(-10deg);
  }
  to {
    opacity: 1;
    transform: translateY(0) rotate(0deg);
  }
}

.tag-hole {
  position: absolute;
  top: 50%;
  left: -12px;
  width: 24px;
  height: 24px;
  background: var(--voyage-parchment);
  border: 3px solid var(--voyage-sand);
  border-radius: 50%;
  transform: translateY(-50%);
}

.tag-rope {
  position: absolute;
  top: 50%;
  left: -8px;
  width: 40px;
  height: 2px;
  background: repeating-linear-gradient(
    90deg,
    var(--voyage-sand),
    var(--voyage-sand) 2px,
    transparent 2px,
    transparent 4px
  );
  transform: translateY(-50%);
}

.tag-content {
  text-align: center;
}

.tag-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.tag-value {
  font-family: 'Space Mono', monospace;
  font-size: 36px;
  font-weight: 700;
  color: var(--voyage-navy);
  margin-bottom: 8px;
}

.tag-label {
  font-family: 'Karla', sans-serif;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--voyage-ink);
}

/* Journey Section */
.journey-section {
  position: relative;
  z-index: 10;
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-icon {
  font-size: 28px;
}

.section-title {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  color: var(--voyage-navy);
  margin: 0;
}

.section-line {
  flex: 1;
  height: 3px;
  background: linear-gradient(to right, var(--voyage-sand), transparent);
}

/* Timeline */
.timeline-container {
  background: white;
  border: 2px solid var(--voyage-sand);
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.timeline-item {
  display: flex;
  gap: 24px;
  position: relative;
}

.timeline-marker {
  position: relative;
  width: 24px;
  flex-shrink: 0;
}

.marker-dot {
  width: 24px;
  height: 24px;
  background: var(--voyage-sunset);
  border: 4px solid white;
  border-radius: 50%;
  box-shadow: 0 0 0 2px var(--voyage-sunset);
  position: relative;
  z-index: 2;
}

.marker-line {
  position: absolute;
  left: 50%;
  top: 24px;
  bottom: -24px;
  width: 2px;
  background: var(--voyage-sand);
  transform: translateX(-50%);
  z-index: 1;
}

.timeline-card {
  flex: 1;
  background: var(--voyage-pearl);
  border: 2px solid var(--voyage-sand);
  border-radius: 8px;
  padding: 20px 24px;
  margin-bottom: 24px;
  position: relative;
  transition: all 0.3s ease;
}

.timeline-card:hover {
  transform: translateX(8px);
  box-shadow: 0 4px 16px rgba(224, 122, 95, 0.2);
}

.card-stamp {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border: 2px dashed;
  border-radius: 4px;
  font-family: 'Space Mono', monospace;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  transform: rotate(-3deg);
}

.stamp-confirmed {
  color: #48bb78;
  border-color: #48bb78;
  background: rgba(72, 187, 120, 0.1);
}

.stamp-pending {
  color: #ed8936;
  border-color: #ed8936;
  background: rgba(237, 137, 54, 0.1);
}

.stamp-completed {
  color: #4299e1;
  border-color: #4299e1;
  background: rgba(66, 153, 225, 0.1);
}

.stamp-draft {
  color: #a0aec0;
  border-color: #a0aec0;
  background: rgba(160, 174, 192, 0.1);
}

.stamp-cancelled {
  color: #f56565;
  border-color: #f56565;
  background: rgba(245, 101, 101, 0.1);
}

.card-content {
  padding-right: 100px;
}

.order-number {
  font-family: 'Space Mono', monospace;
  font-size: 18px;
  font-weight: 700;
  color: var(--voyage-navy);
  margin: 0 0 12px;
}

.order-details {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'Karla', sans-serif;
  font-size: 14px;
  color: var(--voyage-ink);
}

.detail-icon {
  font-size: 16px;
}

/* Bottom Section */
.bottom-section {
  position: relative;
  z-index: 10;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.quick-actions,
.travel-tips {
  background: white;
  border: 2px solid var(--voyage-sand);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  background: linear-gradient(135deg, var(--voyage-pearl), white);
  border: 2px solid var(--voyage-sand);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Karla', sans-serif;
}

.action-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(224, 122, 95, 0.2);
  border-color: var(--voyage-sunset);
}

.btn-icon {
  font-size: 32px;
}

.btn-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--voyage-navy);
}

.tips-scroll {
  max-height: 300px;
  overflow-y: auto;
}

.tip-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--voyage-pearl);
  border-left: 4px solid var(--voyage-sand);
  border-radius: 4px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.tip-card:hover {
  border-left-color: var(--voyage-sunset);
  transform: translateX(4px);
}

.tip-marker {
  width: 28px;
  height: 28px;
  background: var(--voyage-sunset);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Space Mono', monospace;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.tip-text {
  font-family: 'Karla', sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: var(--voyage-ink);
}

/* Floating Elements */
.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.float-icon {
  position: absolute;
  font-size: 32px;
  opacity: 0.15;
  animation: float 30s infinite ease-in-out;
}

.plane {
  top: 15%;
  right: 10%;
  animation-duration: 35s;
}

.ship {
  bottom: 25%;
  left: 8%;
  animation-duration: 40s;
  animation-delay: 5s;
}

.balloon {
  top: 60%;
  right: 20%;
  animation-duration: 32s;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(30px, -40px) rotate(5deg);
  }
  50% {
    transform: translate(-20px, 20px) rotate(-5deg);
  }
  75% {
    transform: translate(40px, -15px) rotate(3deg);
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .expedition-header {
    flex-direction: column;
    text-align: center;
  }

  .dashboard-title {
    font-size: 24px;
  }

  .luggage-tags {
    grid-template-columns: repeat(2, 1fr);
  }

  .bottom-section {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>