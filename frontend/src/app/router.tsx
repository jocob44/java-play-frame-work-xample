import { Navigate, Route, Routes } from 'react-router-dom'
import type { ReactNode } from 'react'
import { AuthPage } from '../pages/AuthPage'
import { ProjectsPage } from '../pages/ProjectsPage'
import { useAuthStore } from '../features/auth/store/authStore'

function ProtectedRoute({ children }: { children: ReactNode }) {
  const token = useAuthStore((state) => state.token)
  if (!token) {
    return <Navigate to="/login" replace />
  }
  return <>{children}</>
}

function PublicRoute({ children }: { children: ReactNode }) {
  const token = useAuthStore((state) => state.token)
  if (token) {
    return <Navigate to="/projects" replace />
  }
  return <>{children}</>
}

export function AppRouter() {
  return (
    <Routes>
      <Route
        path="/login"
        element={
          <PublicRoute>
            <AuthPage />
          </PublicRoute>
        }
      />
      <Route
        path="/projects"
        element={
          <ProtectedRoute>
            <ProjectsPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/projects" replace />} />
    </Routes>
  )
}
