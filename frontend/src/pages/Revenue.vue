<template>
  <div class="revenue-page" v-loading="loading">
    <section class="revenue-hero">
      <div>
        <p class="hero-kicker">Revenue Management</p>
        <h1>订单营收驾驶舱</h1>
        <p class="hero-desc">按订单计划收入、收款状态和完成状态综合展示后台营收情况，方便客服、运营和财务一起跟进。</p>
      </div>
      <div class="hero-actions">
        <div class="window-switch">
          <button :class="{ active: activeWindow === 'weekly' }" @click="activeWindow = 'weekly'">近一周</button>
          <button :class="{ active: activeWindow === 'monthly' }" @click="activeWindow = 'monthly'">近一月</button>
        </div>
        <el-button plain @click="loadData">刷新数据</el-button>
      </div>
    </section>

    <section class="summary-grid">
      <article v-for="card in summaryCards" :key="card.label" class="summary-card">
        <span class="summary-icon">{{ card.icon }}</span>
        <div>
          <p>{{ card.label }}</p>
          <strong>{{ card.value }}</strong>
          <small>{{ card.tip }}</small>
        </div>
      </article>
    </section>

    <section class="main-grid">
      <el-card shadow="never" class="panel panel-wide">
        <template #header>
          <div class="panel-header">
            <div>
              <h2>收入趋势</h2>
              <span>{{ activeWindow === 'weekly' ? '近 7 天' : '近 30 天' }}计划营收与已到账走势</span>
            </div>
            <div class="trend-badge" :class="trendClass(activeTrendDirection)">
              {{ trendText(activeTrendDirection, activeTrendRate) }}
            </div>
          </div>
        </template>

        <div class="chart-shell">
          <svg class="trend-chart" viewBox="0 0 100 100" preserveAspectRatio="none">
            <line v-for="line in [20, 40, 60, 80]" :key="line" x1="0" :y1="line" x2="100" :y2="line" class="chart-grid" />
            <polyline :points="polylinePoints('plannedIncome')" class="chart-line line-planned" />
            <polyline :points="polylinePoints('actualIncome')" class="chart-line line-actual" />

            <circle
              v-for="(point, index) in trendPoints"
              :key="'planned-' + index"
              :cx="pointX(index)"
              :cy="pointY(point.plannedIncome)"
              r="1.5"
              class="chart-dot dot-planned"
            />
            <circle
              v-for="(point, index) in trendPoints"
              :key="'actual-' + index"
              :cx="pointX(index)"
              :cy="pointY(point.actualIncome)"
              r="1.4"
              class="chart-dot dot-actual"
            />
          </svg>

          <div class="chart-legend">
            <span><i class="legend-dot dot-planned"></i>计划营收</span>
            <span><i class="legend-dot dot-actual"></i>已到账</span>
          </div>
          <div class="chart-axis">
            <span v-for="point in trendPoints" :key="point.label">{{ point.label }}</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="panel">
        <template #header>
          <div class="panel-header">
            <div>
              <h2>订单状态分布</h2>
              <span>按已收款、部分收款、已对账、待收款聚合</span>
            </div>
          </div>
        </template>

        <div class="distribution-shell">
          <div class="donut" :style="{ background: distributionGradient }">
            <div class="donut-hole">
              <strong>{{ distributionTotal }}</strong>
              <span>有效订单</span>
            </div>
          </div>

          <div class="distribution-list">
            <div v-for="item in stats.statusDistribution" :key="item.label" class="distribution-item">
              <div class="distribution-title">
                <span class="distribution-color" :style="{ background: item.color }"></span>
                <span>{{ item.label }}</span>
              </div>
              <strong>{{ item.count }} 单</strong>
              <small>{{ formatCurrency(item.amount) }}</small>
            </div>
          </div>
        </div>
      </el-card>
    </section>

    <section class="secondary-grid">
      <el-card shadow="never" class="panel">
        <template #header>
          <div class="panel-header">
            <div>
              <h2>计划 vs 实际</h2>
              <span>本月计划订单总收入与已到账收入对比</span>
            </div>
          </div>
        </template>

        <div class="compare-metric">
          <div>
            <p>计划总收入</p>
            <strong>{{ formatCurrency(stats.planActual.plannedIncome) }}</strong>
          </div>
          <div>
            <p>实际已到账</p>
            <strong>{{ formatCurrency(stats.planActual.actualIncome) }}</strong>
          </div>
          <div>
            <p>差额</p>
            <strong class="warning-text">{{ formatCurrency(stats.planActual.gapIncome) }}</strong>
          </div>
        </div>

        <div class="compare-track">
          <div class="compare-rail"></div>
          <div class="compare-fill compare-plan"></div>
          <div class="compare-fill compare-actual" :style="{ width: completionRateWidth }"></div>
        </div>

        <div class="completion-panel">
          <div class="completion-value">{{ stats.planActual.completionRate || 0 }}%</div>
          <div class="completion-copy">
            <strong>到账完成率</strong>
            <span>当前按 `payStatus = PAID` 的订单金额统计已到账收入</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="panel">
        <template #header>
          <div class="panel-header">
            <div>
              <h2>未收款风险</h2>
              <span>已确认/已对账但未完成收款的订单</span>
            </div>
          </div>
        </template>

        <div v-if="stats.collectionRisks.length" class="risk-list">
          <div v-for="risk in stats.collectionRisks" :key="risk.id" class="risk-item">
            <div class="risk-top">
              <div>
                <strong>{{ risk.orderNo }}</strong>
                <p>{{ statusText(risk.status) }} · {{ payStatusText(risk.payStatus) }}</p>
              </div>
              <span class="risk-amount">{{ formatCurrency(risk.amount) }}</span>
            </div>
            <div class="risk-progress">
              <div class="risk-progress-fill" :style="{ width: `${risk.progress}%` }"></div>
            </div>
            <div class="risk-meta">
              <span>客户 ID：{{ risk.customerId || '-' }}</span>
              <span>收款进度：{{ risk.progress }}%</span>
              <span>挂账天数：{{ risk.overdueDays }} 天</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="当前没有高风险未收款订单" />
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi, type OrderRevenueStats, type OrderRevenueTrendPoint } from '@/utils/api/order'

