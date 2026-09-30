export default function KpiCard({ title, value, helper, icon: Icon }) {
  return <article className="card kpi-card"><div className="kpi-icon"><Icon size={22}/></div><div><span>{title}</span><strong>{value}</strong><small>{helper}</small></div></article>
}
