// User service for admin user management
// This service simulates API calls with dummy data matching backend structure

import { 
  User, 
  CreateUserDto, 
  UpdateUserDto, 
  BulkCreateUserDto, 
  BulkImportResult,
  UserFilters,
  UserStats
} from "@/types/user.type";
import { UserStatus, UserRole, LoginType } from "@/types/util.type";
import { dummyUsers } from "@/data/dummy/users.dummy";

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

class UserService {
  private users: User[] = [...dummyUsers];
  private nextId = Math.max(...dummyUsers.map(u => u.id)) + 1;

  // Get all users with optional filters
  async getUsers(filters?: UserFilters): Promise<User[]> {
    await delay(500);
    
    let filteredUsers = [...this.users];

    if (filters?.search) {
      const search = filters.search.toLowerCase();
      filteredUsers = filteredUsers.filter(user => 
        user.username.toLowerCase().includes(search) ||
        user.displayName.toLowerCase().includes(search) ||
        user.email.toLowerCase().includes(search)
      );
    }

    if (filters?.role && filters.role !== "ALL") {
      filteredUsers = filteredUsers.filter(user => user.role === filters.role);
    }

    if (filters?.accountStatus && filters.accountStatus !== "ALL") {
      filteredUsers = filteredUsers.filter(user => user.accountStatus === filters.accountStatus);
    }

    if (filters?.loginType && filters.loginType !== "ALL") {
      filteredUsers = filteredUsers.filter(user => user.loginType === filters.loginType);
    }

    return filteredUsers;
  }

  // Get user by ID
  async getUserById(id: number): Promise<User | null> {
    await delay(300);
    return this.users.find(user => user.id === id) || null;
  }

  // Create new user (Admin creates user, backend auto-sends verification email)
  async createUser(data: CreateUserDto): Promise<User> {
    await delay(500);

    // Check if email already exists
    if (this.users.some(user => user.email === data.email)) {
      throw new Error("Email already exists");
    }

    // Check if username already exists
    if (this.users.some(user => user.username === data.username)) {
      throw new Error("Username already exists");
    }

    const newUser: User = {
      id: this.nextId++,
      username: data.username,
      email: data.email,
      displayName: data.displayName,
      role: data.role,
      accountStatus: UserStatus.VERIFYING, // User starts in VERIFYING status, waiting for OTP
      level: 0.0,
      loginType: data.loginType || LoginType.LOCAL,
      avatar: data.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${data.username}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    this.users.push(newUser);
    
    // Note: In real backend (POST /api/v1/users), 
    // the server will automatically:
    // 1. Create user with VERIFYING status
    // 2. Generate OTP code
    // 3. Send verification email with OTP to user's email
    
    return newUser;
  }

  // Update user
  async updateUser(id: number, data: UpdateUserDto): Promise<User> {
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

    // Check username uniqueness if changed
    if (data.username && data.username !== this.users[userIndex].username) {
      if (this.users.some(user => user.username === data.username)) {
        throw new Error("Username already exists");
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

  // Ban user
  async banUser(id: number): Promise<User> {
    return this.updateUser(id, { accountStatus: UserStatus.BANNED });
  }

  // Unban/Activate user
  async activateUser(id: number): Promise<User> {
    return this.updateUser(id, { accountStatus: UserStatus.ACTIVE });
  }

  // Deactivate user
  async deactivateUser(id: number): Promise<User> {
    return this.updateUser(id, { accountStatus: UserStatus.INACTIVE });
  }

  // Delete user
  async deleteUser(id: number): Promise<void> {
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
      if (!userDto.username) rowErrors.push("Username is required");
      if (!userDto.password) rowErrors.push("Password is required");
      if (!userDto.email) rowErrors.push("Email is required");
      if (!userDto.displayName) rowErrors.push("Display name is required");
      if (!userDto.role) rowErrors.push("Role is required");

      // Check duplicates
      if (this.users.some(user => user.email === userDto.email)) {
        rowErrors.push("Email already exists");
      }

      if (this.users.some(user => user.username === userDto.username)) {
        rowErrors.push("Username already exists");
      }

      if (rowErrors.length > 0) {
        result.failed++;
        result.errors.push({
          row: i + 1,
          username: userDto.username || "N/A",
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
            username: userDto.username,
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
      totalUsers: this.users.length,
      totalAdmins: this.users.filter(u => u.role === UserRole.ADMIN).length,
      totalInstructors: this.users.filter(u => u.role === UserRole.INSTRUCTOR).length,
      totalStudents: this.users.filter(u => u.role === UserRole.STUDENT).length,
      totalInactive: this.users.filter(u => u.accountStatus === UserStatus.INACTIVE).length,
    };

    return stats;
  }
}

// Export singleton instance
export const userService = new UserService();
