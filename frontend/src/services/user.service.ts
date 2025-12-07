import apiClient, { apiClientLongRunning } from '../api/axios.customize.ts';
import { 
  User, 
  CreateUserDto, 
  UpdateUserDto, 
  BulkCreateUserDto, 
  BulkImportResult,
  UserFilters,
  UserStats
} from "@/types/user.type";
import { UserRole, UserStatus } from "@/types/util.type";
import { ApiResponse } from "@/types/auth.type";

class UserService {
  async getUsers(filters?: UserFilters): Promise<User[]> {
    const params = new URLSearchParams();
    
    if (filters?.search) params.append('search', filters.search);
    if (filters?.role && filters.role !== 'ALL') params.append('role', filters.role);
    if (filters?.loginType && filters.loginType !== 'ALL') {
      params.append('loginType', filters.loginType);
    }

    const response = await apiClient.get<ApiResponse<User[]>>(
      `/api/v1/users?${params.toString()}`
    );
    return response.data.data;
  }

  async getUserById(id: number): Promise<User> {
    const response = await apiClient.get<ApiResponse<User>>(`/api/v1/users/${id}`);
    return response.data.data;
  }

  async createUser(data: CreateUserDto): Promise<User> {
    const response = await apiClient.post<ApiResponse<User>>('/api/v1/users', data);
    return response.data.data;
  }

  async updateUser(id: number, data: UpdateUserDto): Promise<User> {
    const response = await apiClient.put<ApiResponse<User>>(`/api/v1/users/${id}`, data);
    return response.data.data;
  }

  async activateUser(id: number): Promise<User> {
    const response = await apiClient.put<ApiResponse<User>>(
      `/api/v1/users/${id}/status?status=ACTIVE`
    );
    return response.data.data;
  }
  
  async deactivateUser(id: number): Promise<User> {
    const response = await apiClient.put<ApiResponse<User>>(
      `/api/v1/users/${id}/status?status=INACTIVE`
    );
    return response.data.data;
  }

  async deleteUser(id: number): Promise<void> {
    await apiClient.delete(`/api/v1/users/${id}`);
  }

  async bulkCreateUsers(data: BulkCreateUserDto): Promise<BulkImportResult> {
    const response = await apiClientLongRunning.post<ApiResponse<BulkImportResult>>(
      '/api/v1/users/bulk-import',
      data
    );
    return response.data.data;
  }
  
  async cancelBulkEmailSending(taskId: string): Promise<void> {
    await apiClient.post(`/api/v1/users/bulk-import/cancel/${taskId}`);
  }

  // Calculate stats locally from users array
  calculateStats(users: User[]): UserStats {
    return {
      totalUsers: users.length,
      totalAdmins: users.filter(u => u.role === UserRole.ADMIN).length,
      totalInstructors: users.filter(u => u.role === UserRole.INSTRUCTOR).length,
      totalStudents: users.filter(u => u.role === UserRole.STUDENT).length,
      totalInactive: 0, // Removed accountStatus filter
    };
  }
}

export const userService = new UserService();


// API Service to fetch user data (students specifically)

import { BaseUser } from "@/types/user.type";

/**
 * Fetch all students
 */
export async function fetchAllStudents(): Promise<BaseUser[]> {
  const response = await apiClient.get<ApiResponse<User[]>>(
    '/api/v1/users?role=STUDENT'
  );
  
  // Parse to BaseUser array
  return response.data.data.map(user => ({
    id: user.id,
    displayName: user.displayName,
    email: user.email,
    role: user.role
  }));
}

/**
 * Search students by query
 */
export async function searchStudents(query: string): Promise<BaseUser[]> {
  const params = new URLSearchParams();
  params.append('role', 'STUDENT');
  
  if (query.trim()) {
    params.append('search', query.trim());
  }
  
  const response = await apiClient.get<ApiResponse<User[]>>(
    `/api/v1/users?${params.toString()}`
  );
  
  // Parse to BaseUser array
  return response.data.data.map(user => ({
    id: user.id,
    displayName: user.displayName,
    email: user.email,
    role: user.role
  }));
}
