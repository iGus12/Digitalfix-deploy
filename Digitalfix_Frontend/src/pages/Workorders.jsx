import { useMemo, useState, useEffect } from 'react'
import { Plus, Search, SlidersHorizontal, X } from 'lucide-react'
import { useMsal } from '@azure/msal-react'
import { api } from '../services/api'

export default function Workorders() {
  const { instance, accounts } = useMsal()

  const account =
    instance.getActiveAccount() ||
    accounts[0]

  const roles =
    (account?.idTokenClaims?.roles || [])
      .map(role => role.toUpperCase())

  const isAuditor =
    roles.includes('AUDITOR')

  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [q, setQ] = useState('')
  const [show, setShow] = useState(false)

  // Agregamos technician al estado inicial del formulario
  const [form, setForm] = useState({
    clientName: '',
    description: '',
    address: '',
    technician: ''
  })

  const fetchWorkOrders = async () => {
    try {
      const response = await instance.acquireTokenSilent({
        scopes: [`${import.meta.env.VITE_AZURE_CLIENT_ID}/.default`], 
        account: accounts[0]
      });
      const data = await api.getWorkorders(response.accessToken);
      setOrders(data || []);
    } catch (error) {
      console.error("Error al cargar las órdenes de trabajo:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (accounts.length > 0) {
      fetchWorkOrders();
    }
  }, [instance, accounts]);

  const filtered = useMemo(() => {
    return orders.filter((o) =>
      Object.values(o)
        .join(' ')
        .toLowerCase()
        .includes(q.toLowerCase())
    )
  }, [orders, q])

  const create = async (e) => {
    e.preventDefault()

    try {
      const response = await instance.acquireTokenSilent({
        scopes: [`${import.meta.env.VITE_AZURE_CLIENT_ID}/.default`], 
        account: accounts[0]
      });

      const newOrderPayload = {
        clientName: form.clientName,
        description: form.description,
        address: form.address || 'Santiago',
        technician: form.technician || 'Sin asignar',
        status: 'CREADA'
      };

      await api.createWorkorder(newOrderPayload, response.accessToken);
      
      await fetchWorkOrders();

      setShow(false)
      setForm({
        clientName: '',
        description: '',
        address: '',
        technician: ''
      })
    } catch (error) {
      console.error("Error al crear la orden:", error);
      alert("Hubo un error al registrar la orden en Oracle.");
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">
            GESTIÓN DE ÓRDENES
          </span>
          <h1>
            Órdenes de trabajo
          </h1>
          <p>
            Crea, asigna y consulta trabajos de mantención.
          </p>
        </div>

        {!isAuditor && (
          <button
            className="btn primary"
            onClick={() => setShow(true)}
          >
            <Plus size={17}/>
            Nueva orden
          </button>
        )}
      </div>

      <article className="card table-card">
        <div className="toolbar">
          <div className="search">
            <Search size={18}/>
            <input
              placeholder="Buscar por orden, cliente o servicio..."
              value={q}
              onChange={(e) => setQ(e.target.value)}
            />
          </div>

          <button className="btn secondary">
            <SlidersHorizontal size={17}/>
            Filtros
          </button>
        </div>

        <div className="table-wrap">
          {loading ? (
            <p style={{ padding: '20px', textAlign: 'center' }}>Cargando órdenes desde Oracle XE...</p>
          ) : filtered.length === 0 ? (
            <p style={{ padding: '20px', textAlign: 'center' }}>No hay órdenes registradas.</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Orden</th>
                  <th>Cliente</th>
                  <th>Servicio / Descripción</th>
                  <th>Dirección</th>
                  <th>Estado</th>
                  <th>Técnico</th>
                  <th>Creación</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((o, index) => (
                  <tr key={o.id || index}>
                    <td>
                      <strong>
                        OT-{o.id || 1000 + index}
                      </strong>
                    </td>
                    <td>
                      {o.clientName || o.client || 'Juan Pérez'}
                    </td>
                    <td>
                      {o.description || o.service || 'Sin descripción'}
                    </td>
                    <td>
                      {o.address || 'Santiago'}
                    </td>
                    <td>
                      {(() => {
    const status = o.status ? o.status.toUpperCase() : 'CREADA';
    
    // Definimos los estilos según el estado
    let bg = '#e2e8f0';
    let color = '#475569';
    
    if (status === 'CREADA') {
      bg = '#e0f2fe'; color = '#0369a1'; // Azul claro
    } else if (status === 'ASIGNADA') {
      bg = '#ede9fe'; color = '#6d28d9'; // Morado
    } else if (status === 'EN_EJECUCION') {
      bg = '#fef3c7'; color = '#b45309'; // Amarillo / Naranja (Alerta/Proceso)
    } else if (status === 'CERRADA') {
      bg = '#d1fae5'; color = '#047857'; // Verde (Éxito)
    } else if (status === 'CANCELADA') {
      bg = '#fee2e2'; color = '#b91c1c'; // Rojo
    }

    return (
      <span style={{
        backgroundColor: bg,
        color: color,
        padding: '4px 10px',
        borderRadius: '6px',
        fontSize: '12px',
        fontWeight: '600',
        display: 'inline-block'
      }}>
        {status.replaceAll('_', ' ')}
      </span>
    );
  })()}
                    </td>
                    <td>
                      {o.technician || o.tech || 'Sin asignar'}
                    </td>
                    <td>
                      {o.createdAt ? new Date(o.createdAt).toLocaleString() : (o.created || 'Reciente')}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </article>

      {show && (
        <div className="modal-backdrop">
          <form
            className="modal card"
            onSubmit={create}
          >
            <div className="section-title">
              <div>
                <h2>
                  Nueva orden
                </h2>
                <p>
                  Registra un trabajo de mantención.
                </p>
              </div>

              <button
                type="button"
                className="icon-button"
                onClick={() => setShow(false)}
              >
                <X/>
              </button>
            </div>

            <label>
              Cliente
              <input
                required
                placeholder="Ej. Comercial Vega"
                value={form.clientName}
                onChange={(e) =>
                  setForm({
                    ...form,
                    clientName: e.target.value
                  })
                }
              />
            </label>

            <label>
              Servicio / Descripción
              <input
                required
                placeholder="Ej. Falla en el tablero"
                value={form.description}
                onChange={(e) =>
                  setForm({
                    ...form,
                    description: e.target.value
                  })
                }
              />
            </label>

            <label>
              Dirección
              <input
                required
                placeholder="Ej. Ñuñoa, Santiago"
                value={form.address}
                onChange={(e) =>
                  setForm({
                    ...form,
                    address: e.target.value
                  })
                }
              />
            </label>

            <label>
              Técnico asignado
              <input
                placeholder="Ej. J. Morales"
                value={form.technician}
                onChange={(e) =>
                  setForm({
                    ...form,
                    technician: e.target.value
                  })
                }
              />
            </label>

            <div className="modal-actions">
              <button
                type="button"
                className="btn secondary"
                onClick={() => setShow(false)}
              >
                Cancelar
              </button>

              <button
                className="btn primary"
              >
                Crear orden
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  )
}