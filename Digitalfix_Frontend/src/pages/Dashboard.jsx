import { useState, useEffect } from 'react';
import { useMsal } from '@azure/msal-react';
import { api } from '../services/api';
import { ArrowUpRight, AlertTriangle, CheckCircle, Clock, FileText } from 'lucide-react';

export default function Dashboard() {
  const { instance, accounts } = useMsal();
  const account = instance.getActiveAccount() || accounts[0];
  const userName = account?.name ? account.name.split(' ')[0] : 'AGUSTIN';

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const response = await instance.acquireTokenSilent({
          scopes: [`${import.meta.env.VITE_AZURE_CLIENT_ID}/.default`], 
          account: accounts[0]
        });
        const data = await api.getWorkorders(response.accessToken);
        setOrders(data || []);
      } catch (error) {
        console.error("Error al cargar el dashboard:", error);
      } finally {
        setLoading(false);
      }
    };

    if (accounts.length > 0) {
      fetchDashboardData();
    }
  }, [instance, accounts]);

  // Cálculos automáticos para los KPIs basados en Oracle
  const activeOrdersCount = orders.filter(o => o.status && o.status.toUpperCase() !== 'CERRADA').length;
  const closedOrdersCount = orders.filter(o => o.status && o.status.toUpperCase() === 'CERRADA').length;
  const pendingAttentionCount = orders.filter(o => !o.technician && !o.tech).length;

  // Conteo para las barras de estado de la red
  const inExecution = orders.filter(o => o.status && o.status.toUpperCase().includes('EJECUCI')).length;
  const assigned = orders.filter(o => o.status && o.status.toUpperCase() === 'ASIGNADA').length;
  const created = orders.filter(o => o.status && o.status.toUpperCase() === 'CREADA').length;

  // Últimas 5 órdenes para la tabla resumen
  const recentOrders = orders.slice(0, 5);

  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">RESUMEN OPERACIONAL</span>
          <h1>Hola, {userName.toUpperCase()}</h1>
          <p>Este es el estado actual de las operaciones de DigitalFix.</p>
        </div>
        <button className="btn secondary" onClick={() => window.location.reload()}>
          Actualizar <ArrowUpRight size={16} />
        </button>
      </div>

      <section className="summary-strip" style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '15px', marginBottom: '25px' }}>
        <article className="card" style={{ padding: '20px' }}>
          <div style={{ color: '#64748b', fontSize: '13px', marginBottom: '5px' }}>Órdenes activas</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>{loading ? '...' : activeOrdersCount}</div>
          <div style={{ fontSize: '12px', color: '#10b981', marginTop: '5px' }}>ordenes</div>
        </article>

        <article className="card" style={{ padding: '20px' }}>
          <div style={{ color: '#64748b', fontSize: '13px', marginBottom: '5px' }}>Tiempo promedio</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>42 min</div>
          <div style={{ fontSize: '12px', color: '#10b981', marginTop: '5px' }}>-5 min esta semana</div>
        </article>

        <article className="card" style={{ padding: '20px' }}>
          <div style={{ color: '#64748b', fontSize: '13px', marginBottom: '5px' }}>Cerradas totales</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>{loading ? '...' : closedOrdersCount}</div>
          <div style={{ fontSize: '12px', color: '#10b981', marginTop: '5px' }}>Alta eficiencia SLA</div>
        </article>

        <article className="card" style={{ padding: '20px' }}>
          <div style={{ color: '#64748b', fontSize: '13px', marginBottom: '5px' }}>Requieren atención</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#f59e0b' }}>{loading ? '...' : pendingAttentionCount}</div>
          <div style={{ fontSize: '12px', color: '#f59e0b', marginTop: '5px' }}>Sin técnico asignado</div>
        </article>
      </section>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
        {/* Tabla de Órdenes Recientes */}
        <article className="card table-card" style={{ padding: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
            <h3>Órdenes recientes</h3>
            <span style={{ fontSize: '13px', color: '#3b82f6', cursor: 'pointer' }} onClick={() => window.location.href='/workorders'}>Ver todas</span>
          </div>
          <div className="table-wrap">
            {loading ? (
              <p style={{ padding: '20px', textAlign: 'center' }}>Cargando actividad...</p>
            ) : recentOrders.length === 0 ? (
              <p style={{ padding: '20px', textAlign: 'center' }}>No hay actividad reciente.</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Orden</th>
                    <th>Cliente</th>
                    <th>Estado</th>
                    <th>Técnico</th>
                  </tr>
                </thead>
                <tbody>
                  {recentOrders.map((o, index) => (
                    <tr key={o.id || index}>
                      <td><strong>OT-{o.id || index + 1}</strong></td>
                      <td>{o.clientName || o.client}</td>
                      <td>
                        <span className={`badge ${o.status && o.status.toUpperCase() === 'CERRADA' ? 'closed' : 'warning'}`}>
                          {o.status || 'CREADA'}
                        </span>
                      </td>
                      <td>{o.technician || o.tech || 'Sin asignar'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </article>

        {/* Estado de la red / Distribución */}
        <article className="card" style={{ padding: '20px' }}>
          <h3 style={{ marginBottom: '15px' }}>Estado de la red</h3>
          <p style={{ fontSize: '12px', color: '#64748b', marginBottom: '20px' }}>Distribución en tiempo real</p>

          <div style={{ marginBottom: '15px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '5px' }}>
              <span>En ejecución</span>
              <b>{inExecution}</b>
            </div>
            <div style={{ background: '#e2e8f0', height: '8px', borderRadius: '4px' }}>
              <div style={{ background: '#3b82f6', width: `${Math.min(inExecution * 15, 100)}%`, height: '100%', borderRadius: '4px' }}></div>
            </div>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '5px' }}>
              <span>Asignadas</span>
              <b>{assigned}</b>
            </div>
            <div style={{ background: '#e2e8f0', height: '8px', borderRadius: '4px' }}>
              <div style={{ background: '#8b5cf6', width: `${Math.min(assigned * 15, 100)}%`, height: '100%', borderRadius: '4px' }}></div>
            </div>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '5px' }}>
              <span>Creadas / Pendientes</span>
              <b>{created}</b>
            </div>
            <div style={{ background: '#e2e8f0', height: '8px', borderRadius: '4px' }}>
              <div style={{ background: '#f59e0b', width: `${Math.min(created * 15, 100)}%`, height: '100%', borderRadius: '4px' }}></div>
            </div>
          </div>

          <div style={{ background: '#fef3c7', padding: '12px', borderRadius: '8px', borderLeft: '4px solid #f59e0b', fontSize: '12px', color: '#92400e' }}>
            <b>Sistema Operativo</b>.
          </div>
        </article>
      </div>
    </>
  );
}