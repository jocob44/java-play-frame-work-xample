import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { projectStatuses, type Project, type ProjectInput, type ProjectStatus } from '../../../shared/types/project'

type ProjectFormProps = {
  loading: boolean
  selectedProject: Project | null
  onCreate: (payload: ProjectInput) => Promise<void>
  onUpdate: (id: number, payload: ProjectInput) => Promise<void>
  onCancelEdit: () => void
}

const emptyProject: ProjectInput = {
  name: '',
  description: '',
  status: 'TODO',
}

export function ProjectForm({ loading, selectedProject, onCreate, onUpdate, onCancelEdit }: ProjectFormProps) {
  const [formState, setFormState] = useState<ProjectInput>(emptyProject)
  const [localError, setLocalError] = useState<string | null>(null)

  useEffect(() => {
    if (!selectedProject) {
      setFormState(emptyProject)
      return
    }
    setFormState({
      name: selectedProject.name,
      description: selectedProject.description,
      status: selectedProject.status,
    })
  }, [selectedProject])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLocalError(null)
    if (!formState.name.trim() || !formState.description.trim()) {
      setLocalError('Nombre y descripción son obligatorios')
      return
    }
    const payload: ProjectInput = {
      name: formState.name.trim(),
      description: formState.description.trim(),
      status: formState.status,
    }
    if (selectedProject) {
      await onUpdate(selectedProject.id, payload)
      return
    }
    await onCreate(payload)
    setFormState(emptyProject)
  }

  const isEditing = Boolean(selectedProject)

  return (
    <form className="card stack" onSubmit={handleSubmit}>
      <h3>{isEditing ? 'Editar proyecto' : 'Nuevo proyecto'}</h3>
      <div className="field">
        <label htmlFor="name">Nombre</label>
        <input
          id="name"
          value={formState.name}
          onChange={(event) => setFormState((prev) => ({ ...prev, name: event.target.value }))}
          placeholder="Proyecto"
        />
      </div>
      <div className="field">
        <label htmlFor="description">Descripción</label>
        <textarea
          id="description"
          value={formState.description}
          onChange={(event) => setFormState((prev) => ({ ...prev, description: event.target.value }))}
          placeholder="Detalles"
        />
      </div>
      <div className="field">
        <label htmlFor="status">Estado</label>
        <select
          id="status"
          value={formState.status}
          onChange={(event) =>
            setFormState((prev) => ({ ...prev, status: event.target.value as ProjectStatus }))
          }
        >
          {projectStatuses.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>
      </div>
      {localError ? <p className="error">{localError}</p> : null}
      <div className="row">
        <button type="submit" disabled={loading}>
          {loading ? 'Guardando...' : isEditing ? 'Actualizar' : 'Crear'}
        </button>
        {isEditing ? (
          <button type="button" className="secondary" onClick={onCancelEdit} disabled={loading}>
            Cancelar
          </button>
        ) : null}
      </div>
    </form>
  )
}
