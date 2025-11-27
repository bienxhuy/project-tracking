import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { TempPage } from "./pages/TempPage"
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
// import { NotificationManager } from "./components/NotificationManager"

import { ApiTestPage } from "./pages/ApiTestPage"
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
          {/* Main Application Routes */}
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />

            <Route path="student">
              <Route path="dashboard" element={<StudentDashboard />} />
            </Route>

            <Route path="instructor">
              <Route path="dashboard" element={<InstructorDashboard />} />
              <Route path="project/create" element={<ProjectEditorPage />} />
              <Route path="project/edit/:projectId" element={<ProjectEditorPage />} />
            </Route>

            <Route path="project/:id" element={<ProjectDetailPage />} />
            <Route path="project/:projectId/milestone/:milestoneId" element={<MilestoneDetailPage />} />
            <Route path="project/:projectId/milestone/:milestoneId/task/:taskId" element={<TaskDetailPage />} />


            <Route path="test-notification" element={<NotificationTestPage />} />
          </Route>

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
      <Toaster />
    </>
  )
}

export default App
