import { BrowserRouter, Route, Routes } from "react-router-dom"
import MainLayout from "./components/MainLayout"
import { TempPage } from "./pages/TempPage"
import NotificationTestPage from "./pages/NotificationTestPage"
import HomePage from "./pages/HomePage"
import { NotificationManager } from "./components/NotificationManager"

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route element={<MainLayout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/temp" element={<TempPage />} />
            <Route path="/test-notification" element={<NotificationTestPage />} />
          </Route>
        </Routes>
        
        {/* Notification Manager - Active on all pages */}
        <NotificationManager />
      </BrowserRouter>
    </>
  )
}

export default App
