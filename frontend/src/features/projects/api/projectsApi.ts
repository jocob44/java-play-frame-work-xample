import { request } from '../../../shared/api/httpClient'
import type { Project, ProjectInput } from '../../../shared/types/project'

export function listProjects(token: string) {
  return request<Project[]>('/projects', { token })
}

export function createProject(payload: ProjectInput, token: string) {
  return request<Project>('/projects', { method: 'POST', body: payload, token })
}

export function updateProject(id: number, payload: ProjectInput, token: string) {
  return request<Project>(`/projects/${id}`, { method: 'PUT', body: payload, token })
}

export function deleteProject(id: number, token: string) {
  return request<void>(`/projects/${id}`, { method: 'DELETE', token })
}
