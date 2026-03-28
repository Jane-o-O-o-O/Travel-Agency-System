import apiClient from '@/utils/api'

export interface Ticket {
  id: number | string
  ticketNo: string
  orderId?: number
  customerId?: number
  portalUserId?: number
  problemType: string
  priority: string
  title: string
  theme?: string
  peopleCount?: number
  days?: number
  expectedDate?: string
  specialRequirement?: string
  contactInfo?: string
  description: string
  handlerUserId?: number
  status: string
  firstResponseTime: number
  resolutionTime: number
  createdAt: string
  updatedAt: string
}

export interface TicketReply {
  id: number | string
  ticketId: number | string
  fromType: string
  fromUserId: number | string
  content: string
  createdAt: string
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
  
  getById: (id: number | string) =>
    apiClient.get(`/ticket/${id}`),

  getReplies: (id: number | string) =>
    apiClient.get(`/ticket/${id}/replies`),

  reply: (id: number | string, data: { content: string; handlerUserId?: number | string }) =>
    apiClient.post(`/ticket/${id}/reply`, data),
  
  create: (data: Omit<Ticket, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/ticket', data),
  
  update: (id: number | string, data: Partial<Ticket>) =>
    apiClient.put(`/ticket/${id}`, data),
  
  delete: (id: number | string) =>
    apiClient.delete(`/ticket/${id}`)
}
