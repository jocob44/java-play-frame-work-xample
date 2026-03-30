type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  body?: unknown
  token?: string | null
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '/api'

export class HttpError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.name = 'HttpError'
    this.status = status
  }
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    method: options.method ?? 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
    },
    ...(options.body ? { body: JSON.stringify(options.body) } : {}),
  })

  if (!response.ok) {
    let message = `HTTP ${response.status}`
    try {
      const payload = (await response.json()) as { message?: string }
      if (payload.message) {
        message = payload.message
      }
    } catch {
      // Ignore non-JSON responses and fallback to generic message.
    }
    throw new HttpError(message, response.status)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}
