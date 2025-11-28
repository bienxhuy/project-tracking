import { useState } from 'react';
import apiClient from '../api/axios.customize.ts';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface ApiTestResult {
  success: boolean;
  data?: any;
  error?: string;
  timestamp: string;
  duration: number;
}

export function ApiTestPage() {
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<ApiTestResult[]>([]);
  
  // Form states
  const [userId, setUserId] = useState('1');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');

  const addResult = (result: Omit<ApiTestResult, 'timestamp'>) => {
    setResults(prev => [{
      ...result,
      timestamp: new Date().toLocaleTimeString()
    }, ...prev]);
  };

  /**
   * Test 1: Get All Users
   */
  const testGetAllUsers = async () => {
    setLoading(true);
    const startTime = Date.now();
    try {
      const response = await apiClient.get('/api/v1/users');
      const duration = Date.now() - startTime;
      
      addResult({
        success: true,
        data: {
          message: response.data.message,
          count: response.data.data.length,
          users: response.data.data.slice(0, 3) // Show first 3 users only
        },
        duration
      });
    } catch (error: any) {
      const duration = Date.now() - startTime;
      addResult({
        success: false,
        error: error.response?.data?.message || error.message || 'Unknown error',
        duration
      });
    } finally {
      setLoading(false);
    }
  };

  /**
   * Test 2: Get User by ID
   */
  const testGetUserById = async () => {
    if (!userId) return;
    
    setLoading(true);
    const startTime = Date.now();
    try {
      const response = await apiClient.get(`/api/v1/users/${userId}`);
      const duration = Date.now() - startTime;
      
      addResult({
        success: true,
        data: {
          message: response.data.message,
          user: response.data.data
        },
        duration
      });
    } catch (error: any) {
      const duration = Date.now() - startTime;
      addResult({
        success: false,
        error: error.response?.data?.message || error.message || 'Unknown error',
        duration
      });
    } finally {
      setLoading(false);
    }
  };

  /**
   * Test 3: Get User by Username
   */
  const testGetUserByUsername = async () => {
    if (!username) return;
    
    setLoading(true);
    const startTime = Date.now();
    try {
      const response = await apiClient.get(`/api/v1/users/username/${username}`);
      const duration = Date.now() - startTime;
      
      addResult({
        success: true,
        data: {
          message: response.data.message,
          user: response.data.data
        },
        duration
      });
    } catch (error: any) {
      const duration = Date.now() - startTime;
      addResult({
        success: false,
        error: error.response?.data?.message || error.message || 'Unknown error',
        duration
      });
    } finally {
      setLoading(false);
    }
  };

  /**
   * Test 4: Get User by Email
   */
  const testGetUserByEmail = async () => {
    if (!email) return;
    
    setLoading(true);
    const startTime = Date.now();
    try {
      const response = await apiClient.get(`/api/v1/users/email/${email}`);
      const duration = Date.now() - startTime;
      
      addResult({
        success: true,
        data: {
          message: response.data.message,
          user: response.data.data
        },
        duration
      });
    } catch (error: any) {
      const duration = Date.now() - startTime;
      addResult({
        success: false,
        error: error.response?.data?.message || error.message || 'Unknown error',
        duration
      });
    } finally {
      setLoading(false);
    }
  };

  /**
   * Test 5: Test Backend Connection
   */
  const testConnection = async () => {
    setLoading(true);
    const startTime = Date.now();
    try {
      // Try to ping the backend
      const response = await apiClient.get('/api/v1/users');
      const duration = Date.now() - startTime;
      
      addResult({
        success: true,
        data: {
          message: '✅ Backend connection successful!',
          baseURL: apiClient.defaults.baseURL,
          responseTime: `${duration}ms`
        },
        duration
      });
    } catch (error: any) {
      const duration = Date.now() - startTime;
      addResult({
        success: false,
        error: `❌ Backend connection failed: ${error.message}`,
        duration
      });
    } finally {
      setLoading(false);
    }
  };

  const clearResults = () => {
    setResults([]);
  };

  return (
    <div className="container mx-auto p-6 max-w-7xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">API Test Page</h1>
        <p className="text-muted-foreground">
          Test backend API endpoints to verify database connection
        </p>
        <div className="mt-4 p-4 bg-blue-50 dark:bg-blue-950 rounded-lg">
          <p className="text-sm font-medium">Backend URL: {apiClient.defaults.baseURL}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        {/* Test 1: Connection Test */}
        <Card>
          <CardHeader>
            <CardTitle>1. Connection Test</CardTitle>
            <CardDescription>Test if backend is reachable</CardDescription>
          </CardHeader>
          <CardContent>
            <Button 
              onClick={testConnection} 
              disabled={loading}
              className="w-full"
            >
              Test Connection
            </Button>
          </CardContent>
        </Card>

        {/* Test 2: Get All Users */}
        <Card>
          <CardHeader>
            <CardTitle>2. Get All Users</CardTitle>
            <CardDescription>GET /api/v1/users</CardDescription>
          </CardHeader>
          <CardContent>
            <Button 
              onClick={testGetAllUsers} 
              disabled={loading}
              className="w-full"
            >
              Get All Users
            </Button>
          </CardContent>
        </Card>

        {/* Test 3: Get User by ID */}
        <Card>
          <CardHeader>
            <CardTitle>3. Get User by ID</CardTitle>
            <CardDescription>GET /api/v1/users/{`{id}`}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <Input
              type="number"
              placeholder="User ID"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
            />
            <Button 
              onClick={testGetUserById} 
              disabled={loading || !userId}
              className="w-full"
            >
              Get User by ID
            </Button>
          </CardContent>
        </Card>

        {/* Test 4: Get User by Username */}
        <Card>
          <CardHeader>
            <CardTitle>4. Get User by Username</CardTitle>
            <CardDescription>GET /api/v1/users/username/{`{username}`}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <Input
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
            <Button 
              onClick={testGetUserByUsername} 
              disabled={loading || !username}
              className="w-full"
            >
              Get User by Username
            </Button>
          </CardContent>
        </Card>

        {/* Test 5: Get User by Email */}
        <Card>
          <CardHeader>
            <CardTitle>5. Get User by Email</CardTitle>
            <CardDescription>GET /api/v1/users/email/{`{email}`}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <Input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <Button 
              onClick={testGetUserByEmail} 
              disabled={loading || !email}
              className="w-full"
            >
              Get User by Email
            </Button>
          </CardContent>
        </Card>
      </div>

      {/* Results Section */}
      <div className="space-y-4">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold">Test Results</h2>
          {results.length > 0 && (
            <Button variant="outline" onClick={clearResults}>
              Clear Results
            </Button>
          )}
        </div>

        {results.length === 0 ? (
          <Card>
            <CardContent className="p-8 text-center text-muted-foreground">
              No tests run yet. Click any test button above to start.
            </CardContent>
          </Card>
        ) : (
          results.map((result, index) => (
            <Card key={index} className={result.success ? 'border-green-500' : 'border-red-500'}>
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div>
                    <Badge variant={result.success ? 'default' : 'destructive'}>
                      {result.success ? '✓ SUCCESS' : '✗ FAILED'}
                    </Badge>
                    <p className="text-sm text-muted-foreground mt-1">
                      {result.timestamp} • {result.duration}ms
                    </p>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                {result.success ? (
                  <pre className="bg-slate-100 dark:bg-slate-900 p-4 rounded-lg overflow-auto text-sm">
                    {JSON.stringify(result.data, null, 2)}
                  </pre>
                ) : (
                  <div className="bg-red-50 dark:bg-red-950 p-4 rounded-lg">
                    <p className="text-red-600 dark:text-red-400 font-medium">
                      {result.error}
                    </p>
                  </div>
                )}
              </CardContent>
            </Card>
          ))
        )}
      </div>
    </div>
  );
}