const emptyStats = (): OrderRevenueStats => ({
  totalOrders: 0,
  activeOrders: 0,
  paidOrders: 0,
  pendingReceiptOrders: 0,
  totalPlannedIncome: 0,
  totalActualIncome: 0,
  weeklyTrendDirection: 'FLAT',
  weeklyChangeRate: 0,
  monthlyTrendDirection: 'FLAT',
  monthlyChangeRate: 0,
  weeklyTrend: [],
  monthlyTrend: [],
  statusDistribution: [],
  planActual: {
    plannedIncome: 0,
    actualIncome: 0,
    gapIncome: 0,
    completionRate: 0
  },
  collectionRisks: []
})

const loading = ref(false)
const activeWindow = ref<'weekly' | 'monthly'>('weekly')
const stats = ref<OrderRevenueStats>(emptyStats())

const loadData = async () => {
  loading.value = true
  try {
    const res = await orderApi.revenueStats()
    stats.value = res || emptyStats()
  } catch (error) {
    ElMessage.error('营收数据加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

const trendPoints = computed<OrderRevenueTrendPoint[]>(() =>
  activeWindow.value === 'weekly' ? stats.value.weeklyTrend || [] : stats.value.monthlyTrend || []
)

const maxTrendValue = computed(() => {
  const max = trendPoints.value.reduce((result, item) => {
    return Math.max(result, Number(item.plannedIncome || 0), Number(item.actualIncome || 0))
  }, 0)
  return max || 1
})

const activeTrendDirection = computed(() =>
  activeWindow.value === 'weekly' ? stats.value.weeklyTrendDirection : stats.value.monthlyTrendDirection
)

const activeTrendRate = computed(() =>
  activeWindow.value === 'weekly' ? stats.value.weeklyChangeRate : stats.value.monthlyChangeRate
)

const summaryCards = computed(() => [
  {
    label: '计划总营收',
    value: formatCurrency(stats.value.totalPlannedIncome),
    tip: `有效订单 ${stats.value.activeOrders} 单`,
    icon: '📈'
  },
  {
    label: '累计已到账',
    value: formatCurrency(stats.value.totalActualIncome),
    tip: `已收款 ${stats.value.paidOrders} 单`,
    icon: '💰'
  },
  {
    label: '待跟进收款',
    value: `${stats.value.pendingReceiptOrders} 单`,
    tip: `挂账金额 ${formatCurrency(stats.value.planActual.gapIncome)}`,
    icon: '🧾'
  },
  {
    label: '本月到账率',
    value: `${stats.value.planActual.completionRate || 0}%`,
    tip: '按已收款订单金额统计',
    icon: '🎯'
  }
])

const distributionTotal = computed(() =>
  stats.value.statusDistribution.reduce((sum, item) => sum + Number(item.count || 0), 0)
)

const distributionGradient = computed(() => {
  if (!stats.value.statusDistribution.length || !distributionTotal.value) {
    return 'conic-gradient(#d4d9e3 0deg 360deg)'
  }

  let currentDegree = 0
  const stops = stats.value.statusDistribution.map((item) => {
    const nextDegree = currentDegree + (Number(item.count || 0) / distributionTotal.value) * 360
    const stop = `${item.color} ${currentDegree}deg ${nextDegree}deg`
    currentDegree = nextDegree
    return stop
  })
  return `conic-gradient(${stops.join(', ')})`
})

const completionRateWidth = computed(() => `${Math.min(stats.value.planActual.completionRate || 0, 100)}%`)

const pointX = (index: number) => {
  const total = trendPoints.value.length
  if (total <= 1) {
    return 0
  }
  return Number(((index / (total - 1)) * 100).toFixed(2))
}

const pointY = (value: number) => {
  const max = maxTrendValue.value
  return Number((92 - (Number(value || 0) / max) * 74).toFixed(2))
}

const polylinePoints = (key: 'plannedIncome' | 'actualIncome') =>
  trendPoints.value.map((point, index) => `${pointX(index)},${pointY(Number(point[key] || 0))}`).join(' ')

const trendClass = (direction: string) => {
  return {
    up: direction === 'UP',
    down: direction === 'DOWN',
    flat: direction === 'FLAT'
  }
}

const trendText = (direction: string, rate: number) => {
  if (direction === 'UP') {
    return `上升 ${Math.abs(Number(rate || 0)).toFixed(2)}%`
  }
  if (direction === 'DOWN') {
    return `下降 ${Math.abs(Number(rate || 0)).toFixed(2)}%`
  }
  return '基本持平'
}

function formatCurrency(value: number) {
  const numericValue = Number(value || 0)
  return `¥${numericValue.toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  })}`
}

const statusText = (status: string) => {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    PENDING_CONFIRM: '待确认',
    CONFIRMED: '已确认',
    COMPLETED: '已对账',
    CANCELLED: '已取消'
  }
  return map[status] || status || '-'
}

const payStatusText = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: '待收款',
    PARTIAL: '部分收款',
    PAID: '已收款'
  }
  return map[status] || status || '-'
}
</script>

