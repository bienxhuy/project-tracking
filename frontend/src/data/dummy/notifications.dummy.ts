// Simulated data from BE

import { NotificationListResponse } from '@/types/notification.type'

export const notificationListResponse: NotificationListResponse = {
  totalCount: 7,
  projectNotifications: [
    {
      projectId: 1,
      projectTitle: 'Website Redesign',
      notifications: [
        {
          id: 1,
          title: 'New comment on Task #23',
          message: 'Alice: Please update the hero image sizes before Friday.',
          isRead: false,
          createdDate: new Date('2025-11-16T09:12:00Z'),
          link: '/projects/proj-1/tasks/23',
          project: {
            id: 1,
            title: 'Website Redesign',
          },
        },
        {
          id: 2,
          title: 'Milestone reached',
          message: 'Sprint 2 milestone has been completed by the team.',
          isRead: true,
          createdDate: new Date('2025-11-14T15:40:00Z'),
          link: '/projects/proj-1/milestones/2',
          project: {
            id: 1,
            title: 'Website Redesign',
          },
        },
      ],
    },
    {
      projectId: 2,
      projectTitle: 'Mobile App - Student Portal',
      notifications: [
        {
          id: 3,
          title: 'Task assigned to you',
          message: 'You have been assigned Task #88: Implement login screen.',
          isRead: false,
          createdDate: new Date('2025-11-15T08:00:00Z'),
          link: '/projects/proj-2/tasks/88',
          project: {
            id: 2,
            title: 'Mobile App - Student Portal',
          },
        },
        {
          id: 4,
          title: 'Review requested',
          message: 'Please review PR #45 for the authentication flow.',
          isRead: false,
          createdDate: new Date('2025-11-16T06:30:00Z'),
          link: '/projects/proj-2/pull-requests/45',
          project: {
            id: 2,
            title: 'Mobile App - Student Portal',
          },
        },
        {
          id: 5,
          title: 'Deadline approaching',
          message: 'Prototype delivery is due in 3 days (Nov 19).',
          isRead: true,
          createdDate: new Date('2025-11-13T12:00:00Z'),
          link: '/projects/proj-2',
          project: {
            id: 2,
            title: 'Mobile App - Student Portal',
          },
        },
      ],
    },
    {
      projectId: 3,
      projectTitle: 'Research: AI Scheduling Assistant',
      notifications: [
        {
          id: 6,
          title: 'New collaborator joined',
          message: 'Dr. Nguyen has been added to the project as a reviewer.',
          isRead: false,
          createdDate: new Date('2025-11-10T10:20:00Z'),
          link: '/projects/proj-3/members',
          project: {
            id: 3,
            title: 'Research: AI Scheduling Assistant',
          },
        },
      ],
    },
  ],
}
