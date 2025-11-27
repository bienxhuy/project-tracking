import apiClient from "../api/axios.customize.ts";
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  ApiResponse,
  ChangePasswordRequest,
} from "@/types/auth.type";
import { User } from "@/types/user.type";

class AuthService {
  async login(credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    const response = await apiClient.post<ApiResponse<LoginResponse>>(
      '/api/v1/auth/login',
      credentials
    );
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<ApiResponse<User>> {
    const response = await apiClient.post<ApiResponse<User>>(
      '/api/v1/auth/register',
      userData
    );
    return response.data;
  }

  async logout(): Promise<ApiResponse<boolean>> {
    const response = await apiClient.get<ApiResponse<boolean>>(
      "/api/v1/auth/logout"
    );
    return response.data;
  }

  async changePassword(
    userId: number,
    payload: ChangePasswordRequest
  ): Promise<ApiResponse<User>> {
    const response = await apiClient.put<ApiResponse<User>>(
      `/api/v1/users/${userId}`,
      {
        password: payload.newPassword,
      }
    );
    return response.data;
  }

  async getAccount(): Promise<ApiResponse<User>> {
    const response = await apiClient.get<ApiResponse<User>>(
      '/api/v1/auth/account'
    );
    return response.data;
  }

  async refreshToken(): Promise<ApiResponse<LoginResponse>> {
    const response = await apiClient.get<ApiResponse<LoginResponse>>(
      '/api/v1/auth/refresh'
    );
    return response.data;
  }
}

export const authService = new AuthService();