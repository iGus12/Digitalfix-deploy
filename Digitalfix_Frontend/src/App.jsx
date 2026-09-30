import { Navigate, Route, Routes } from 'react-router-dom'
import { useIsAuthenticated } from '@azure/msal-react'

import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'

import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Workorders from './pages/Workorders'
import Catalog from './pages/Catalog'
import Reports from './pages/Reports'
import Audit from './pages/Audit'

const withLayout = (element, roles) => (
  <ProtectedRoute allowedRoles={roles}>
    <Layout>
      {element}
    </Layout>
  </ProtectedRoute>
)

export default function App() {
  const isAuthenticated = useIsAuthenticated()

  return (
    <Routes>

      <Route
        path="/login"
        element={
          isAuthenticated
            ? <Navigate to="/dashboard" replace />
            : <Login />
        }
      />

      <Route
        path="/dashboard"
        element={
          isAuthenticated
            ? <Layout><Dashboard /></Layout>
            : <Navigate to="/login" replace />
        }
      />

      <Route
        path="/workorders"
        element={withLayout(
          <Workorders />,
          ['ADMIN', 'SUPERVISOR', 'CLIENTE']
        )}
      />

      <Route
        path="/catalog"
        element={withLayout(
          <Catalog />,
          ['ADMIN', 'SUPERVISOR']
        )}
      />

      <Route
        path="/reports"
        element={withLayout(
          <Reports />,
          ['ADMIN']
        )}
      />

      <Route
        path="/audit"
        element={withLayout(
          <Audit />,
          ['ADMIN', 'AUDITOR']
        )}
      />

      <Route
        path="*"
        element={
          <Navigate
            to={isAuthenticated ? '/dashboard' : '/login'}
            replace
          />
        }
      />

    </Routes>
  )
}