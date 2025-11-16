import { useState } from 'react';
import { useNotification } from '@/hooks/useNotification';
import { sendTestNotification } from '@/services/notification.api';
import { Bell, CheckCircle, XCircle, Copy, Send, Loader2 } from 'lucide-react';

export default function NotificationTestPage() {
  const { fcmToken, permission, requestPermission, isSupported } = useNotification();
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<{ success: boolean; message: string } | null>(null);
  const [copied, setCopied] = useState(false);

  const handleRequestPermission = async () => {
    const token = await requestPermission();
    if (token) {
      setResult({ success: true, message: 'Permission granted! FCM token received.' });
    } else {
      setResult({ success: false, message: 'Permission denied or error occurred.' });
    }
  };

  const handleSendTest = async () => {
    if (!fcmToken) {
      setResult({ success: false, message: 'No FCM token available. Please enable notifications first.' });
      return;
    }

    setLoading(true);
    setResult(null);

    try {
      await sendTestNotification();
      setResult({ 
        success: true, 
        message: `Test notification sent successfully! Check your notifications.`
      });
    } catch (error: any) {
      setResult({ 
        success: false, 
        message: error.response?.data?.message || 'Failed to send test notification. Make sure you are logged in.'
      });
    } finally {
      setLoading(false);
    }
  };

  const copyToken = () => {
    if (fcmToken) {
      navigator.clipboard.writeText(fcmToken);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-blue-50 to-indigo-100 p-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-block p-4 bg-blue-500 rounded-full mb-4">
            <Bell className="w-12 h-12 text-white" />
          </div>
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Firebase Push Notification Test
          </h1>
          <p className="text-gray-600">
            Test Firebase Cloud Messaging integration
          </p>
        </div>

        {/* Browser Support Check */}
        {!isSupported && (
          <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
            <div className="flex">
              <XCircle className="w-5 h-5 text-red-500 mr-3 flex-shrink-0" />
              <div>
                <h3 className="text-red-800 font-semibold">Browser Not Supported</h3>
                <p className="text-red-700 text-sm mt-1">
                  Your browser doesn't support push notifications. Please use Chrome, Firefox, or Edge.
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Main Cards */}
        <div className="grid gap-6 mb-6">
          {/* Status Card */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
              <CheckCircle className="w-6 h-6 text-blue-500" />
              Notification Status
            </h2>
            
            <div className="space-y-4">
              {/* Permission Status */}
              <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                <div>
                  <p className="text-sm font-medium text-gray-700">Permission Status</p>
                  <p className="text-xs text-gray-500 mt-1">Current notification permission state</p>
                </div>
                <div>
                  {permission === 'granted' && (
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                      <CheckCircle className="w-4 h-4 mr-1" />
                      Granted
                    </span>
                  )}
                  {permission === 'denied' && (
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                      <XCircle className="w-4 h-4 mr-1" />
                      Denied
                    </span>
                  )}
                  {permission === 'default' && (
                    <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                      Default
                    </span>
                  )}
                </div>
              </div>

              {/* FCM Token */}
              <div className="p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-sm font-medium text-gray-700">FCM Token</p>
                  {fcmToken && (
                    <button
                      onClick={copyToken}
                      className="text-blue-600 hover:text-blue-700 text-sm flex items-center gap-1"
                    >
                      <Copy className="w-4 h-4" />
                      {copied ? 'Copied!' : 'Copy'}
                    </button>
                  )}
                </div>
                {fcmToken ? (
                  <div className="bg-white p-3 rounded border border-gray-200 font-mono text-xs break-all">
                    {fcmToken}
                  </div>
                ) : (
                  <p className="text-sm text-gray-500 italic">No token available. Enable notifications first.</p>
                )}
              </div>
            </div>
          </div>

          {/* Actions Card */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
              <Send className="w-6 h-6 text-blue-500" />
              Test Actions
            </h2>

            <div className="space-y-4">
              {/* Request Permission */}
              {permission !== 'granted' && (
                <div className="p-4 bg-blue-50 rounded-lg">
                  <h3 className="font-medium text-gray-900 mb-2">Step 1: Enable Notifications</h3>
                  <p className="text-sm text-gray-600 mb-3">
                    Click the button below to request notification permission from your browser.
                  </p>
                  <button
                    onClick={handleRequestPermission}
                    className="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-3 px-4 rounded-lg flex items-center justify-center gap-2 transition-colors"
                  >
                    <Bell className="w-5 h-5" />
                    Request Notification Permission
                  </button>
                </div>
              )}

              {/* Send Test Notification */}
              {permission === 'granted' && (
                <div className="p-4 bg-green-50 rounded-lg">
                  <h3 className="font-medium text-gray-900 mb-2">Step 2: Send Test Notification</h3>
                  <p className="text-sm text-gray-600 mb-3">
                    Send a test notification from the backend to verify the integration.
                  </p>
                  <button
                    onClick={handleSendTest}
                    disabled={loading || !fcmToken}
                    className="w-full bg-green-500 hover:bg-green-600 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-medium py-3 px-4 rounded-lg flex items-center justify-center gap-2 transition-colors"
                  >
                    {loading ? (
                      <>
                        <Loader2 className="w-5 h-5 animate-spin" />
                        Sending...
                      </>
                    ) : (
                      <>
                        <Send className="w-5 h-5" />
                        Send Test Notification
                      </>
                    )}
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Result Card */}
          {result && (
            <div className={`rounded-lg shadow-lg p-6 ${
              result.success ? 'bg-green-50 border-l-4 border-green-500' : 'bg-red-50 border-l-4 border-red-500'
            }`}>
              <div className="flex items-start gap-3">
                {result.success ? (
                  <CheckCircle className="w-6 h-6 text-green-500 flex-shrink-0 mt-0.5" />
                ) : (
                  <XCircle className="w-6 h-6 text-red-500 flex-shrink-0 mt-0.5" />
                )}
                <div>
                  <h3 className={`font-semibold ${result.success ? 'text-green-800' : 'text-red-800'}`}>
                    {result.success ? 'Success!' : 'Error'}
                  </h3>
                  <p className={`text-sm mt-1 ${result.success ? 'text-green-700' : 'text-red-700'}`}>
                    {result.message}
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Info Card */}
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-3">ℹ️ How It Works</h2>
          <div className="space-y-2 text-sm text-gray-600">
            <p>1. <strong>Request Permission:</strong> Browser will ask for notification permission</p>
            <p>2. <strong>Get FCM Token:</strong> Firebase generates a unique token for this device</p>
            <p>3. <strong>Register Token:</strong> Token is automatically sent to backend</p>
            <p>4. <strong>Send Test:</strong> Backend uses the token to send a notification via Firebase</p>
            <p>5. <strong>Receive:</strong> You should see a notification (foreground or background)</p>
          </div>

          <div className="mt-4 pt-4 border-t border-gray-200">
            <p className="text-xs text-gray-500">
              <strong>Note:</strong> Make sure you're logged in to test the notification. 
              The backend requires authentication to send notifications.
            </p>
          </div>
        </div>

        {/* Debug Info */}
        {process.env.NODE_ENV === 'development' && (
          <div className="mt-6 bg-gray-800 text-white rounded-lg shadow-lg p-6">
            <h2 className="text-lg font-semibold mb-3">🔧 Debug Info</h2>
            <div className="space-y-2 text-xs font-mono">
              <p>Browser Supported: {isSupported ? '✅' : '❌'}</p>
              <p>Permission: {permission}</p>
              <p>Has Token: {fcmToken ? '✅' : '❌'}</p>
              <p>API URL: {import.meta.env.VITE_API_BASE_URL || 'Not configured'}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
