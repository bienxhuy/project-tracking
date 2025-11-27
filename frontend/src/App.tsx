import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { TempPage } from "./pages/TempPage"
import { StudentDashboard } from "./pages/student/StudentDashboard"
import { AdminLayout } from "./pages/admin/AdminLayout"
import { AdminDashboard } from "./pages/admin/AdminDashboard"
import { ManageUsers } from "./pages/admin/ManageUsers"
import NotificationTestPage from "./pages/NotificationTestPage"
import HomePage from "./pages/HomePage"
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

            <Route path="test-api" element={<ApiTestPage />} />
          </Route>

          {/* Admin Routes */}
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<AdminDashboard />} />
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="users" element={<ManageUsers />} />
          </Route>

          <Route path="/temp" element={<TempPage />} />
        </Routes>

        {/* Notification Manager - Active on all pages */}
        {/* <NotificationManager /> */}
      </BrowserRouter>
      <Toaster />
    </>
  )
}

export default App
