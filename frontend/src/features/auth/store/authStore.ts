import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import * as authApi from '../api/authApi'
import type { Credentials } from '../../../shared/types/auth'

type AuthState = {
  userId: number | null
  email: string | null
  token: string | null
  loading: boolean
  error: string | null
  login: (credentials: Credentials) => Promise<void>
  register: (credentials: Credentials) => Promise<void>
  logout: () => void
  clearError: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      userId: null,
      email: null,
      token: null,
      loading: false,
      error: null,
      login: async (credentials) => {
        set({ loading: true, error: null })
        try {
          const response = await authApi.login(credentials)
          set({
            userId: response.userId,
            email: response.email,
            token: response.token,
            loading: false,
            error: null,
          })
        } catch (error) {
          set({ loading: false, error: error instanceof Error ? error.message : 'Error al iniciar sesión' })
          throw error
        }
      },
      register: async (credentials) => {
        set({ loading: true, error: null })
        try {
          const response = await authApi.register(credentials)
          set({
            userId: response.userId,
            email: response.email,
            token: response.token,
            loading: false,
            error: null,
          })
        } catch (error) {
          set({ loading: false, error: error instanceof Error ? error.message : 'Error al registrarse' })
          throw error
        }
      },
      logout: () => set({ userId: null, email: null, token: null, error: null }),
      clearError: () => set({ error: null }),
    }),
    {
      name: 'auth-store',
      partialize: (state) => ({
        userId: state.userId,
        email: state.email,
        token: state.token,
      }),
    },
  ),
)
