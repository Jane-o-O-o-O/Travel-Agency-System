<template>
  <div class="portal-page voyage-card">
    <div class="card-head">
      <span class="card-icon">🔐</span>
      <h1 class="card-title">用户登录</h1>
      <p class="card-desc">登录后即可提交工单、查看进度</p>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" class="portal-form" @submit.prevent="handleLogin">
      <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password prefix-icon="Lock" @keyup.enter="handleLogin" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form-item>
    </el-form>
    <p class="switch-tip">
      还没有账号？<router-link to="/portal/register">立即注册</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { portalAuthApi } from '@/utils/api/portal'
import { setPortalSession } from '@/utils/portalUser'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate().catch(() => {})
  loading.value = true
  try {
    const res: any = await portalAuthApi.login(form)
    if (res && res.token) {
      setPortalSession(res.token, res.userInfo || {})
      ElMessage.success('登录成功')
      router.push('/portal/my-tickets')
    }
  } catch {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.portal-page {
  max-width: 420px;
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
.card-desc { color: var(--voyage-ink); font-size: 14px; margin: 0; opacity: 0.9; }
.portal-form :deep(.el-input__wrapper) { border-radius: 8px; }
.submit-btn { width: 100%; height: 44px; font-size: 16px; }
.switch-tip { text-align: center; margin-top: 20px; font-size: 14px; color: var(--voyage-ink); }
.switch-tip a { color: var(--voyage-sunset); text-decoration: none; }
.switch-tip a:hover { text-decoration: underline; }
</style>
