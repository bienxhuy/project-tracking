import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { TempPage } from "./pages/TempPage"
import { StudentDashboard } from "./pages/student/StudentDashboard"
import { ProjectDetailPage } from "./pages/ProjectDetail"
import { MilestoneDetailPage } from "./pages/MilestoneDetail"
import { ScrollToTop } from "./components/ScropToTop"

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

            <Route path="project/:id" element={<ProjectDetailPage />} />
            <Route path="project/:projectId/milestone/:milestoneId" element={<MilestoneDetailPage />} />
          </Route>

          <Route path="/temp" element={<TempPage />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
