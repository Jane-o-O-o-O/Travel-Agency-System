import apiClient from '@/utils/api'

export interface Ticket {
  id: number
  ticketNo: string
  orderId: number
  customerId: number
  problemType: string
  priority: string
  title: string
  description: string
  handlerUserId: number
  status: string
  firstResponseTime: number
  resolutionTime: number
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  list: T[]
  total: number
  pageNo: number
  pageSize: number
}

export const ticketApi = {
  list: (pageNo: number, pageSize: number, searchKey?: string) =>
    apiClient.get('/ticket/list', { params: { pageNo, pageSize, searchKey } }),
  
  getById: (id: number) =>
    apiClient.get(`/ticket/${id}`),
  
  create: (data: Omit<Ticket, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/ticket', data),
  
  update: (id: number, data: Partial<Ticket>) =>
    apiClient.put(`/ticket/${id}`, data),
  
  delete: (id: number) =>
    apiClient.delete(`/ticket/${id}`)
}
