<template>
  <div class="portal-page wide">
    <div class="card-head">
      <span class="card-icon">📋</span>
      <h1 class="card-title">提交出行意向</h1>
      <p class="card-desc">填写意向主题、人数与期望日期，我们会尽快与您联系</p>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" class="portal-form submit-form" @submit.prevent="submit">
      <el-form-item label="意向主题" prop="theme">
        <el-select v-model="form.theme" placeholder="请选择" style="width:100%">
          <el-option label="非遗文化" value="非遗" />
          <el-option label="亲子度假" value="亲子" />
          <el-option label="摄影采风" value="摄影" />
          <el-option label="研学旅行" value="研学" />
          <el-option label="海岛度假" value="海岛" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="出行人数" prop="peopleCount">
        <el-input-number v-model="form.peopleCount" :min="1" :max="99" style="width:100%" />
      </el-form-item>
      <el-form-item label="出行天数" prop="days">
        <el-input-number v-model="form.days" :min="1" :max="30" style="width:100%" />
      </el-form-item>
      <el-form-item label="期望日期" prop="expectedDate">
        <el-date-picker
          v-model="form.expectedDate"
          type="date"
          placeholder="选择期望出发日期"
          value-format="YYYY-MM-DD"
          style="width:100%"
        />
      </el-form-item>
      <el-form-item label="特殊要求" prop="specialRequirement">
        <el-input v-model="form.specialRequirement" type="textarea" :rows="4" placeholder="例如：想体验的活动、饮食偏好、住宿要求等" />
      </el-form-item>
      <el-form-item label="联系方式" prop="contactInfo">
        <el-input v-model="form.contactInfo" placeholder="手机号或微信号，便于我们联系您" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="submit">
          提交工单
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { portalTicketApi } from '@/utils/api/portal'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  theme: '',
  peopleCount: 1,
  days: 3,
  expectedDate: '',
  specialRequirement: '',
  contactInfo: ''
})
const rules: FormRules = {
  theme: [{ required: true, message: '请选择意向主题', trigger: 'change' }],
  peopleCount: [{ required: true, message: '请填写出行人数', trigger: 'blur' }],
  days: [{ required: true, message: '请填写出行天数', trigger: 'blur' }],
  contactInfo: [{ required: true, message: '请填写联系方式', trigger: 'blur' }]
}

const submit = async () => {
  if (!formRef.value) return
  await formRef.value.validate().catch(() => {})
  loading.value = true
  try {
    await portalTicketApi.submit({
      theme: form.theme,
      peopleCount: form.peopleCount,
      days: form.days,
      expectedDate: form.expectedDate || undefined,
      specialRequirement: form.specialRequirement || undefined,
      contactInfo: form.contactInfo
    })
    ElMessage.success('提交成功，我们会在工单中与您沟通')
    router.push('/portal/my-tickets')
  } catch {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.portal-page.wide { max-width: 560px; margin: 0 auto; }
.portal-page {
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
.submit-form :deep(.el-form-item__label) { color: var(--voyage-navy); }
.submit-btn { width: 100%; height: 44px; font-size: 16px; }
</style>
