<template>
  <div class="profile-page">
    <section class="profile-hero">
      <div class="hero-avatar">{{ userInitial }}</div>
      <div class="hero-copy">
        <p class="hero-kicker">个人中心</p>
        <h1>{{ profile.realName || profile.username || '旅行用户' }}</h1>
        <p>在这里维护你的基础资料，客服沟通时会优先使用这里的联系方式。</p>
      </div>
      <div class="hero-actions">
        <el-button plain @click="goSupport">联系客服</el-button>
        <router-link to="/portal/my-tickets" class="ghost-link">查看我的咨询</router-link>
      </div>
    </section>

    <section class="profile-grid" v-loading="loading">
      <el-card shadow="never" class="profile-card">
        <template #header>
          <div class="card-header">
            <h2>基础资料</h2>
            <span>用于展示头像信息和客服联系信息</span>
          </div>
        </template>

        <el-form :model="profile" label-position="top" class="profile-form">
          <el-form-item label="用户名">
            <el-input :model-value="profile.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="profile.realName" placeholder="请输入你的真实姓名" maxlength="20" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profile.phone" placeholder="建议填写，便于客服回访" maxlength="20" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profile.email" placeholder="example@travel.com" maxlength="50" />
          </el-form-item>
          <div class="form-actions">
            <el-button @click="loadProfile">重置</el-button>
            <el-button type="primary" :loading="saving" @click="saveProfile">保存资料</el-button>
          </div>
        </el-form>
      </el-card>

      <el-card shadow="never" class="profile-card">
        <template #header>
          <div class="card-header">
            <h2>账号概览</h2>
            <span>头像下拉菜单会直接读取这里的资料</span>
          </div>
        </template>

        <div class="summary-list">
          <div class="summary-item">
            <span>显示名称</span>
            <strong>{{ profile.realName || profile.username || '-' }}</strong>
          </div>
          <div class="summary-item">
            <span>账号标识</span>
            <strong>{{ profile.username || '-' }}</strong>
          </div>
          <div class="summary-item">
            <span>客服联系手机号</span>
            <strong>{{ profile.phone || '未填写' }}</strong>
          </div>
          <div class="summary-item">
            <span>客服联系邮箱</span>
            <strong>{{ profile.email || '未填写' }}</strong>
          </div>
        </div>

        <div class="profile-tip">
          <p>头像为系统默认头像，显示你的姓名首字。</p>
          <p>如果后续要支持上传头像，只需要在门户用户表新增 `avatar_url` 字段并在这里接入即可。</p>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { portalAuthApi, type PortalUserProfile } from '@/utils/api/portal'
import { getPortalUserInfo, updatePortalUserInfo } from '@/utils/portalUser'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const localUser = getPortalUserInfo()
const profile = reactive<PortalUserProfile>({
  id: localUser?.id,
  username: localUser?.username || '',
  realName: localUser?.realName || '',
  phone: localUser?.phone || '',
  email: localUser?.email || ''
})

const userInitial = computed(() => (profile.realName || profile.username || '游').slice(0, 1))

const loadProfile = async () => {
  loading.value = true
  try {
    const res: any = await portalAuthApi.profile()
    profile.id = res.id
    profile.username = res.username
    profile.realName = res.realName || ''
    profile.phone = res.phone || ''
    profile.email = res.email || ''
    updatePortalUserInfo(res)
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    const res: any = await portalAuthApi.updateProfile({
      realName: profile.realName || '',
      phone: profile.phone || '',
      email: profile.email || ''
    })
    profile.id = res.id
    profile.username = res.username
    profile.realName = res.realName || ''
    profile.phone = res.phone || ''
    profile.email = res.email || ''
    updatePortalUserInfo(res)
    ElMessage.success('个人资料已更新')
  } finally {
    saving.value = false
  }
}

const goSupport = () => {
  window.dispatchEvent(new CustomEvent('portal-open-support'))
  router.push('/portal/home')
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-hero {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 24px;
  padding: 28px;
  background:
    radial-gradient(circle at top left, rgba(212, 165, 116, 0.18), transparent 28%),
    linear-gradient(135deg, var(--voyage-navy), #102b4a);
  color: var(--voyage-pearl);
  border-radius: 24px;
  box-shadow: 0 18px 36px rgba(26, 54, 93, 0.22);
}

.hero-avatar {
  width: 84px;
  height: 84px;
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  font-weight: 700;
  background: linear-gradient(135deg, #f4a261, #e76f51);
  color: white;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.28);
}

.hero-kicker {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(250, 248, 243, 0.75);
}

.hero-copy h1 {
  margin: 0 0 8px;
  font-family: 'Playfair Display', serif;
  font-size: 30px;
}

.hero-copy p {
  margin: 0;
  line-height: 1.7;
  color: rgba(250, 248, 243, 0.84);
}

.hero-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
}

.ghost-link {
  color: var(--voyage-pearl);
  text-decoration: none;
  font-size: 14px;
  text-align: center;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 24px;
}

.profile-card {
  border: none;
  border-radius: 24px;
  box-shadow: 0 18px 36px rgba(26, 54, 93, 0.08);
}

.card-header h2 {
  margin: 0 0 6px;
  font-size: 20px;
  color: var(--voyage-navy);
}

.card-header span {
  color: rgba(45, 55, 72, 0.64);
  font-size: 13px;
}

.profile-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--voyage-navy);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.summary-list {
  display: grid;
  gap: 14px;
}

.summary-item {
  padding: 16px 18px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.08), rgba(26, 54, 93, 0.03));
}

.summary-item span {
  display: block;
  color: rgba(45, 55, 72, 0.6);
  font-size: 13px;
  margin-bottom: 8px;
}

.summary-item strong {
  color: var(--voyage-navy);
  font-size: 16px;
}

.profile-tip {
  margin-top: 18px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(26, 54, 93, 0.04);
  color: rgba(45, 55, 72, 0.76);
  line-height: 1.75;
}

.profile-tip p {
  margin: 0;
}

.profile-tip p + p {
  margin-top: 10px;
}

@media (max-width: 900px) {
  .profile-hero,
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    flex-direction: row;
    justify-content: flex-start;
  }
}
</style>
