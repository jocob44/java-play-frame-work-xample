import { useState } from 'react'
import type { FormEvent } from 'react'
import type { Credentials } from '../../../shared/types/auth'

type AuthFormProps = {
  mode: 'login' | 'register'
  loading: boolean
  error: string | null
  onSubmit: (credentials: Credentials) => Promise<void>
}

export function AuthForm({ mode, loading, error, onSubmit }: AuthFormProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [localError, setLocalError] = useState<string | null>(null)

  const title = mode === 'login' ? 'Iniciar sesión' : 'Crear cuenta'
  const submitLabel = mode === 'login' ? 'Entrar' : 'Registrarme'

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLocalError(null)
    if (!email.trim() || !password.trim()) {
      setLocalError('Email y contraseña son obligatorios')
      return
    }
    await onSubmit({ email: email.trim(), password: password.trim() })
  }

  return (
    <form className="card stack" onSubmit={handleSubmit}>
      <h2>{title}</h2>
      <div className="field">
        <label htmlFor="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          autoComplete="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          placeholder="usuario@mail.com"
        />
      </div>
      <div className="field">
        <label htmlFor="password">Contraseña</label>
        <input
          id="password"
          name="password"
          type="password"
          autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          placeholder="********"
        />
      </div>
      {localError ? <p className="error">{localError}</p> : null}
      {error ? <p className="error">{error}</p> : null}
      <button type="submit" disabled={loading}>
        {loading ? 'Procesando...' : submitLabel}
      </button>
    </form>
  )
}
