// User type definitions for the admin user management system

export enum UserRole {
  STUDENT = "STUDENT",
  INSTRUCTOR = "INSTRUCTOR",
  ADMIN = "ADMIN"
}

export enum UserStatus {
  ACTIVE = "ACTIVE",
  INACTIVE = "INACTIVE"
}

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  studentId?: string;  // For students
  instructorId?: string;  // For instructors
  avatar?: string;
  createdAt: string;
  updatedAt: string;
  lastLogin?: string;
  // Activity stats (optional)
  projectCount?: number;
  completedTasks?: number;
  totalTasks?: number;
}

export interface CreateUserDto {
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  studentId?: string;
  instructorId?: string;
}

export interface UpdateUserDto {
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: UserRole;
  status?: UserStatus;
  studentId?: string;
  instructorId?: string;
}

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
  email: string;
  errors: string[];
}

export interface UserFilters {
  search?: string;
  role?: UserRole | "ALL";
  status?: UserStatus | "ALL";
}

export interface UserStats {
  totalStudents: number;
  totalInstructors: number;
  totalActive: number;
  totalInactive: number;
}

