import type { PortalUserProfile } from '@/utils/api/portal'

const PORTAL_TOKEN_KEY = 'portalToken'
const PORTAL_USER_INFO_KEY = 'portalUserInfo'
const PORTAL_USER_EVENT = 'portal-user-updated'

export const portalUserEventName = PORTAL_USER_EVENT

export const getPortalToken = (): string | null => localStorage.getItem(PORTAL_TOKEN_KEY)

export const getPortalUserInfo = (): PortalUserProfile | null => {
  const raw = localStorage.getItem(PORTAL_USER_INFO_KEY)
  return raw ? JSON.parse(raw) : null
}

export const setPortalSession = (token: string, userInfo: PortalUserProfile) => {
  localStorage.setItem(PORTAL_TOKEN_KEY, token)
  localStorage.setItem(PORTAL_USER_INFO_KEY, JSON.stringify(userInfo || {}))
  window.dispatchEvent(new CustomEvent(PORTAL_USER_EVENT))
}

export const updatePortalUserInfo = (userInfo: PortalUserProfile) => {
  localStorage.setItem(PORTAL_USER_INFO_KEY, JSON.stringify(userInfo || {}))
  window.dispatchEvent(new CustomEvent(PORTAL_USER_EVENT))
}

export const clearPortalSession = () => {
  localStorage.removeItem(PORTAL_TOKEN_KEY)
  localStorage.removeItem(PORTAL_USER_INFO_KEY)
  window.dispatchEvent(new CustomEvent(PORTAL_USER_EVENT))
}
