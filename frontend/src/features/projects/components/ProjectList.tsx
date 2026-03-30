import type { Project } from '../../../shared/types/project'

type ProjectListProps = {
  items: Project[]
  loading: boolean
  onEdit: (item: Project) => void
  onDelete: (id: number) => Promise<void>
}

export function ProjectList({ items, loading, onEdit, onDelete }: ProjectListProps) {
  if (!items.length) {
    return (
      <div className="card">
        <p className="muted">{loading ? 'Cargando proyectos...' : 'No hay proyectos todavía.'}</p>
      </div>
    )
  }

  return (
    <div className="stack">
      {items.map((item) => (
        <article className="card stack" key={item.id}>
          <div className="row-between">
            <strong>{item.name}</strong>
            <span className="muted">{item.status}</span>
          </div>
          <p>{item.description}</p>
          <small className="muted">Creado: {new Date(item.createdAt).toLocaleString()}</small>
          <div className="row">
            <button type="button" className="secondary" onClick={() => onEdit(item)} disabled={loading}>
              Editar
            </button>
            <button
              type="button"
              className="danger"
              onClick={() => void onDelete(item.id)}
              disabled={loading}
            >
              Eliminar
            </button>
          </div>
        </article>
      ))}
    </div>
  )
}
