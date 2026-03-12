import apiClient from '@/utils/api'

export interface TourOrder {
  id: number
  orderNo: string
  customerId: number
  customerName?: string
  productId: number
  startDate: string
  endDate: string
  peopleCount: number
  amount: number
  payStatus: string
  status: string
  ownerUserId: number
  ownerUserName?: string
  remark: string
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNo: number
  pageSize: number
}

export const orderApi = {
  list: (pageNo: number, pageSize: number, status?: string, customerId?: number) =>
    apiClient.get('/order/list', { params: { pageNo, pageSize, status, customerId } }),
  
  getById: (id: number) =>
    apiClient.get(`/order/${id}`),
  
  create: (data: Omit<TourOrder, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/order', data),
  
  update: (id: number, data: Partial<TourOrder>) =>
    apiClient.put(`/order/${id}`, data),
  
  delete: (id: number) =>
    apiClient.delete(`/order/${id}`)
}
