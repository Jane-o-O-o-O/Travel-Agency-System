<template>
  <div id="app" class="voyage-portal">
    <!-- Vintage Paper Texture Overlay -->
    <div class="paper-texture"></div>
    
    <el-container v-if="!hideLayout" class="main-container">
      <!-- Sidebar Navigation: Vintage Travel Guide Style -->
      <el-aside width="280px" class="expedition-sidebar">
        <div class="sidebar-header">
          <div class="compass-logo">
            <svg viewBox="0 0 100 100" class="compass">
              <circle cx="50" cy="50" r="45" class="compass-ring"/>
              <circle cx="50" cy="50" r="35" class="compass-inner"/>
              <polygon points="50,20 55,50 50,55 45,50" class="compass-needle"/>
              <text x="50" y="85" class="compass-text">时拾纪</text>
            </svg>
          </div>
          <h1 class="voyage-title">VOYAGE CONSOLE</h1>
          <p class="voyage-subtitle">探险管理中心</p>
        </div>

        <nav class="expedition-nav">
          <router-link 
            v-for="route in routes" 
            :key="route.path"
            :to="route.path"
            class="nav-stamp"
            :class="{ active: $route.path === route.path }"
          >
            <span class="stamp-icon">{{ route.icon }}</span>
            <span class="stamp-label">{{ route.label }}</span>
            <span class="stamp-border"></span>
          </router-link>
        </nav>

        <div class="sidebar-footer">
          <div class="user-badge">
            <span class="badge-avatar">{{ userInitial }}</span>
            <span class="badge-name">{{ userInfo?.realName || '旅行者' }}</span>
          </div>
          <button @click="logout" class="departure-btn">
            <span>✈</span> 登出
          </button>
        </div>
      </el-aside>

      <!-- Main Content Area -->
      <el-container class="content-container">
        <el-header class="expedition-header">
          <div class="header-ornament"></div>
          <div class="route-breadcrumb">
            <span class="breadcrumb-icon">📍</span>
            <span class="breadcrumb-text">{{ currentRouteTitle }}</span>
          </div>
          <div class="header-actions">
            <div class="time-badge">
              <span class="time-icon">🕐</span>
              <span class="time-text">{{ currentTime }}</span>
            </div>
          </div>
        </el-header>

        <el-main class="voyage-content">
          <transition name="page-transition" mode="out-in">
            <router-view />
          </transition>
        </el-main>
      </el-container>
    </el-container>
    
    <!-- Login Page (No Layout) -->
    <div v-else class="login-wrapper">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const currentTime = ref('')

const routes = [
  { path: '/dashboard', label: '控制台', icon: '🗺️' },
  { path: '/customers', label: '旅客档案', icon: '👥' },
  { path: '/templates', label: '航线模板', icon: '✈️' },
  { path: '/products', label: '探险产品', icon: '🎒' },
  { path: '/orders', label: '订单追踪', icon: '📋' },
  { path: '/resources', label: '资源调度', icon: '🚗' },
  { path: '/tickets', label: '服务工单', icon: '🎫' }
]

const hideLayout = computed(() => route.path === '/login')

const currentRouteTitle = computed(() => {
  const current = routes.find(r => r.path === route.path)
  return current?.label || '探险之旅'
})

const userInfo = computed(() => {
  const stored = localStorage.getItem('userInfo')
  return stored ? JSON.parse(stored) : null
})

const userInitial = computed(() => {
  return userInfo.value?.realName?.charAt(0) || 'T'
})

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

let timeInterval: number

onMounted(() => {
  updateTime()
  timeInterval = window.setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Karla:wght@300;400;600&family=Space+Mono:wght@400;700&display=swap');

:root {
  --voyage-navy: #1a365d;
  --voyage-sand: #d4a574;
  --voyage-sunset: #e07a5f;
  --voyage-parchment: #f7f4ed;
  --voyage-ink: #2d3748;
  --voyage-pearl: #faf8f3;
  --voyage-shadow: rgba(26, 54, 93, 0.15);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Karla', sans-serif;
  background: var(--voyage-parchment);
  color: var(--voyage-ink);
  overflow: hidden;
}

#app {
  width: 100%;
  height: 100vh;
  position: relative;
}

.voyage-portal {
  width: 100%;
  height: 100%;
  position: relative;
}

.paper-texture {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,0.015) 2px, rgba(0,0,0,0.015) 4px),
    repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(0,0,0,0.015) 2px, rgba(0,0,0,0.015) 4px);
  pointer-events: none;
  z-index: 1;
  opacity: 0.5;
}

.main-container {
  height: 100%;
  position: relative;
  z-index: 2;
}

/* ===== SIDEBAR: EXPEDITION STYLE ===== */
.expedition-sidebar {
  background: linear-gradient(165deg, var(--voyage-navy) 0%, #0f2744 100%);
  color: var(--voyage-pearl);
  box-shadow: 4px 0 24px var(--voyage-shadow);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.expedition-sidebar::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(212, 165, 116, 0.1) 0%, transparent 70%);
  animation: rotate-gradient 20s linear infinite;
}

