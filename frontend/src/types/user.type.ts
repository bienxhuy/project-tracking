// User type definitions matching backend entity structure

import { UserRole, UserStatus, LoginType } from "./util.type";

// Re-export enums for backward compatibility
export { UserRole, UserStatus, LoginType };

// Main User interface matching backend entity
export interface User {
  id: number;  // Long in backend
  username: string;
  email: string;
  displayName: string;
  avatar?: string;
  role: UserRole;
  accountStatus: UserStatus;
  level: number;  // double in backend
  loginType: LoginType;
  createdAt: string;  // LocalDateTime from BaseEntity
  updatedAt: string;  // LocalDateTime from BaseEntity
  
  // Password is not included in response for security
  // password: string; (only for creation)
}

// DTO for creating new user
export interface CreateUserDto {
  username: string;
  password: string;
  email: string;
  displayName: string;
  role: UserRole;
  loginType?: LoginType;  // Default to LOCAL if not provided
  avatar?: string;
}

// DTO for updating user
export interface UpdateUserDto {
  username?: string;
  password?: string;  // Optional, only if changing password
  email?: string;
  displayName?: string;
  role?: UserRole;
  accountStatus?: UserStatus;
  level?: number;
  avatar?: string;
}

// Bulk operations
export interface BulkCreateUserDto {
  users: CreateUserDto[];
}

export interface BulkImportResult {
  total: number;
  success: number;
  failed: number;
  errors: BulkImportError[];
}

export interface BulkImportError {
  row: number;
  username: string;
  email: string;
  errors: string[];
}

// Filters for user list
export interface UserFilters {
  search?: string;  // Search by username, email, displayName
  role?: UserRole | "ALL";
  accountStatus?: UserStatus | "ALL";
  loginType?: LoginType | "ALL";
}

// Statistics for dashboard
export interface UserStats {
  totalUsers: number;
  totalAdmins: number;
  totalInstructors: number;
  totalStudents: number;
  totalInactive: number;
}


