<template>
  <div class="portal-wrap">
    <header class="portal-header">
      <div class="header-inner">
        <router-link to="/portal/home" class="logo-link">
          <span class="logo-icon">🧭</span>
          <span class="logo-text">时拾纪旅行</span>
        </router-link>
        <nav class="portal-nav">
          <router-link to="/portal/home" class="nav-link">首页</router-link>
          <router-link to="/portal/submit-ticket" class="nav-link">提交工单</router-link>
          <router-link to="/portal/my-tickets" class="nav-link">我的工单</router-link>
          <template v-if="userInfo">
            <span class="user-name">{{ userInfo.realName || userInfo.username }}</span>
            <button type="button" class="logout-btn" @click="logout">退出登录</button>
          </template>
          <template v-else>
            <router-link to="/portal/login" class="nav-link nav-login">登录</router-link>
            <router-link to="/portal/register" class="nav-link nav-register">注册</router-link>
          </template>
        </nav>
      </div>
    </header>
    <main class="portal-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <footer class="portal-footer">
      <p>© 时拾纪旅行社 · 探索你的下一站</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
// 依赖 route.path，登录后跳转时重新读取 localStorage，顶栏才能显示用户与退出
const userInfo = computed(() => {
  void route.path
  const s = localStorage.getItem('portalUserInfo')
  return s ? JSON.parse(s) : null
})

const logout = () => {
  localStorage.removeItem('portalToken')
  localStorage.removeItem('portalUserInfo')
  router.push('/portal/home')
}
</script>

<style scoped>
.portal-wrap {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--voyage-parchment);
}
.portal-header {
  background: linear-gradient(135deg, var(--voyage-navy), #0f2744);
  color: var(--voyage-pearl);
  padding: 12px 24px;
  box-shadow: 0 4px 16px var(--voyage-shadow);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: var(--voyage-pearl);
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 700;
}
.logo-icon {
  font-size: 28px;
}
.portal-nav {
  display: flex;
  align-items: center;
  gap: 24px;
}
.nav-link {
  color: var(--voyage-sand);
  text-decoration: none;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.2s;
}
.nav-link:hover,
.nav-link.router-link-active {
  color: var(--voyage-pearl);
  background: rgba(212, 165, 116, 0.2);
}
.nav-login { color: var(--voyage-pearl); }
.nav-register {
  background: var(--voyage-sunset);
  color: white;
}
.nav-register:hover { opacity: 0.9; }
.user-name {
  font-size: 14px;
  color: var(--voyage-sand);
}
.logout-btn {
  background: transparent;
  border: 1px solid var(--voyage-sand);
  color: var(--voyage-sand);
  padding: 6px 14px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}
.logout-btn:hover {
  background: rgba(212, 165, 116, 0.2);
}
.portal-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 32px 24px;
}
.portal-footer {
  text-align: center;
  padding: 20px;
  color: var(--voyage-ink);
  font-size: 13px;
  opacity: 0.8;
}
.fade-enter-active,
.fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }
</style>
