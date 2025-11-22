import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { ToastProvider } from './components/ui/toast'
import { AuthProvider } from './contexts/AuthContext'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ToastProvider>
      {/* <AuthProvider> */}
        <App />
      {/* </AuthProvider> */}
    </ToastProvider>
  </StrictMode>,
)
