<template>
  <div class="portal-page voyage-card">
    <div class="card-head">
      <span class="card-icon">✨</span>
      <h1 class="card-title">用户注册</h1>
      <p class="card-desc">注册后即可使用工单服务</p>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" class="portal-form" @submit.prevent="handleRegister">
      <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="用户名" size="large" />
      </el-form-item>
      <el-form-item prop="realName">
        <el-input v-model="form.realName" placeholder="真实姓名" size="large" />
      </el-form-item>
      <el-form-item prop="phone">
        <el-input v-model="form.phone" placeholder="手机号" size="large" />
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="form.email" placeholder="邮箱" size="large" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" size="large" show-password />
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleRegister">
          注 册
        </el-button>
      </el-form-item>
    </el-form>
    <p class="switch-tip">
      已有账号？<router-link to="/portal/login">去登录</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { portalAuthApi } from '@/utils/api/portal'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: (_: any, v: string, cb: (e?: Error) => void) => (v === form.password ? cb() : cb(new Error('两次密码不一致'))) }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate().catch(() => {})
  loading.value = true
  try {
    await portalAuthApi.register({
      ...form,
      confirmPassword: form.confirmPassword
    })
    ElMessage.success('注册成功，请登录')
    router.push('/portal/login')
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
</style>
