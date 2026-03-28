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
          <router-link to="/portal/submit-ticket" class="nav-link">定制需求</router-link>
          <button type="button" class="nav-link nav-button" @click="openSupport">联系客服</button>

          <template v-if="userInfo">
            <el-dropdown trigger="hover" placement="bottom-end" @command="handleCommand">
              <button type="button" class="avatar-entry">
                <span class="avatar-badge">{{ userInitial }}</span>
                <span class="avatar-meta">
                  <strong>{{ userInfo.realName || userInfo.username }}</strong>
                  <small>个人中心</small>
                </span>
                <span class="avatar-caret">▾</span>
              </button>

              <template #dropdown>
                <el-dropdown-menu class="portal-dropdown">
                  <div class="dropdown-summary">
                    <span class="dropdown-avatar">{{ userInitial }}</span>
                    <div>
                      <strong>{{ userInfo.realName || userInfo.username }}</strong>
                      <p>{{ userInfo.phone || userInfo.email || '站内联系已开启' }}</p>
                    </div>
                  </div>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="tickets">我的咨询</el-dropdown-item>
                  <el-dropdown-item command="support">联系客服</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <button type="button" class="support-fab" @click="openSupport">
      <span class="support-fab-icon">💬</span>
      <span class="support-fab-copy">
        <strong>联系客服</strong>
        <small>像淘宝一样直接会话</small>
      </span>
    </button>

    <PortalSupportDrawer v-model="supportVisible" />

    <footer class="portal-footer">
      <p>© 时拾纪旅行社 · 探索你的下一站</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PortalSupportDrawer from '@/components/portal/PortalSupportDrawer.vue'
import { clearPortalSession, getPortalUserInfo, portalUserEventName } from '@/utils/portalUser'

const route = useRoute()
const router = useRouter()
const supportVisible = ref(false)
const userInfo = ref(getPortalUserInfo())

const syncUserInfo = () => {
  userInfo.value = getPortalUserInfo()
}

const openSupport = () => {
  supportVisible.value = true
}

const handleSupportEvent = () => {
  supportVisible.value = true
}

watch(
  () => route.fullPath,
  () => {
    syncUserInfo()
  },
  { immediate: true }
)

onMounted(() => {
  window.addEventListener(portalUserEventName, syncUserInfo)
  window.addEventListener('portal-open-support', handleSupportEvent)
})

onUnmounted(() => {
  window.removeEventListener(portalUserEventName, syncUserInfo)
  window.removeEventListener('portal-open-support', handleSupportEvent)
})

const userInitial = computed(() => (userInfo.value?.realName || userInfo.value?.username || '游').slice(0, 1))

const logout = () => {
  clearPortalSession()
  supportVisible.value = false
  router.push('/portal/home')
}

const handleCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/portal/profile')
    return
  }
  if (command === 'tickets') {
    router.push('/portal/my-tickets')
    return
  }
  if (command === 'support') {
    openSupport()
    return
  }
  if (command === 'logout') {
    logout()
  }
}
</script>

<style scoped>
.portal-wrap {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(circle at top right, rgba(212, 165, 116, 0.14), transparent 22%),
    linear-gradient(180deg, #f7f4ed, #f4ede1);
}

.portal-header {
  background: linear-gradient(135deg, var(--voyage-navy), #0f2744);
  color: var(--voyage-pearl);
  padding: 14px 24px;
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
  gap: 24px;
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
  gap: 14px;
}

.nav-link,
.nav-button {
  color: var(--voyage-sand);
  text-decoration: none;
  font-weight: 600;
  padding: 8px 14px;
  border-radius: 999px;
  transition: all 0.2s;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
}

.nav-link:hover,
.nav-link.router-link-active,
.nav-button:hover {
  color: var(--voyage-pearl);
  background: rgba(212, 165, 116, 0.2);
}

.nav-login {
  color: var(--voyage-pearl);
}

.nav-register {
  background: var(--voyage-sunset);
  color: white;
}

.nav-register:hover {
  opacity: 0.92;
  background: var(--voyage-sunset);
}

.avatar-entry {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 10px 6px 6px;
  border: 1px solid rgba(212, 165, 116, 0.28);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: var(--voyage-pearl);
  cursor: pointer;
  min-width: 184px;
}

.avatar-entry:hover {
  border-color: rgba(224, 122, 95, 0.62);
  background: rgba(255, 255, 255, 0.12);
}

.avatar-badge,
.dropdown-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f4a261, #e76f51);
  color: white;
  font-weight: 700;
  box-shadow: 0 8px 18px rgba(224, 122, 95, 0.2);
}

.avatar-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
}

.avatar-meta strong {
  font-size: 14px;
  color: var(--voyage-pearl);
  max-width: 90px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-meta small,
.avatar-caret {
  color: rgba(250, 248, 243, 0.72);
}

.dropdown-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 14px 12px;
  margin-bottom: 8px;
  border-bottom: 1px solid rgba(26, 54, 93, 0.08);
}

.dropdown-summary strong {
  display: block;
  color: var(--voyage-navy);
  margin-bottom: 4px;
}

.dropdown-summary p {
  margin: 0;
  color: rgba(45, 55, 72, 0.62);
  font-size: 12px;
}

.portal-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 32px 24px 90px;
}

.support-fab {
  position: fixed;
  right: 24px;
  bottom: 28px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--voyage-sunset), #f4a261);
  color: white;
  box-shadow: 0 18px 34px rgba(224, 122, 95, 0.24);
  cursor: pointer;
  z-index: 110;
}

.support-fab:hover {
  transform: translateY(-2px);
}

.support-fab-icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.16);
  font-size: 22px;
}

.support-fab-copy {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.support-fab-copy strong {
  font-size: 14px;
}

.support-fab-copy small {
  color: rgba(255, 255, 255, 0.82);
}

.portal-footer {
  text-align: center;
  padding: 20px;
  color: var(--voyage-ink);
  font-size: 13px;
  opacity: 0.8;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 840px) {
  .header-inner {
    flex-direction: column;
    align-items: flex-start;
  }

  .portal-nav {
    width: 100%;
    flex-wrap: wrap;
  }

  .avatar-entry {
    min-width: auto;
  }
}

@media (max-width: 640px) {
  .portal-main {
    padding: 24px 16px 100px;
  }

  .support-fab {
    left: 16px;
    right: 16px;
    bottom: 16px;
    justify-content: center;
  }
}
</style>
