import { Link } from 'react-router-dom';
import { Bell, Home } from 'lucide-react';

export default function HomePage() {
  return (
    <div className="min-h-screen bg-linear-to-br from-purple-50 to-blue-100 flex items-center justify-center p-8">
      <div className="max-w-2xl w-full">
        <div className="text-center mb-12">
          <h1 className="text-5xl font-bold text-gray-900 mb-4">
            Project Tracking System
          </h1>
          <p className="text-xl text-gray-600">
            Firebase Push Notification Demo
          </p>
        </div>

        <div className="grid gap-6">
          {/* Notification Test Card */}
          <Link
            to="/test-notification"
            className="bg-white rounded-lg shadow-lg p-8 hover:shadow-xl transition-all transform hover:-translate-y-1"
          >
            <div className="flex items-center gap-4">
              <div className="bg-blue-500 p-4 rounded-full">
                <Bell className="w-8 h-8 text-white" />
              </div>
              <div className="flex-1">
                <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                  Test Push Notifications
                </h2>
                <p className="text-gray-600">
                  Test Firebase Cloud Messaging integration, request permissions, and send test notifications.
                </p>
              </div>
              <svg
                className="w-6 h-6 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 5l7 7-7 7"
                />
              </svg>
            </div>
          </Link>

          {/* Temp Page Card */}
          <Link
            to="/temp"
            className="bg-white rounded-lg shadow-lg p-8 hover:shadow-xl transition-all transform hover:-translate-y-1"
          >
            <div className="flex items-center gap-4">
              <div className="bg-purple-500 p-4 rounded-full">
                <Home className="w-8 h-8 text-white" />
              </div>
              <div className="flex-1">
                <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                  Temporary Page
                </h2>
                <p className="text-gray-600">
                  View the temporary testing page.
                </p>
              </div>
              <svg
                className="w-6 h-6 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 5l7 7-7 7"
                />
              </svg>
            </div>
          </Link>
        </div>

        {/* Info Box */}
        <div className="mt-12 bg-white rounded-lg shadow-lg p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3">
            🚀 Quick Start Guide
          </h3>
          <ol className="space-y-2 text-sm text-gray-600">
            <li>1. Click "Test Push Notifications" above</li>
            <li>2. Click "Request Notification Permission"</li>
            <li>3. Allow notifications in your browser</li>
            <li>4. Click "Send Test Notification"</li>
            <li>5. You should receive a notification!</li>
          </ol>
        </div>

        {/* Footer */}
        <div className="mt-8 text-center text-sm text-gray-500">
          <p>Built with React + TypeScript + Firebase Cloud Messaging</p>
        </div>
      </div>
    </div>
  );
}
