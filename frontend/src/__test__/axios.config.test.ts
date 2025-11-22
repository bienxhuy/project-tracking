// axios.config.test.ts
import { describe, it, expect, vi, beforeEach } from 'vitest';
import type { InternalAxiosRequestConfig } from 'axios';

// Mock axios module - factory function is hoisted
vi.mock('axios', () => {
  const mockInterceptors = {
    request: {
      use: vi.fn((onFulfilled: any) => {
        mockInterceptors.request._onFulfilled = onFulfilled;
        return 0;
      }),
      _onFulfilled: null as any,
    },
    response: {
      use: vi.fn((onFulfilled: any, onRejected: any) => {
        mockInterceptors.response._onFulfilled = onFulfilled;
        mockInterceptors.response._onRejected = onRejected;
        return 0;
      }),
      _onFulfilled: null as any,
      _onRejected: null as any,
    },
  };

  const mockAxiosInstance: any = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    request: vi.fn(),
    interceptors: mockInterceptors,
  };

  // Simulate axios instance behavior
  mockAxiosInstance.get.mockImplementation(async (url: string, config?: any) => {
    const mergedConfig: InternalAxiosRequestConfig = {
      url,
      method: 'get',
      headers: {} as any,
      ...config,
    };

    // Apply request interceptor
    let finalConfig = mergedConfig;
    if (mockInterceptors.request._onFulfilled) {
      finalConfig = await mockInterceptors.request._onFulfilled(mergedConfig);
    }

    // Call the actual request
    try {
      const response = await mockAxiosInstance.request(finalConfig);
      // Apply response interceptor (success)
      if (mockInterceptors.response._onFulfilled) {
        return await mockInterceptors.response._onFulfilled(response);
      }
      return response;
    } catch (error: any) {
      // Apply response interceptor (error)
      if (mockInterceptors.response._onRejected) {
        return await mockInterceptors.response._onRejected(error);
      }
      throw error;
    }
  });

  const mockAxios: any = {
    create: vi.fn(() => mockAxiosInstance),
    get: vi.fn(),
    post: vi.fn(),
    interceptors: mockInterceptors,
    _mockInstance: mockAxiosInstance, // Expose for testing
  };

  return { 
    default: mockAxios,
    __esModule: true,
  };
});

import apiClient from '../api/axios.customize.ts';
import axios from 'axios';

// Get access to mocked axios
const mockedAxios = axios as any;
const mockAxiosInstance = mockedAxios._mockInstance;

describe("Axios Interceptors", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
    mockAxiosInstance.request.mockReset();
    mockedAxios.get.mockReset();
  });

  it("should attach token to headers in request interceptor", async () => {
    localStorage.setItem('accessToken', 'fake-token');

    // Mock successful request
    mockAxiosInstance.request.mockResolvedValueOnce({ 
      data: "ok",
      status: 200,
      statusText: 'OK',
      headers: {},
      config: {} as any,
    });

    await apiClient.get('/test');

    // Check that request was called with the token
    expect(mockAxiosInstance.request).toHaveBeenCalled();
    const callConfig = mockAxiosInstance.request.mock.calls[0][0];
    expect(callConfig.headers.Authorization).toBe("Bearer fake-token");
  });

  it("should call refresh token when receiving 401", async () => {
    localStorage.setItem('accessToken', 'expired-token');

    // First request fails with 401
    mockAxiosInstance.request
      .mockRejectedValueOnce({ 
        response: { status: 401 }, 
        config: {
          url: '/secured',
          method: 'get',
          headers: {} as any,
        }
      })
      // Retry succeeds after refresh
      .mockResolvedValueOnce({ 
        data: "ok",
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {} as any,
      });

    // Mock refresh token endpoint
    mockedAxios.get.mockResolvedValueOnce({ 
      data: { data: { accessToken: "new-token" }},
      status: 200,
      statusText: 'OK',
      headers: {},
      config: {} as any,
    });

    await apiClient.get("/secured");

    // Verify refresh was called
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining('/auth/refresh'),
      expect.anything()
    );

    // Verify token was updated
    expect(localStorage.getItem('accessToken')).toBe("new-token");
  });

  it("should redirect to /login when refresh fails", async () => {
    const originalLocation = window.location;
    // @ts-ignore
    delete window.location;
    // @ts-ignore
    window.location = { href: "" } as Location;

    localStorage.setItem('accessToken', 'expired-token');

    // Request fails with 401
    mockAxiosInstance.request.mockRejectedValueOnce({ 
      response: { status: 401 }, 
      config: {
        url: '/secured',
        method: 'get',
        headers: {} as any,
      }
    });

    // Refresh fails
    mockedAxios.get.mockRejectedValueOnce(new Error("refresh failed"));

    await expect(apiClient.get("/secured")).rejects.toThrow();

    expect(window.location.href).toBe("/login");

    // Restore
    // @ts-ignore
    window.location = originalLocation;
  });

  it("should queue requests during refresh", async () => {
    localStorage.setItem('accessToken', 'expired');

    let refreshResolve: any;
    const refreshPromise = new Promise((resolve) => {
      refreshResolve = resolve;
    });

    // First 3 requests fail with 401, then all retries succeed
    mockAxiosInstance.request
      .mockRejectedValueOnce({ 
        response: { status: 401 }, 
        config: {
          url: '/a',
          method: 'get',
          headers: {} as any,
        }
      })
      .mockRejectedValueOnce({ 
        response: { status: 401 }, 
        config: {
          url: '/b',
          method: 'get',
          headers: {} as any,
        }
      })
      .mockRejectedValueOnce({ 
        response: { status: 401 }, 
        config: {
          url: '/c',
          method: 'get',
          headers: {} as any,
        }
      })
      // All retries succeed
      .mockResolvedValue({ 
        data: "ok",
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {} as any,
      });

    // Refresh returns a pending promise
    mockedAxios.get.mockReturnValueOnce(refreshPromise);

    const p1 = apiClient.get("/a");
    const p2 = apiClient.get("/b");
    const p3 = apiClient.get("/c");

    // Wait a bit for all requests to queue
    await new Promise(resolve => setTimeout(resolve, 10));

    // Resolve refresh with new token
    refreshResolve({ 
      data: { data: { accessToken: "new-token" }},
      status: 200,
      statusText: 'OK',
      headers: {},
      config: {} as any,
    });

    await Promise.all([p1, p2, p3]);

    expect(localStorage.getItem('accessToken')).toBe("new-token");
  });
});
