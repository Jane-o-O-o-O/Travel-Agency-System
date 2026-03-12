import apiClient from '@/utils/api'

export interface ItineraryTemplate {
  id: number
  name: string
  theme: string
  days: number
  description: string
  applicableCrowd: string
  status: string
  useCount: number
  version: number
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNo: number
  pageSize: number
}

export const templateApi = {
  list: (pageNo: number, pageSize: number, searchKey?: string) =>
    apiClient.get('/template/list', { params: { pageNo, pageSize, searchKey } }),
  
  getById: (id: number) =>
    apiClient.get(`/template/${id}`),
  
  create: (data: Omit<ItineraryTemplate, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/template', data),
  
  update: (id: number, data: Partial<ItineraryTemplate>) =>
    apiClient.put(`/template/${id}`, data),
  
  delete: (id: number) =>
    apiClient.delete(`/template/${id}`)
}
