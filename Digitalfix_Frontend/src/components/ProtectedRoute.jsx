import { Navigate } from 'react-router-dom'
import { useIsAuthenticated, useMsal } from '@azure/msal-react'

export default function ProtectedRoute({ children, allowedRoles }) {
  const isAuthenticated = useIsAuthenticated()
  const { instance, accounts } = useMsal()

  const account =
    instance.getActiveAccount() ||
    accounts[0]

  // Si no hay sesión iniciada con Microsoft
  if (!isAuthenticated || !account) {
    return <Navigate to="/login" replace />
  }

  // Roles entregados por Microsoft Entra ID
  const roles =
    (account?.idTokenClaims?.roles || [])
      .map(role => role.toUpperCase())

  /*
    Mientras todavía no configuremos App Roles
    en Microsoft Entra ID, dejamos pasar al usuario
    autenticado a todas las rutas.
  */
  if (roles.length === 0) {
    return children
  }

  // Si la ruta no exige roles específicos
  if (!allowedRoles || allowedRoles.length === 0) {
    return children
  }

  // Verificar si el usuario tiene al menos uno
  // de los roles permitidos
  const hasAccess = allowedRoles.some((role) =>
    roles.includes(role)
  )

  if (!hasAccess) {
    return <Navigate to="/dashboard" replace />
  }

  return children
}
