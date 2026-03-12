import apiClient from '@/utils/api'

export interface Resource {
  id: number
  name: string
  code: string
  type: string
  description: string
  status: string
  contact: string
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

export const resourceApi = {
  list: (pageNo: number, pageSize: number, searchKey?: string) =>
    apiClient.get('/resource/list', { params: { pageNo, pageSize, searchKey } }),
  
  getById: (id: number) =>
    apiClient.get(`/resource/${id}`),
  
  create: (data: Omit<Resource, 'id' | 'createTime' | 'updateTime'>) =>
    apiClient.post('/resource', data),
  
  update: (id: number, data: Partial<Resource>) =>
    apiClient.put(`/resource/${id}`, data),
  
  delete: (id: number) =>
    apiClient.delete(`/resource/${id}`)
}