<style scoped>
.revenue-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.revenue-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 30px;
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(244, 162, 97, 0.24), transparent 28%),
    linear-gradient(135deg, var(--voyage-navy), #123253);
  color: var(--voyage-pearl);
  box-shadow: 0 22px 36px rgba(26, 54, 93, 0.2);
}

.hero-kicker {
  margin: 0 0 10px;
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(250, 248, 243, 0.7);
}

.revenue-hero h1 {
  margin: 0 0 10px;
  font-family: 'Playfair Display', serif;
  font-size: 34px;
}

.hero-desc {
  margin: 0;
  max-width: 720px;
  line-height: 1.7;
  color: rgba(250, 248, 243, 0.82);
}

.hero-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.window-switch {
  display: inline-flex;
  padding: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
}

.window-switch button {
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.76);
  padding: 10px 16px;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 600;
}

.window-switch button.active {
  background: white;
  color: var(--voyage-navy);
}

.summary-grid,
.main-grid,
.secondary-grid {
  display: grid;
  gap: 24px;
}

.summary-grid {
  grid-template-columns: repeat(4, 1fr);
}

.summary-card {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 16px;
  padding: 22px;
  border-radius: 22px;
  background: white;
  box-shadow: 0 18px 36px rgba(26, 54, 93, 0.08);
}

.summary-icon {
  width: 54px;
  height: 54px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(224, 122, 95, 0.18), rgba(244, 162, 97, 0.2));
  font-size: 24px;
}

.summary-card p,
.compare-metric p {
  margin: 0 0 8px;
  color: rgba(45, 55, 72, 0.62);
}

.summary-card strong,
.compare-metric strong,
.completion-value,
.distribution-item strong,
.risk-top strong {
  display: block;
  color: var(--voyage-navy);
}

.summary-card strong {
  font-size: 24px;
  margin-bottom: 8px;
}

.summary-card small {
  color: rgba(45, 55, 72, 0.54);
}

.main-grid {
  grid-template-columns: 1.65fr 1fr;
}

.secondary-grid {
  grid-template-columns: 1fr 1fr;
}

