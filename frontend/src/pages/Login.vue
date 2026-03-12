<template>
  <div class="voyage-login">
    <!-- Animated Background -->
    <div class="journey-bg">
      <div class="floating-element plane">✈</div>
      <div class="floating-element compass">🧭</div>
      <div class="floating-element map">🗺️</div>
      <div class="floating-element globe">🌍</div>
    </div>

    <!-- Login Card -->
    <div class="passport-card">
      <div class="passport-cover">
        <div class="emblem">
          <svg viewBox="0 0 100 100" class="emblem-graphic">
            <circle cx="50" cy="50" r="40" class="emblem-ring"/>
            <circle cx="50" cy="50" r="30" class="emblem-inner"/>
            <polygon points="50,25 55,50 50,53 45,50" class="emblem-arrow"/>
          </svg>
        </div>
        <h1 class="login-title">旅行社管理系统</h1>
        <p class="login-subtitle">时拾纪官方平台</p>
        <div class="title-ornament"></div>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-field">
          <label class="field-label">
            <span class="label-icon">👤</span>
            <span class="label-text">旅行者账号</span>
          </label>
          <input 
            v-model="form.username"
            type="text"
            placeholder="输入您的用户名"
            class="voyage-input"
            required
          />
        </div>

        <div class="form-field">
          <label class="field-label">
            <span class="label-icon">🔐</span>
            <span class="label-text">通行证密码</span>
          </label>
          <input 
            v-model="form.password"
            type="password"
            placeholder="输入您的密码"
            class="voyage-input"
            @keyup.enter="handleLogin"
            required
          />
        </div>

        <button 
          type="submit" 
          class="embark-btn"
          :disabled="loading"
        >
          <span class="btn-icon">🚀</span>
          <span class="btn-text">{{ loading ? '验证中...' : '登 录' }}</span>
          <span class="btn-shine"></span>
        </button>

        <router-link to="/portal/register" class="register-btn">
          <span class="btn-icon">✨</span>
          <span class="btn-text">我是用户，去用户端注册</span>
        </router-link>

        <div class="welcome-note">
          <span class="note-icon">💡</span>
          <span class="note-text">默认管理员: admin / admin123 · 用户请访问</span>
          <router-link to="/portal/home" class="portal-link">用户端入口</router-link>
        </div>
      </form>

      <!-- Decorative Stamps -->
      <div class="passport-stamps">
        <div class="stamp stamp-1">已验证</div>
        <div class="stamp stamp-2">安全</div>
      </div>
    </div>

    <!-- Footer Credits -->
    <div class="journey-footer">
      <p>©2026 时拾纪旅行社 保留所有权利</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import apiClient from '@/utils/api'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: 'admin',
  password: 'admin123'
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const response = await apiClient.post('/auth/login', form.value)
    
    localStorage.setItem('token', response.token || 'demo-token')
    localStorage.setItem('userInfo', JSON.stringify(response.user || { username: form.value.username, realName: '管理员' }))
    
    ElMessage.success({
      message: '登录成功！欢迎回来 🎉',
      duration: 2000
    })
    
    setTimeout(() => {
      router.push('/dashboard')
    }, 500)
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.voyage-login {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #1a365d 0%, #2d4a6f 50%, #1a365d 100%);
  overflow: hidden;
}

.journey-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.floating-element {
  position: absolute;
  font-size: 48px;
  opacity: 0.1;
  animation: float 20s infinite ease-in-out;
}

.plane {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
  animation-duration: 25s;
}

.compass {
  top: 70%;
  right: 15%;
  animation-delay: 5s;
  animation-duration: 30s;
}

.map {
  bottom: 20%;
  left: 20%;
  animation-delay: 10s;
  animation-duration: 35s;
}

.globe {
  top: 40%;
  right: 25%;
  animation-delay: 15s;
  animation-duration: 28s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(20px, -30px) rotate(5deg);
  }
  50% {
    transform: translate(-20px, 20px) rotate(-5deg);
  }
  75% {
    transform: translate(30px, -10px) rotate(3deg);
  }
}

.passport-card {
  width: 480px;
  background: #f7f4ed;
  border-radius: 16px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(212, 165, 116, 0.3),
    inset 0 0 40px rgba(212, 165, 116, 0.05);
  position: relative;
  z-index: 10;
  overflow: hidden;
  animation: cardEntry 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes cardEntry {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.passport-cover {
  background: linear-gradient(165deg, #1a365d, #0f2744);
  padding: 48px 40px;
  position: relative;
  text-align: center;
  border-bottom: 4px solid #d4a574;
}

.passport-cover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 10px,
    rgba(212, 165, 116, 0.03) 10px,
    rgba(212, 165, 116, 0.03) 20px
  );
  pointer-events: none;
}

.emblem {
  width: 100px;
  height: 100px;
  margin: 0 auto 24px;
  position: relative;
}

.emblem-graphic {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 16px rgba(224, 122, 95, 0.4));
  animation: emblemPulse 3s ease-in-out infinite;
}

