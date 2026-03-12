import apiClient from '@/utils/api'

export interface Customer {
  id: number
  name: string
  phone: string
  email: string
  source: string
  budgetLevel: string
  note: string
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNo: number
  pageSize: number
}

export const customerApi = {
  list: (pageNo: number, pageSize: number, searchKey?: string) =>
    apiClient.get('/customer/list', { params: { pageNo, pageSize, searchKey } }),
  
  getById: (id: number) =>
    apiClient.get(`/customer/${id}`),
  
  create: (data: Omit<Customer, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/customer', data),
  
  update: (id: number, data: Partial<Customer>) =>
    apiClient.put(`/customer/${id}`, data),
  
  delete: (id: number) =>
    apiClient.delete(`/customer/${id}`)
}
