import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../features/auth/store/authStore'
import { ProjectForm } from '../features/projects/components/ProjectForm'
import { ProjectList } from '../features/projects/components/ProjectList'
import { useProjectsStore } from '../features/projects/store/projectsStore'
import type { Project, ProjectInput } from '../shared/types/project'

export function ProjectsPage() {
  const navigate = useNavigate()
  const { token, email, logout } = useAuthStore()
  const { items, loading, error, loadProjects, createProject, updateProject, deleteProject, clearError } =
    useProjectsStore()
  const [selectedProject, setSelectedProject] = useState<Project | null>(null)

  useEffect(() => {
    if (!token) {
      navigate('/login', { replace: true })
      return
    }
    void loadProjects(token)
  }, [token, loadProjects, navigate])

  const handleCreate = async (payload: ProjectInput) => {
    if (!token) {
      return
    }
    await createProject(payload, token)
  }

  const handleUpdate = async (id: number, payload: ProjectInput) => {
    if (!token) {
      return
    }
    await updateProject(id, payload, token)
    setSelectedProject(null)
  }

  const handleDelete = async (id: number) => {
    if (!token) {
      return
    }
    const confirmed = window.confirm('¿Eliminar este proyecto?')
    if (!confirmed) {
      return
    }
    await deleteProject(id, token)
    if (selectedProject?.id === id) {
      setSelectedProject(null)
    }
  }

  return (
    <main className="container stack">
      <header className="row-between">
        <div>
          <h1>Mis proyectos</h1>
          <p className="muted">Usuario: {email}</p>
        </div>
        <button
          type="button"
          onClick={() => {
            logout()
            navigate('/login', { replace: true })
          }}
        >
          Cerrar sesión
        </button>
      </header>

      {error ? (
        <div className="card">
          <p className="error">{error}</p>
          <button type="button" className="secondary" onClick={clearError}>
            Limpiar error
          </button>
        </div>
      ) : null}

      <ProjectForm
        loading={loading}
        selectedProject={selectedProject}
        onCreate={handleCreate}
        onUpdate={handleUpdate}
        onCancelEdit={() => setSelectedProject(null)}
      />

      <ProjectList items={items} loading={loading} onEdit={setSelectedProject} onDelete={handleDelete} />
    </main>
  )
}
