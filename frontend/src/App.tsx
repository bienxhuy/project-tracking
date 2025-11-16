import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { TempPage } from "./pages/TempPage"
import { StudentDashboard } from "./pages/student/StudentDashboard"
import { ProjectDetail } from "./pages/ProjectDetail"

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route path="student">
              <Route path="dashboard" element={<StudentDashboard />} />
            </Route>
            
            <Route path="project/:projectId" element={<ProjectDetail />} />
          </Route>

          <Route path="/temp" element={<TempPage />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
