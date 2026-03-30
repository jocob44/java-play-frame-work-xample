import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthForm } from '../features/auth/components/AuthForm'
import { useAuthStore } from '../features/auth/store/authStore'
import type { Credentials } from '../shared/types/auth'

export function AuthPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const { login, register, loading, error, clearError } = useAuthStore()

  const handleSubmit = async (credentials: Credentials) => {
    if (mode === 'login') {
      await login(credentials)
    } else {
      await register(credentials)
    }
    navigate('/projects')
  }

  return (
    <main className="container stack">
      <h1>Play Projects</h1>
      <AuthForm mode={mode} loading={loading} error={error} onSubmit={handleSubmit} />
      <button
        type="button"
        className="secondary"
        onClick={() => {
          clearError()
          setMode((prev) => (prev === 'login' ? 'register' : 'login'))
        }}
      >
        {mode === 'login' ? '¿No tenés cuenta? Registrate' : '¿Ya tenés cuenta? Ingresá'}
      </button>
    </main>
  )
}
