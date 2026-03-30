# Frontend Projects UI

Frontend en React + TypeScript + Zustand para consumir:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET/POST/PUT/DELETE /api/projects`

## Scripts

- `npm run dev` inicia Vite en `http://localhost:3000`
- `npm run build` compila para producción
- `npm run preview` sirve el build localmente

## Variables de entorno

- `VITE_API_BASE_URL` (default: `/api`)
- `VITE_PROXY_TARGET` (default: `http://localhost:9000`)

Con el default, en desarrollo Vite usa proxy a `http://localhost:9000`.
En `docker-compose.dev.yml` se define `VITE_PROXY_TARGET=http://backend:9000` para que funcione dentro de la red Docker.
En Docker producción local, nginx redirige `/api` al backend.

## Estructura

- `src/features/auth` login/registro + store persistido
- `src/features/projects` CRUD + store
- `src/shared/api` cliente HTTP tipado
- `src/pages` pantallas de auth y proyectos
