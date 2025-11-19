// User service for admin user management
// This service simulates API calls with dummy data

import { 
  User, 
  CreateUserDto, 
  UpdateUserDto, 
  BulkCreateUserDto, 
  BulkImportResult,
  UserFilters,
  UserStats,
  UserStatus,
  UserRole
} from "@/types/user.type";
import { dummyUsers } from "@/data/dummy/users.dummy";

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

class UserService {
  private users: User[] = [...dummyUsers];

  // Get all users with optional filters
  async getUsers(filters?: UserFilters): Promise<User[]> {
    await delay(500);
    
    let filteredUsers = [...this.users];

    if (filters?.search) {
      const search = filters.search.toLowerCase();
      filteredUsers = filteredUsers.filter(user => 
        user.firstName.toLowerCase().includes(search) ||
        user.lastName.toLowerCase().includes(search) ||
        user.email.toLowerCase().includes(search) ||
        user.studentId?.toLowerCase().includes(search) ||
        user.instructorId?.toLowerCase().includes(search)
      );
    }

    if (filters?.role && filters.role !== "ALL") {
      filteredUsers = filteredUsers.filter(user => user.role === filters.role);
    }

    if (filters?.status && filters.status !== "ALL") {
      filteredUsers = filteredUsers.filter(user => user.status === filters.status);
    }

    return filteredUsers;
  }

  // Get user by ID
  async getUserById(id: string): Promise<User | null> {
    await delay(300);
    return this.users.find(user => user.id === id) || null;
  }

  // Create new user
  async createUser(data: CreateUserDto): Promise<User> {
    await delay(500);

    // Check if email already exists
    if (this.users.some(user => user.email === data.email)) {
      throw new Error("Email already exists");
    }

    // Check if student/instructor ID already exists
    if (data.studentId && this.users.some(user => user.studentId === data.studentId)) {
      throw new Error("Student ID already exists");
    }
    if (data.instructorId && this.users.some(user => user.instructorId === data.instructorId)) {
      throw new Error("Instructor ID already exists");
    }

    const newUser: User = {
      id: Math.random().toString(36).substr(2, 9),
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      role: data.role,
      status: UserStatus.ACTIVE,
      studentId: data.studentId,
      instructorId: data.instructorId,
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${data.firstName}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      projectCount: 0,
      completedTasks: 0,
      totalTasks: 0
    };

    this.users.push(newUser);
    return newUser;
  }

  // Update user
  async updateUser(id: string, data: UpdateUserDto): Promise<User> {
    await delay(500);

    const userIndex = this.users.findIndex(user => user.id === id);
    if (userIndex === -1) {
      throw new Error("User not found");
    }

    // Check email uniqueness if changed
    if (data.email && data.email !== this.users[userIndex].email) {
      if (this.users.some(user => user.email === data.email)) {
        throw new Error("Email already exists");
      }
    }

    const updatedUser: User = {
      ...this.users[userIndex],
      ...data,
      updatedAt: new Date().toISOString()
    };

    this.users[userIndex] = updatedUser;
    return updatedUser;
  }

  // Deactivate user
  async deactivateUser(id: string): Promise<User> {
    return this.updateUser(id, { status: UserStatus.INACTIVE });
  }

  // Activate user
  async activateUser(id: string): Promise<User> {
    return this.updateUser(id, { status: UserStatus.ACTIVE });
  }

  // Delete user
  async deleteUser(id: string): Promise<void> {
    await delay(500);

    const userIndex = this.users.findIndex(user => user.id === id);
    if (userIndex === -1) {
      throw new Error("User not found");
    }

    this.users.splice(userIndex, 1);
  }

  // Bulk create users
  async bulkCreateUsers(data: BulkCreateUserDto): Promise<BulkImportResult> {
    await delay(1000);

    const result: BulkImportResult = {
      total: data.users.length,
      success: 0,
      failed: 0,
      errors: []
    };

    for (let i = 0; i < data.users.length; i++) {
      const userDto = data.users[i];
      const rowErrors: string[] = [];

      // Validate required fields
      if (!userDto.firstName) rowErrors.push("First name is required");
      if (!userDto.lastName) rowErrors.push("Last name is required");
      if (!userDto.email) rowErrors.push("Email is required");
      if (!userDto.role) rowErrors.push("Role is required");

      // Check duplicates
      if (this.users.some(user => user.email === userDto.email)) {
        rowErrors.push("Email already exists");
      }

      if (userDto.role === UserRole.STUDENT && !userDto.studentId) {
        rowErrors.push("Student ID is required for students");
      }

      if (userDto.role === UserRole.INSTRUCTOR && !userDto.instructorId) {
        rowErrors.push("Instructor ID is required for instructors");
      }

      if (rowErrors.length > 0) {
        result.failed++;
        result.errors.push({
          row: i + 1,
          email: userDto.email || "N/A",
          errors: rowErrors
        });
      } else {
        try {
          await this.createUser(userDto);
          result.success++;
        } catch (error) {
          result.failed++;
          result.errors.push({
            row: i + 1,
            email: userDto.email,
            errors: [error instanceof Error ? error.message : "Unknown error"]
          });
        }
      }
    }

    return result;
  }

  // Get user statistics
  async getUserStats(): Promise<UserStats> {
    await delay(300);
    
    const stats: UserStats = {
      totalStudents: this.users.filter(u => u.role === UserRole.STUDENT).length,
      totalInstructors: this.users.filter(u => u.role === UserRole.INSTRUCTOR).length,
      totalActive: this.users.filter(u => u.status === UserStatus.ACTIVE).length,
      totalInactive: this.users.filter(u => u.status === UserStatus.INACTIVE).length,
    };

    return stats;
  }
}

// Export singleton instance
export const userService = new UserService();

