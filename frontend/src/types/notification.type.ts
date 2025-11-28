// Minimal representation of a notification for API calls

export interface Notification {
  id: number
  title: string
  message: string
  isRead: boolean
  createdDate: Date
  link: string
  project: {
    id: number
    title: string
  }
}

export interface NotificationListResponse {
  totalCount: number
  projectNotifications: Array<{
    projectId: number
    projectTitle: string
    notifications: Notification[]
  }>
}