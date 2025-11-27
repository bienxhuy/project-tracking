import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { TempPage } from "./pages/TempPage"
import { StudentDashboard } from "./pages/student/StudentDashboard"
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
          <Route path="/" element={<MainLayout />}>
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
          </Route>

          <Route path="/temp" element={<TempPage />} />
        </Routes>
      </BrowserRouter>
      <Toaster />
    </>
  )
}

export default App
