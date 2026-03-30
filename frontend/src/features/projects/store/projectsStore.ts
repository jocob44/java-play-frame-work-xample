import { create } from 'zustand'
import * as projectsApi from '../api/projectsApi'
import type { Project, ProjectInput } from '../../../shared/types/project'

type ProjectsState = {
  items: Project[]
  loading: boolean
  error: string | null
  loadProjects: (token: string) => Promise<void>
  createProject: (payload: ProjectInput, token: string) => Promise<void>
  updateProject: (id: number, payload: ProjectInput, token: string) => Promise<void>
  deleteProject: (id: number, token: string) => Promise<void>
  clearError: () => void
}

export const useProjectsStore = create<ProjectsState>((set, get) => ({
  items: [],
  loading: false,
  error: null,
  loadProjects: async (token) => {
    set({ loading: true, error: null })
    try {
      const items = await projectsApi.listProjects(token)
      set({ items, loading: false, error: null })
    } catch (error) {
      set({ loading: false, error: error instanceof Error ? error.message : 'Error al cargar proyectos' })
      throw error
    }
  },
  createProject: async (payload, token) => {
    set({ loading: true, error: null })
    try {
      const created = await projectsApi.createProject(payload, token)
      const current = get().items
      set({ items: [created, ...current], loading: false, error: null })
    } catch (error) {
      set({ loading: false, error: error instanceof Error ? error.message : 'Error al crear proyecto' })
      throw error
    }
  },
  updateProject: async (id, payload, token) => {
    set({ loading: true, error: null })
    try {
      const updated = await projectsApi.updateProject(id, payload, token)
      const current = get().items
      set({
        items: current.map((item) => (item.id === id ? updated : item)),
        loading: false,
        error: null,
      })
    } catch (error) {
      set({ loading: false, error: error instanceof Error ? error.message : 'Error al actualizar proyecto' })
      throw error
    }
  },
  deleteProject: async (id, token) => {
    set({ loading: true, error: null })
    try {
      await projectsApi.deleteProject(id, token)
      set({
        items: get().items.filter((item) => item.id !== id),
        loading: false,
        error: null,
      })
    } catch (error) {
      set({ loading: false, error: error instanceof Error ? error.message : 'Error al eliminar proyecto' })
      throw error
    }
  },
  clearError: () => set({ error: null }),
}))
