import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 30000
})

// 请求拦截器：门户接口用 portalToken，管理端用 token
apiClient.interceptors.request.use(
  (config) => {
    const isPortal = config.url?.includes('/portal/')
    const token = isPortal ? localStorage.getItem('portalToken') : localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    const data = response.data
    
    // 根据code判断是否成功
    if (data.code === 0) {
      return data.data || data
    } else if (data.code === 401) {
      // 认证失败
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error('认证已过期，请重新登录')
      return Promise.reject(data)
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(data)
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default apiClient
