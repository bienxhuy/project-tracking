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
import { NotificationManager } from "./components/NotificationManager"

import { ApiTestPage } from "./pages/ApiTestPage"

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            
            <Route path="student">
              <Route path="dashboard" element={<StudentDashboard />} />
            </Route>
            
            <Route path="project/:projectId" element={<ProjectDetail />} />
            
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
    </>
  )
}

export default App
