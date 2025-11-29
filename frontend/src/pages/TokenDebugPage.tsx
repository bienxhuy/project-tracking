import { useState, useEffect } from 'react';
import { debugDeviceTokens } from '@/services/notification.api';
import { Bug, RefreshCw, AlertCircle, CheckCircle2, XCircle } from 'lucide-react';

interface TokenDebugInfo {
  userId: number;
  username: string;
  totalTokens: number;
  tokens: Array<{
    id: number;
    fcmToken: string;
    deviceType: string;
    deviceInfo: string;
    isActive: boolean;
    createdAt: string;
    lastUsedAt: string;
  }>;
}

export default function TokenDebugPage() {
  const [loading, setLoading] = useState(false);
  const [debugInfo, setDebugInfo] = useState<TokenDebugInfo | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [currentToken, setCurrentToken] = useState<string | null>(null);

  useEffect(() => {
    // Get current token from localStorage
    const token = localStorage.getItem('fcmToken');
    setCurrentToken(token);
    
    // Auto load debug info
    loadDebugInfo();
  }, []);

  const loadDebugInfo = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const data = await debugDeviceTokens();
      setDebugInfo(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load debug information');
    } finally {
      setLoading(false);
    }
  };

  const isTokenInDatabase = (token: string) => {
    return debugInfo?.tokens.some(t => t.fcmToken === token);
  };

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-100 rounded-lg">
                <Bug className="w-6 h-6 text-purple-600" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">
                  FCM Token Debug
                </h1>
                <p className="text-gray-600 text-sm">
                  Kiểm tra trạng thái token và troubleshoot lỗi UNREGISTERED
                </p>
              </div>
            </div>
            <button
              onClick={loadDebugInfo}
              disabled={loading}
              className="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50"
            >
              <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
              Refresh
            </button>
          </div>
        </div>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
            <div className="flex items-center gap-2">
              <XCircle className="w-5 h-5 text-red-500" />
              <p className="text-red-700">{error}</p>
            </div>
          </div>
        )}

        {/* Current Token Status */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Current Browser Token
          </h2>
          
          {currentToken ? (
            <div className="space-y-3">
              <div className="p-4 bg-gray-50 rounded-lg border border-gray-200">
                <p className="text-xs text-gray-500 mb-2">FCM Token (localStorage)</p>
                <p className="font-mono text-sm text-gray-800 break-all">
                  {currentToken}
                </p>
              </div>
              
              {debugInfo && (
                <div className="flex items-center gap-2">
                  {isTokenInDatabase(currentToken) ? (
                    <>
                      <CheckCircle2 className="w-5 h-5 text-green-500" />
                      <span className="text-green-700 font-medium">
                        ✓ Token này có trong database
                      </span>
                    </>
                  ) : (
                    <>
                      <AlertCircle className="w-5 h-5 text-orange-500" />
                      <span className="text-orange-700 font-medium">
                        ⚠ Token này KHÔNG có trong database! (Có thể chưa register hoặc đã bị xóa)
                      </span>
                    </>
                  )}
                </div>
              )}
            </div>
          ) : (
            <div className="flex items-center gap-2 text-gray-500">
              <AlertCircle className="w-5 h-5" />
              <p>Không có token trong localStorage. Bạn chưa enable notification.</p>
            </div>
          )}
        </div>

        {/* User Info */}
        {debugInfo && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              User Information
            </h2>
            <div className="grid grid-cols-3 gap-4">
              <div className="p-4 bg-blue-50 rounded-lg">
                <p className="text-sm text-blue-600 font-medium">User ID</p>
                <p className="text-xl font-bold text-blue-900">{debugInfo.userId}</p>
              </div>
              <div className="p-4 bg-green-50 rounded-lg">
                <p className="text-sm text-green-600 font-medium">Username</p>
                <p className="text-xl font-bold text-green-900">{debugInfo.username}</p>
              </div>
              <div className="p-4 bg-purple-50 rounded-lg">
                <p className="text-sm text-purple-600 font-medium">Total Tokens</p>
                <p className="text-xl font-bold text-purple-900">{debugInfo.totalTokens}</p>
              </div>
            </div>
          </div>
        )}

        {/* Tokens List */}
        {debugInfo && debugInfo.tokens.length > 0 && (
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              Registered Tokens in Database
            </h2>
            
            <div className="space-y-4">
              {debugInfo.tokens.map((token, index) => (
                <div 
                  key={token.id}
                  className={`p-4 border-2 rounded-lg ${
                    currentToken === token.fcmToken 
                      ? 'border-green-500 bg-green-50' 
                      : 'border-gray-200 bg-gray-50'
                  }`}
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex items-center gap-2">
                      <span className="px-3 py-1 bg-blue-100 text-blue-700 text-xs font-semibold rounded-full">
                        Token #{index + 1}
                      </span>
                      {currentToken === token.fcmToken && (
                        <span className="px-3 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded-full">
                          Current Browser Token
                        </span>
                      )}
                      {token.isActive ? (
                        <span className="px-3 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded-full">
                          Active
                        </span>
                      ) : (
                        <span className="px-3 py-1 bg-red-100 text-red-700 text-xs font-semibold rounded-full">
                          Inactive
                        </span>
                      )}
                    </div>
                  </div>
                  
                  <div className="space-y-2">
                    <div>
                      <p className="text-xs text-gray-500 mb-1">FCM Token</p>
                      <p className="font-mono text-xs text-gray-800 break-all bg-white p-2 rounded border">
                        {token.fcmToken}
                      </p>
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <p className="text-xs text-gray-500">Device Type</p>
                        <p className="text-sm font-medium text-gray-800">{token.deviceType}</p>
                      </div>
                      <div>
                        <p className="text-xs text-gray-500">Device Info</p>
                        <p className="text-sm font-medium text-gray-800 truncate" title={token.deviceInfo}>
                          {token.deviceInfo}
                        </p>
                      </div>
                      <div>
                        <p className="text-xs text-gray-500">Created At</p>
                        <p className="text-sm font-medium text-gray-800">
                          {new Date(token.createdAt).toLocaleString()}
                        </p>
                      </div>
                      <div>
                        <p className="text-xs text-gray-500">Last Used</p>
                        <p className="text-sm font-medium text-gray-800">
                          {new Date(token.lastUsedAt).toLocaleString()}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Troubleshooting Guide */}
        <div className="bg-yellow-50 border-l-4 border-yellow-500 p-6 mt-6">
          <h3 className="font-semibold text-yellow-900 mb-3 flex items-center gap-2">
            <AlertCircle className="w-5 h-5" />
            Troubleshooting UNREGISTERED Error
          </h3>
          <div className="space-y-2 text-sm text-yellow-800">
            <p><strong>1. Token không có trong database:</strong></p>
            <ul className="list-disc list-inside ml-4 space-y-1">
              <li>Bấm "Enable Notifications" để register token mới</li>
              <li>Kiểm tra xem bạn đã login chưa</li>
            </ul>
            
            <p className="mt-3"><strong>2. Token có trong database nhưng vẫn lỗi:</strong></p>
            <ul className="list-disc list-inside ml-4 space-y-1">
              <li>Token đã bị Firebase invalidate (token cũ, app reinstall, etc.)</li>
              <li>Kiểm tra Firebase Project ID có khớp không</li>
              <li>Kiểm tra VAPID key có đúng không</li>
              <li>Thử xóa token cũ và register lại</li>
            </ul>
            
            <p className="mt-3"><strong>3. Firebase Configuration:</strong></p>
            <ul className="list-disc list-inside ml-4 space-y-1">
              <li>Đảm bảo frontend và backend dùng cùng Firebase project</li>
              <li>Check service account key JSON có đúng không</li>
              <li>Verify VAPID key matches với Firebase Console</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
