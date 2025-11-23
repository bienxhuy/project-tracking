import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { TempPage } from "./pages/TempPage"
import { StudentDashboard } from "./pages/student/StudentDashboard"
import { ProjectDetail } from "./pages/ProjectDetail"
import { AdminLayout } from "./pages/admin/AdminLayout"
import { AdminDashboard } from "./pages/admin/AdminDashboard"
import { ManageUsers } from "./pages/admin/ManageUsers"
import NotificationTestPage from "./pages/NotificationTestPage"
import HomePage from "./pages/HomePage"
import LoginPage from "./pages/LoginPage"
import { PublicRoute } from "./components/auth/PublicRoute"
import { ProtectedRoute } from "./components/auth/ProtectedRoute"
import { RoleBasedRoute } from "./components/auth/RoleBasedRoute"
import { UserRole } from "./types/user.type"

import { ApiTestPage } from "./pages/ApiTestPage"

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          {/* Public Route - Login Page */}
          <Route 
            path="/login" 
            element={
              <PublicRoute>
                <LoginPage />
              </PublicRoute>
            } 
          />

          {/* Protected Home Route - Redirect to login if no token */}
          <Route 
            index 
            path="/" 
            element={
              <ProtectedRoute>
                <HomePage />
              </ProtectedRoute>
            } 
          />

          {/* Student Routes - Protected by Role (STUDENT only) */}
          <Route 
            path="/test-api" 
            element={
              <RoleBasedRoute allowedRoles={[UserRole.STUDENT]}>
                <ApiTestPage />
              </RoleBasedRoute>
            }
          />

          {/* Instructor Routes - Protected by Role (INSTRUCTOR only) */}
          <Route 
            path="/temp-page" 
            element={
              <RoleBasedRoute allowedRoles={[UserRole.INSTRUCTOR]}>
                <TempPage />
              </RoleBasedRoute>
            }
          />

          {/* Admin Routes - Protected by Role (ADMIN only) */}
          <Route 
            path="/admin" 
            element={
              <RoleBasedRoute allowedRoles={[UserRole.ADMIN]}>
                <AdminLayout />
              </RoleBasedRoute>
            }
          >
            <Route index element={<AdminDashboard />} />
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="users" element={<ManageUsers />} />
          </Route>
        </Routes>
        
        {/* Notification Manager - Active on all pages */}
        {/* <NotificationManager /> */}
      </BrowserRouter>
    </>
  )
}

export default App
