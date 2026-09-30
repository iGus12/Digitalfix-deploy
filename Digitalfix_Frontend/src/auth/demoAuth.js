const USER_KEY = 'digitalfix_user'

export const demoUsers = {
  ADMIN: { name: 'Carla Admin', email: 'admin@digitalfix.cl', role: 'ADMIN' },
  SUPERVISOR: { name: 'Supervisor Operaciones', email: 'supervisor@digitalfix.cl', role: 'SUPERVISOR' },
  CLIENTE: { name: 'Cliente Demo', email: 'cliente@digitalfix.cl', role: 'CLIENTE' },
  AUDITOR: { name: 'Auditor Demo', email: 'auditor@digitalfix.cl', role: 'AUDITOR' },
}

export function loginDemo(role) {
  const user = demoUsers[role] || demoUsers.CLIENTE
  localStorage.setItem(USER_KEY, JSON.stringify(user))
  return user
}

export function logoutDemo() {
  localStorage.removeItem(USER_KEY)
}

export function getCurrentUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY))
  } catch {
    return null
  }
}
