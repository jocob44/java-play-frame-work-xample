export const projectStatuses = ['TODO', 'IN_PROGRESS', 'DONE'] as const

export type ProjectStatus = (typeof projectStatuses)[number]

export type Project = {
  id: number
  name: string
  description: string
  status: ProjectStatus
  ownerId: number
  createdAt: string
}

export type ProjectInput = {
  name: string
  description: string
  status: ProjectStatus
}
