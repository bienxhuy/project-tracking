import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { StudentDashboard } from "./pages/student/StudentDashboard"
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
import { NotificationManager } from "./components/NotificationManager"

import { InstructorDashboard } from "./pages/instructor/InstructorDashboard"
import { ProjectEditorPage } from "./pages/instructor/ProjectEditorPage"
import { ProjectDetailPage } from "./pages/ProjectDetail"
import { MilestoneDetailPage } from "./pages/MilestoneDetail"
import { ScrollToTop } from "./components/ScropToTop"
import { TaskDetailPage } from "./pages/TaskDetail"
import { Toaster } from "@/components/ui/sonner"

function App() {
  return (
    <>
      <BrowserRouter>
        <ScrollToTop />
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

          {/* Protected Routes - All authenticated users can access */}
          <Route 
            path="/" 
            element={
              <ProtectedRoute>
                <MainLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<HomePage />} />

            {/* Student Routes - Protected by Role (STUDENT only) */}
            <Route 
              path="student/dashboard" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.STUDENT]}>
                  <StudentDashboard />
                </RoleBasedRoute>
              }
            />

            {/* Instructor Routes - Protected by Role (INSTRUCTOR only) */}
            <Route 
              path="instructor/dashboard" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.INSTRUCTOR]}>
                  <InstructorDashboard />
                </RoleBasedRoute>
              }
            />
            <Route 
              path="instructor/project/create" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.INSTRUCTOR]}>
                  <ProjectEditorPage />
                </RoleBasedRoute>
              }
            />
            <Route 
              path="instructor/project/edit/:projectId" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.INSTRUCTOR]}>
                  <ProjectEditorPage />
                </RoleBasedRoute>
              }
            />

            {/* Project Routes - Protected by Role (STUDENT and INSTRUCTOR only) */}
            <Route 
              path="project/:id" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.STUDENT, UserRole.INSTRUCTOR]}>
                  <ProjectDetailPage />
                </RoleBasedRoute>
              } 
            />
            <Route 
              path="project/:projectId/milestone/:milestoneId" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.STUDENT, UserRole.INSTRUCTOR]}>
                  <MilestoneDetailPage />
                </RoleBasedRoute>
              } 
            />
            <Route 
              path="project/:projectId/milestone/:milestoneId/task/:taskId" 
              element={
                <RoleBasedRoute allowedRoles={[UserRole.STUDENT, UserRole.INSTRUCTOR]}>
                  <TaskDetailPage />
                </RoleBasedRoute>
              } 
            />

            {/* Test Notification Route - All authenticated users can access */}
            <Route path="test-notification" element={<NotificationTestPage />} />
          </Route>

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
        <NotificationManager />
      </BrowserRouter>
      <Toaster />
    </>
  )
}

export default App
