import apiClient from '@/utils/api'

export interface PortalUserProfile {
  id?: number | string
  username?: string
  realName?: string
  phone?: string
  email?: string
}

export const portalAuthApi = {
  login: (data: { username: string; password: string }) =>
    apiClient.post('/portal/auth/login', data),
  register: (data: {
    username: string
    password: string
    confirmPassword: string
    realName?: string
    phone?: string
    email?: string
  }) => apiClient.post('/portal/auth/register', data),
  profile: () => apiClient.get('/portal/auth/profile'),
  updateProfile: (data: Pick<PortalUserProfile, 'realName' | 'phone' | 'email'>) =>
    apiClient.put('/portal/auth/profile', data)
}

export interface PortalTicketSubmit {
  theme?: string
  peopleCount?: number
  days?: number
  expectedDate?: string
  specialRequirement?: string
  contactInfo?: string
  title?: string
}

export interface TicketReply {
  id: number | string
  ticketId: number | string
  fromType: string
  fromUserId: number | string
  content: string
  createdAt: string
}

export const portalTicketApi = {
  submit: (data: PortalTicketSubmit) => apiClient.post('/portal/ticket', data),
  myList: (pageNo: number, pageSize: number) =>
    apiClient.get('/portal/ticket/my', { params: { pageNo, pageSize } }),
  getDetail: (id: number | string) => apiClient.get(`/portal/ticket/${id}`),
  addReply: (id: number | string, content: string) =>
    apiClient.post(`/portal/ticket/${id}/reply`, { content })
}
