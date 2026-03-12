import apiClient from '@/utils/api'

export interface TourProduct {
  id: number
  name: string
  code: string
  templateId: number
  days: number
  basePrice: number
  costDescription: string
  included: string
  excluded: string
  notes: string
  groupRule: string
  highlights: string
  status: string
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNo: number
  pageSize: number
}

export const productApi = {
  list: (pageNo: number, pageSize: number, searchKey?: string) =>
    apiClient.get('/product/list', { params: { pageNo, pageSize, searchKey } }),
  
  getById: (id: number) =>
    apiClient.get(`/product/${id}`),
  
  create: (data: Omit<TourProduct, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/product', data),
  
  update: (id: number, data: Partial<TourProduct>) =>
    apiClient.put(`/product/${id}`, data),
  
  delete: (id: number) =>
    apiClient.delete(`/product/${id}`)
}