@keyframes rotate-gradient {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.sidebar-header {
  padding: 32px 24px;
  text-align: center;
  border-bottom: 2px solid rgba(212, 165, 116, 0.2);
  position: relative;
  z-index: 1;
}

.compass-logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
}

.compass {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 12px rgba(0,0,0,0.3));
  animation: gentle-spin 30s linear infinite;
}

@keyframes gentle-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.compass-ring {
  fill: none;
  stroke: var(--voyage-sand);
  stroke-width: 2;
}

.compass-inner {
  fill: none;
  stroke: var(--voyage-pearl);
  stroke-width: 1;
  stroke-dasharray: 5, 5;
}

.compass-needle {
  fill: var(--voyage-sunset);
  transform-origin: center;
}

.compass-text {
  fill: var(--voyage-pearl);
  font-size: 12px;
  font-family: 'Playfair Display', serif;
  text-anchor: middle;
  font-weight: 600;
}

.voyage-title {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0 0 8px;
  color: var(--voyage-pearl);
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.voyage-subtitle {
  font-family: 'Space Mono', monospace;
  font-size: 11px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--voyage-sand);
  opacity: 0.9;
}

/* Navigation: Passport Stamp Style */
.expedition-nav {
  flex: 1;
  padding: 24px 16px;
  overflow-y: auto;
  position: relative;
  z-index: 1;
}

.expedition-nav::-webkit-scrollbar {
  width: 4px;
}

.expedition-nav::-webkit-scrollbar-thumb {
  background: rgba(212, 165, 116, 0.3);
  border-radius: 2px;
}

.nav-stamp {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  margin: 8px 0;
  text-decoration: none;
  color: var(--voyage-pearl);
  border-radius: 8px;
  border: 2px dashed transparent;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.nav-stamp::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) rotate(-5deg);
  width: 0;
  height: 0;
  background: radial-gradient(circle, var(--voyage-sunset), transparent);
  opacity: 0;
  transition: all 0.4s ease;
  border-radius: 50%;
}

.nav-stamp:hover::before {
  width: 100%;
  height: 100%;
  opacity: 0.1;
}

.nav-stamp:hover {
  border-color: var(--voyage-sand);
  transform: translateX(4px);
}

.nav-stamp.active {
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.2), rgba(224, 122, 95, 0.2));
  border-color: var(--voyage-sand);
  border-style: solid;
  box-shadow: inset 0 0 20px rgba(212, 165, 116, 0.3);
}

.stamp-icon {
  font-size: 22px;
  margin-right: 12px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.stamp-label {
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.sidebar-footer {
  padding: 20px;
  border-top: 2px solid rgba(212, 165, 116, 0.2);
  position: relative;
  z-index: 1;
}

.user-badge {
  display: flex;
  align-items: center;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  margin-bottom: 12px;
}

.badge-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--voyage-sand);
  color: var(--voyage-navy);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  margin-right: 12px;
  font-family: 'Playfair Display', serif;
}

.badge-name {
  font-size: 14px;
  font-weight: 500;
}

.departure-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, var(--voyage-sunset), #c5614d);
  border: none;
  border-radius: 6px;
  color: white;
  font-family: 'Karla', sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(224, 122, 95, 0.3);
}

.departure-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(224, 122, 95, 0.4);
}

.departure-btn span {
  margin-right: 8px;
}

/* ===== HEADER: ELEGANT BAR ===== */
.expedition-header {
  background: linear-gradient(to right, var(--voyage-pearl), #ffffff);
  border-bottom: 3px solid var(--voyage-sand);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  box-shadow: 0 2px 8px var(--voyage-shadow);
  position: relative;
}

.header-ornament {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: repeating-linear-gradient(
    90deg,
    var(--voyage-sand) 0px,
    var(--voyage-sand) 10px,
    transparent 10px,
    transparent 20px
  );
  opacity: 0.3;
}

.route-breadcrumb {
  display: flex;
  align-items: center;
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 600;
  color: var(--voyage-navy);
}

.breadcrumb-icon {
  margin-right: 12px;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.time-badge {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background: var(--voyage-parchment);
  border: 1px solid var(--voyage-sand);
  border-radius: 20px;
  font-family: 'Space Mono', monospace;
  box-shadow: 0 2px 4px var(--voyage-shadow);
}

.time-icon {
  margin-right: 8px;
}

.time-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--voyage-ink);
}

/* ===== CONTENT AREA ===== */
.content-container {
  background: var(--voyage-parchment);
}

.voyage-content {
  padding: 32px;
  overflow-y: auto;
  height: calc(100vh - 64px);
}

.voyage-content::-webkit-scrollbar {
  width: 8px;
}

.voyage-content::-webkit-scrollbar-thumb {
  background: var(--voyage-sand);
  border-radius: 4px;
}

/* Page Transitions */
.page-transition-enter-active {
  animation: pageEnter 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-transition-leave-active {
  animation: pageLeave 0.3s cubic-bezier(0.4, 0, 1, 1);
}

@keyframes pageEnter {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes pageLeave {
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
    transform: translateY(-10px);
  }
}

/* Login Wrapper */
.login-wrapper {
  width: 100%;
  height: 100%;
}
</style>