@keyframes emblemPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.emblem-ring {
  fill: none;
  stroke: #d4a574;
  stroke-width: 2;
}

.emblem-inner {
  fill: none;
  stroke: #e07a5f;
  stroke-width: 1.5;
  stroke-dasharray: 5, 5;
  animation: dashRotate 20s linear infinite;
}

@keyframes dashRotate {
  to {
    stroke-dashoffset: -200;
  }
}

.emblem-arrow {
  fill: #e07a5f;
}

.login-title {
  font-family: 'Playfair Display', serif;
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 3px;
  margin: 0 0 12px;
  color: #f7f4ed;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5);
}

.login-subtitle {
  font-family: 'Space Mono', monospace;
  font-size: 13px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: #d4a574;
}

.title-ornament {
  width: 120px;
  height: 3px;
  background: linear-gradient(to right, transparent, #d4a574, transparent);
  margin: 20px auto 0;
}

.login-form {
  padding: 40px;
}

.form-field {
  margin-bottom: 24px;
}

.field-label {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #1a365d;
}

.label-icon {
  margin-right: 8px;
  font-size: 18px;
}

.voyage-input {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #d4a574;
  border-radius: 8px;
  font-family: 'Karla', sans-serif;
  font-size: 15px;
  background: white;
  color: #2d3748;
  transition: all 0.3s ease;
  outline: none;
}

.voyage-input:focus {
  border-color: #e07a5f;
  box-shadow: 0 0 0 3px rgba(224, 122, 95, 0.1);
  transform: translateY(-2px);
}

.voyage-input::placeholder {
  color: #a0aec0;
  font-style: italic;
}

.embark-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #e07a5f, #c5614d);
  border: none;
  border-radius: 8px;
  color: white;
  font-family: 'Karla', sans-serif;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(224, 122, 95, 0.3);
  margin-top: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.embark-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(224, 122, 95, 0.4);
}

.embark-btn:active:not(:disabled) {
  transform: translateY(-1px);
}

.embark-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.embark-btn:hover .btn-shine {
  left: 100%;
}

.btn-icon {
  font-size: 20px;
}

.register-btn {
  width: 100%;
  padding: 14px;
  background: transparent;
  border: 2px solid var(--voyage-sand);
  border-radius: 8px;
  color: var(--voyage-navy);
  font-family: 'Karla', sans-serif;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  text-decoration: none;
  box-sizing: border-box;
}

.register-btn:hover {
  background: var(--voyage-sand);
  color: white;
  border-color: var(--voyage-sand);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(212, 165, 116, 0.3);
}

.welcome-note {
  margin-top: 24px;
  padding: 14px;
  background: rgba(212, 165, 116, 0.1);
  border-left: 3px solid #d4a574;
  border-radius: 4px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px 10px;
  font-size: 13px;
  color: #2d3748;
}

.note-icon {
  font-size: 16px;
}

.portal-link {
  color: var(--voyage-sunset);
  font-weight: 600;
  text-decoration: none;
}
.portal-link:hover {
  text-decoration: underline;
}

.passport-stamps {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.stamp {
  position: absolute;
  font-family: 'Space Mono', monospace;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 2px;
  padding: 8px 16px;
  border: 3px dashed;
  border-radius: 4px;
  transform: rotate(-15deg);
  opacity: 0.08;
}

.stamp-1 {
  top: 30px;
  right: 30px;
  color: #e07a5f;
  border-color: #e07a5f;
}

.stamp-2 {
  bottom: 40px;
  left: 40px;
  color: #d4a574;
  border-color: #d4a574;
  transform: rotate(12deg);
}

.journey-footer {
  position: absolute;
  bottom: 20px;
  width: 100%;
  text-align: center;
  font-family: 'Space Mono', monospace;
  font-size: 12px;
  color: #d4a574;
  opacity: 0.7;
}

/* Register Modal */
.register-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(26, 54, 93, 0.8);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.register-card {
  width: 90%;
  max-width: 520px;
  max-height: 90vh;
  overflow-y: auto;
  background: #f7f4ed;
  border-radius: 16px;
  border: 3px solid var(--voyage-sand);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  background: linear-gradient(165deg, var(--voyage-navy), #0f2744);
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 3px solid var(--voyage-sand);
}

.modal-title {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  color: var(--voyage-pearl);
  margin: 0;
}

.close-btn {
  width: 36px;
  height: 36px;
  background: transparent;
  border: 2px solid var(--voyage-sand);
  border-radius: 50%;
  color: var(--voyage-sand);
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.close-btn:hover {
  background: var(--voyage-sunset);
  border-color: var(--voyage-sunset);
  color: white;
  transform: rotate(90deg);
}

.register-form {
  padding: 32px;
}
</style>
