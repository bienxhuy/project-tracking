import { BrowserRouter, Route, Routes } from "react-router-dom"

import { MainLayout } from "./pages/MainLayout"
import { StudentLayout } from "./pages/student/StudentLayout"

import { TempPage } from "./pages/TempPage"
import { StudentDashboard } from "./pages/student/StudentDashboard"

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/student" element={<StudentLayout />}>
            <Route path="dashboard" element={<StudentDashboard />} />
          </Route>

          <Route element={<MainLayout />}>
            <Route path="/temp" element={<TempPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
