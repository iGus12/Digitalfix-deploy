
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
  X,
  Bell
} from 'lucide-react'

import { useState } from 'react'
import { useMsal } from '@azure/msal-react'
import { api } from '../services/api'
import { loginRequest } from '../auth/msalConfig'

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
  const [notificacionesAbiertas, setNotificacionesAbiertas] = useState(false)

  const [notificaciones, setNotificaciones] = useState([])
  const [cantidadNoLeidas, setCantidadNoLeidas] = useState(0)
  const [cargandoNotificaciones, setCargandoNotificaciones] = useState(false)
  const [errorNotificaciones, setErrorNotificaciones] = useState('')

  const { instance, accounts } = useMsal()

  const account =
    instance.getActiveAccount() ||
    accounts[0]

  // Obtener token de Microsoft para consultar el BFF
  const obtenerToken = async () => {
    if (!account) {
      throw new Error('No hay una sesión activa.')
    }

    const respuesta = await instance.acquireTokenSilent({
      ...loginRequest,
      account
    })

    const token = respuesta.accessToken

    return token

  // Consultar notificaciones del usuario autenticado
  const cargarNotificaciones = async () => {
    if (!account) return

    setCargandoNotificaciones(true)
    setErrorNotificaciones('')

    try {
      const token = await obtenerToken()

      const lista = await api.getNotificaciones(token)
      const cantidad = await api.getNotificacionesNoLeidas(token)

      setNotificaciones(Array.isArray(lista) ? lista : [])
      setCantidadNoLeidas(Number(cantidad) || 0)

    } catch (error) {
      console.error('Error al consultar notificaciones:', error)

      setErrorNotificaciones(
        'No se pudieron cargar las notificaciones.'
      )
    } finally {
      setCargandoNotificaciones(false)
    }
  }

  // Marcar una notificación como leída
  const marcarComoLeida = async (id) => {
    try {
      const token = await obtenerToken()

      await api.marcarNotificacionLeida(id, token)

      setNotificaciones((anteriores) =>
        anteriores.map((notificacion) =>
          notificacion.id === id
            ? { ...notificacion, leida: true }
            : notificacion
        )
      )

      setCantidadNoLeidas((cantidad) =>
        Math.max(0, cantidad - 1)
      )

    } catch (error) {
      console.error('Error al marcar notificación:', error)

      setErrorNotificaciones(
        'No se pudo marcar la notificación como leída.'
      )
    }
  }

  // Datos reales del usuario autenticado
  const name =
    account?.name ||
    'Usuario'

  const email =
    account?.username ||
    ''

  // Roles de Microsoft Entra ID
  const roles =
    (account?.idTokenClaims?.roles || [])
      .map(role => role.toUpperCase())

  const primaryRole =
    roles.length > 0
      ? roles[0]
      : 'USUARIO MICROSOFT'

  // Mantener comportamiento actual de los roles
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
        postLogoutRedirectUri: `${window.location.origin}/login`
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

            <strong>{name}</strong>

            <span>{primaryRole}</span>

            {email && (
              <small>{email}</small>
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
            aria-label="Abrir menú"
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

          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '14px'
            }}
          >

            <div
              style={{
                position: 'relative'
              }}
            >

              <button
                type="button"
                className="icon-button"
                aria-label="Notificaciones"
                aria-expanded={notificacionesAbiertas}
                onClick={() => {
                  if (!notificacionesAbiertas) {
                    cargarNotificaciones()
                  }

                  setNotificacionesAbiertas(
                    !notificacionesAbiertas
                  )
                }}
              >
                <Bell size={21} />

                {cantidadNoLeidas > 0 && (
                  <span
                    style={{
                      position: 'absolute',
                      top: '-5px',
                      right: '-7px',
                      background: '#dc2626',
                      color: '#fff',
                      borderRadius: '999px',
                      minWidth: '18px',
                      height: '18px',
                      padding: '0 4px',
                      fontSize: '10px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}
                  >
                    {cantidadNoLeidas > 99
                      ? '99+'
                      : cantidadNoLeidas}
                  </span>
                )}
              </button>

              {notificacionesAbiertas && (
                <div
                  style={{
                    position: 'absolute',
                    top: '42px',
                    right: 0,
                    width: '300px',
                    maxWidth: 'calc(100vw - 32px)',
                    padding: '16px',
                    background: '#fff',
                    color: '#1f2937',
                    border: '1px solid #e5e7eb',
                    borderRadius: '12px',
                    boxShadow: '0 8px 24px rgba(0,0,0,0.15)',
                    zIndex: 1000
                  }}
                >

                  <strong>Notificaciones</strong>

                  {cantidadNoLeidas > 0 && (
                    <span
                      style={{
                        marginLeft: '10px',
                        color: '#2563eb',
                        fontSize: '12px'
                      }}
                    >
                      {cantidadNoLeidas} sin leer
                    </span>
                  )}

                  {cargandoNotificaciones ? (

                    <p
                      style={{
                        fontSize: '13px',
                        marginTop: '12px'
                      }}
                    >
                      Cargando notificaciones...
                    </p>

                  ) : errorNotificaciones ? (

                    <p
                      style={{
                        color: '#dc2626',
                        fontSize: '13px',
                        marginTop: '12px'
                      }}
                    >
                      {errorNotificaciones}
                    </p>

                  ) : notificaciones.length === 0 ? (

                    <p
                      style={{
                        fontSize: '13px',
                        marginTop: '12px'
                      }}
                    >
                      No tienes notificaciones por ahora.
                    </p>

                  ) : (

                    <div
                      style={{
                        marginTop: '12px',
                        maxHeight: '300px',
                        overflowY: 'auto'
                      }}
                    >

                      {notificaciones.map((notificacion) => (

                        <div
                          key={notificacion.id}
                          style={{
                            padding: '10px 0',
                            borderBottom: '1px solid #e5e7eb',
                            opacity: notificacion.leida ? 0.6 : 1
                          }}
                        >

                          <strong
                            style={{
                              fontSize: '13px'
                            }}
                          >
                            {notificacion.titulo}
                          </strong>

                          <p
                            style={{
                              fontSize: '12px',
                              margin: '5px 0'
                            }}
                          >
                            {notificacion.mensaje}
                          </p>

                          {!notificacion.leida && (

                            <button
                              type="button"
                              onClick={() =>
                                marcarComoLeida(notificacion.id)
                              }
                              style={{
                                background: 'none',
                                border: 'none',
                                padding: 0,
                                color: '#2563eb',
                                cursor: 'pointer',
                                fontSize: '12px'
                              }}
                            >
                              Marcar como leída
                            </button>

                          )}

                        </div>

                      ))}

                    </div>

                  )}

                </div>
              )}

            </div>

            <div className="status-pill">
              <span></span>
              Sistema operativo
            </div>

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
}