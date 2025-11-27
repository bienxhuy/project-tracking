// Minimal representation of a user data for API calls

export interface User {
  id: number,
  full_name: string,
  email: string,
  role: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN',
}