.panel {
  border: none;
  border-radius: 24px;
  box-shadow: 0 18px 36px rgba(26, 54, 93, 0.08);
}

.panel-wide {
  min-height: 420px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-header h2 {
  margin: 0 0 6px;
  color: var(--voyage-navy);
  font-size: 21px;
}

.panel-header span {
  color: rgba(45, 55, 72, 0.58);
  font-size: 13px;
}

.trend-badge {
  padding: 10px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
}

.trend-badge.up {
  background: rgba(42, 168, 118, 0.14);
  color: #1f8f62;
}

.trend-badge.down {
  background: rgba(231, 111, 81, 0.14);
  color: #d94841;
}

.trend-badge.flat {
  background: rgba(26, 54, 93, 0.08);
  color: var(--voyage-navy);
}

.chart-shell {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trend-chart {
  width: 100%;
  height: 280px;
  background: linear-gradient(180deg, rgba(212, 165, 116, 0.04), rgba(26, 54, 93, 0.02));
  border-radius: 20px;
}

.chart-grid {
  stroke: rgba(26, 54, 93, 0.08);
  stroke-width: 0.6;
}

.chart-line {
  fill: none;
  stroke-width: 2.5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.line-planned {
  stroke: #e76f51;
}

.line-actual {
  stroke: #1f8f62;
}

.dot-planned {
  fill: #e76f51;
}

.dot-actual {
  fill: #1f8f62;
}

.chart-legend,
.chart-axis,
.risk-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.chart-legend span,
.chart-axis span,
.completion-copy span,
.distribution-item small,
.risk-top p,
.risk-meta span {
  color: rgba(45, 55, 72, 0.58);
  font-size: 12px;
}

.legend-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
}

.distribution-shell {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 24px;
  align-items: center;
}

.donut {
  width: 190px;
  height: 190px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.donut-hole {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 1px rgba(26, 54, 93, 0.04);
}

.donut-hole strong {
  font-size: 34px;
  color: var(--voyage-navy);
}

.donut-hole span {
  color: rgba(45, 55, 72, 0.54);
  font-size: 13px;
}

.distribution-list {
  display: grid;
  gap: 14px;
}

.distribution-item {
  padding: 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.08), rgba(26, 54, 93, 0.03));
}

.distribution-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  color: var(--voyage-navy);
  font-weight: 600;
}

.distribution-color {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.compare-metric {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-bottom: 24px;
}

.compare-metric strong {
  font-size: 24px;
}

.warning-text,
.risk-amount {
  color: #d94841;
}

.compare-track {
  position: relative;
  height: 22px;
  margin-bottom: 22px;
}

.compare-rail,
.compare-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  border-radius: 999px;
}

.compare-rail {
  width: 100%;
  background: rgba(26, 54, 93, 0.08);
}

.compare-plan {
  width: 100%;
  background: rgba(231, 111, 81, 0.2);
}

.compare-actual {
  background: linear-gradient(90deg, #1f8f62, #2aa876);
  box-shadow: 0 10px 18px rgba(42, 168, 118, 0.18);
}

.completion-panel {
  display: flex;
  align-items: center;
  gap: 18px;
}

.completion-value {
  width: 92px;
  height: 92px;
  border-radius: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--voyage-navy), #1f4d75);
  color: white;
  font-size: 30px;
  box-shadow: 0 18px 28px rgba(26, 54, 93, 0.2);
}

.completion-copy strong {
  display: block;
  margin-bottom: 6px;
  color: var(--voyage-navy);
}

.risk-list {
  display: grid;
  gap: 16px;
}

.risk-item {
  padding: 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(231, 111, 81, 0.08), rgba(244, 162, 97, 0.08));
}

.risk-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.risk-top strong {
  font-size: 16px;
  margin-bottom: 4px;
}

.risk-top p {
  margin: 0;
}

.risk-progress {
  height: 10px;
  border-radius: 999px;
  overflow: hidden;
  background: rgba(26, 54, 93, 0.08);
  margin-bottom: 10px;
}

.risk-progress-fill {
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #f59f00, #e76f51);
}

@media (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-grid,
  .secondary-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .revenue-hero,
  .panel-header,
  .compare-metric,
  .distribution-shell,
  .completion-panel,
  .chart-legend,
  .chart-axis,
  .risk-top,
  .risk-meta {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    width: 100%;
    align-items: stretch;
  }

  .window-switch {
    width: 100%;
  }

  .window-switch button {
    flex: 1;
  }
}
</style>
