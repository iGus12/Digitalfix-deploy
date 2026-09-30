import { NavLink } from 'react-router-dom'
import {
  LayoutDashboard,
  ClipboardList,
  PackageSearch,
  BarChart3,
  ShieldCheck,
  LogOut,
  Zap,
  Menu,
  X
} from 'lucide-react'

import { useState } from 'react'
import { useMsal } from '@azure/msal-react'

const links = [
  {
    to: '/dashboard',
    label: 'Dashboard',
    icon: LayoutDashboard,
    roles: ['ADMIN', 'SUPERVISOR', 'CLIENTE', 'AUDITOR']
  },
  {
    to: '/workorders',
    label: 'Órdenes',
    icon: ClipboardList,
    roles: ['ADMIN', 'SUPERVISOR', 'CLIENTE']
  },
  {
    to: '/catalog',
    label: 'Catálogo',
    icon: PackageSearch,
    roles: ['ADMIN', 'SUPERVISOR']
  },
  {
    to: '/reports',
    label: 'Reportería',
    icon: BarChart3,
    roles: ['ADMIN']
  },
  {
    to: '/audit',
    label: 'Auditoría',
    icon: ShieldCheck,
    roles: ['ADMIN', 'AUDITOR']
  },
]

export default function Layout({ children }) {
  const [open, setOpen] = useState(false)

  const { instance, accounts } = useMsal()

  const account =
    instance.getActiveAccount() ||
    accounts[0]

  // Datos reales entregados por Microsoft
  const name =
    account?.name ||
    'Usuario'

  const email =
    account?.username ||
    ''

  // Roles provenientes de Microsoft Entra ID.
  // Todavía puede venir vacío porque aún no configuramos los App Roles.
  const roles =
    (account?.idTokenClaims?.roles || [])
      .map(role => role.toUpperCase())

  console.log('Roles de Entra ID:', roles)

  const primaryRole =
    roles.length > 0
      ? roles[0]
      : 'USUARIO MICROSOFT'

  /*
    TEMPORAL:
    Mientras todavía no configuramos los roles en Entra ID,
    mostramos todas las opciones del menú.

    Cuando configuremos ADMIN, SUPERVISOR, CLIENTE y AUDITOR,
    este mismo código filtrará automáticamente el menú.
  */
  const visibleLinks =
    roles.length === 0
      ? links
      : links.filter((link) =>
          link.roles.some((role) =>
            roles.includes(role)
          )
        )

  const logout = async () => {
    try {
      await instance.logoutRedirect({
        account,
        postLogoutRedirectUri: 'http://localhost:5173/login'
      })
    } catch (error) {
      console.error(
        'Error al cerrar sesión:',
        error
      )
    }
  }

  return (
    <div className="app-shell">

      <aside
        className={`sidebar ${
          open ? 'sidebar-open' : ''
        }`}
      >

        <div className="brand">

          <div className="brand-mark">
            <Zap size={22} />
          </div>

          <div>
            <strong>DigitalFix</strong>
            <span>Field Service</span>
          </div>

        </div>

        <button
          className="mobile-close"
          onClick={() => setOpen(false)}
          aria-label="Cerrar menú"
        >
          <X />
        </button>

        <nav>

          {visibleLinks.map(
            ({ to, label, icon: Icon }) => (

              <NavLink
                key={to}
                to={to}
                onClick={() => setOpen(false)}
                className={({ isActive }) =>
                  `nav-link ${
                    isActive ? 'active' : ''
                  }`
                }
              >

                <Icon size={19} />

                {label}

              </NavLink>

            )
          )}

        </nav>

        <div className="sidebar-user">

          <div className="avatar">
            {name.charAt(0).toUpperCase()}
          </div>

          <div className="user-meta">

            <strong>
              {name}
            </strong>

            <span>
              {primaryRole}
            </span>

            {email && (
              <small>
                {email}
              </small>
            )}

          </div>

          <button
            onClick={logout}
            className="icon-button"
            title="Cerrar sesión"
          >
            <LogOut size={18} />
          </button>

        </div>

      </aside>

      <div className="main-area">

        <header className="topbar">

          <button
            className="menu-button"
            onClick={() => setOpen(true)}
          >
            <Menu />
          </button>

          <div>

            <strong>
              Plataforma de mantenimiento eléctrico
            </strong>

            <span>
              Operación y trazabilidad en tiempo real
            </span>

          </div>

          <div className="status-pill">
            <span></span>
            Sistema operativo
          </div>

        </header>

        <main className="content">
          {children}
        </main>

      </div>

      {open && (
        <div
          className="overlay"
          onClick={() => setOpen(false)}
        />
      )}

    </div>
  )
}