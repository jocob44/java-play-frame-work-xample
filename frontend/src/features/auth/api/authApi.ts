import { request } from '../../../shared/api/httpClient'
import type { AuthResponse, Credentials } from '../../../shared/types/auth'

export function register(credentials: Credentials) {
  return request<AuthResponse>('/auth/register', { method: 'POST', body: credentials })
}

export function login(credentials: Credentials) {
  return request<AuthResponse>('/auth/login', { method: 'POST', body: credentials })
}
