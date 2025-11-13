import { BrowserRouter, Route, Routes } from "react-router-dom"
import MainLayout from "./components/MainLayout"
import { TempPage } from "./pages/TempPage"

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route element={<MainLayout />}>
            <Route path="/temp" element={<TempPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
