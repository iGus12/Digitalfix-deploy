const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request(path, options = {}, token = null) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  // Si nos pasan un token de Azure, lo inyectamos aquí
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Error ${response.status}`);
  }

  if (response.status === 204) return null;
  return response.json();
}

export const api = {
  // Ahora todas las funciones exigen el token para poder pasárselo al BFF
  getWorkorders: (token) => request('/api/workorders', {}, token),
  createWorkorder: (data, token) => request('/api/workorders', { method: 'POST', body: JSON.stringify(data) }, token),
  updateStatus: (id, status, token) => request(`/api/workorders/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }, token),
  getCatalog: (token) => request('/api/catalog/services', {}, token),
  getKpis: (token) => request('/api/report/kpis?range=last24h', {}, token),
  getAudit: (token) => request('/api/audit/events', {}, token),
  createCatalogItem: (data, token) => request('/api/catalog/services', { method: 'POST', body: JSON.stringify(data) }, token),
};

export { API_BASE_URL };
