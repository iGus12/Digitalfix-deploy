import { useState, useEffect } from 'react';
import { Search, Package, Wrench } from 'lucide-react';
import { useMsal } from '@azure/msal-react';
import { api } from '../services/api';

export default function Catalog() {
  const { instance, accounts } = useMsal();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Estados para controlar el formulario y mostrarlo u ocultarlo
  const [showForm, setShowForm] = useState(false);
  const [newItem, setNewItem] = useState({ name: '', type: 'REPUESTO', stock: 0, price: 0, status: 'Disponible' });

  // Función para traer los datos del catálogo
  const fetchCatalog = async () => {
    try {
      const response = await instance.acquireTokenSilent({
        scopes: [`${import.meta.env.VITE_AZURE_CLIENT_ID}/.default`], 
        account: accounts[0]
      });
      const data = await api.getCatalog(response.accessToken);
      setItems(data);
    } catch (error) {
      console.error("Error al cargar el catálogo:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (accounts.length > 0) fetchCatalog();
  }, [instance, accounts]);

  // Función que se ejecuta al enviar el formulario del nuevo ítem
  const handleCreateItem = async (e) => {
    e.preventDefault();
    try {
      const response = await instance.acquireTokenSilent({
        scopes: [`${import.meta.env.VITE_AZURE_CLIENT_ID}/.default`], 
        account: accounts[0]
      });
      
      await api.createCatalogItem(newItem, response.accessToken);
      
      await fetchCatalog();
      setShowForm(false);
      setNewItem({ name: '', type: 'REPUESTO', stock: 0, price: 0, status: 'Disponible' });
    } catch (error) {
      console.error("Error al crear el ítem:", error);
      alert("Hubo un error al guardar. Revisa la consola.");
    }
  };

  const totalServicios = items.filter(i => i.type && i.type.toUpperCase().includes('SERVICIO')).length;
  const totalStock = items.reduce((acc, item) => acc + (Number(item.stock) || 0), 0);

  return (
    <>
      <div className="page-heading">
        <div>
          <span className="eyebrow">CATÁLOGO TÉCNICO</span>
          <h1>Servicios y repuestos</h1>
          <p>Consulta disponibilidad, tarifas y stock técnico.</p>
        </div>
        <button className="btn primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancelar' : 'Agregar ítem'}
        </button>
      </div>

      <section className="summary-strip">
        <div><Wrench /><span><b>{totalServicios}</b> servicios activos</span></div>
        <div><Package /><span><b>{totalStock}</b> unidades en stock</span></div>
      </section>

      {showForm && (
        <article className="card" style={{ padding: '20px', marginBottom: '20px' }}>
          <h3 style={{ marginBottom: '15px' }}>Crear nuevo registro</h3>
          <form onSubmit={handleCreateItem} style={{ display: 'flex', gap: '15px', flexWrap: 'wrap', alignItems: 'center' }}>
            <input 
              placeholder="Nombre (ej. Multímetro)" required style={{ padding: '8px', flex: '1' }}
              value={newItem.name} onChange={e => setNewItem({...newItem, name: e.target.value})} 
            />
            <select style={{ padding: '8px' }} value={newItem.type} onChange={e => setNewItem({...newItem, type: e.target.value})}>
              <option value="REPUESTO">Repuesto</option>
              <option value="SERVICIO">Servicio</option>
              <option value="HERRAMIENTA">Herramienta</option>
            </select>
            <input 
              type="number" placeholder="Stock" required style={{ padding: '8px', width: '80px' }}
              value={newItem.stock} onChange={e => setNewItem({...newItem, stock: parseInt(e.target.value)})} 
            />
            <input 
              type="number" placeholder="Precio ($)" required style={{ padding: '8px', width: '100px' }}
              value={newItem.price} onChange={e => setNewItem({...newItem, price: parseFloat(e.target.value)})} 
            />
            <button type="submit" className="btn primary">Agregar item</button>
          </form>
        </article>
      )}

      <article className="card table-card">
        <div className="toolbar">
          <div className="search">
            <Search size={18} />
            <input placeholder="Buscar en catálogo..." />
          </div>
        </div>
        <div className="table-wrap">
          {loading ? (
            <p style={{ padding: '20px', textAlign: 'center' }}>Cargando datos desde...</p>
          ) : items.length === 0 ? (
            <p style={{ padding: '20px', textAlign: 'center' }}>No hay ítems en el catálogo.</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Stock</th>
                  <th>Tarifa</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {items.map((r, index) => (
                  <tr key={r.id || index}>
                    <td><strong>{r.name}</strong></td>
                    <td>{r.type}</td>
                    <td>{r.stock !== null && r.stock !== undefined ? r.stock : '—'}</td>
                    <td>${r.price}</td>
                    <td>
                      <span className={`badge ${r.status === 'Stock bajo' ? 'warning' : 'closed'}`}>
                        {r.status || 'Disponible'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </article>
    </>
  );
}