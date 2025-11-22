import { User } from './user.type';

export interface LoginRequest {
  identifier: string; // username or email
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  userId?: number;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  displayName: string;
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
  errorCode?: string;
}

export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}