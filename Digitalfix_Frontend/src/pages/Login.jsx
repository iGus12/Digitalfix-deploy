import { useState } from 'react'
import { useMsal } from '@azure/msal-react'
import { ShieldCheck, Wrench, Activity } from 'lucide-react'

import { loginRequest } from '../auth/msalConfig'

export default function Login() {
  const { instance } = useMsal()

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loginMicrosoft = async () => {
    try {
      setLoading(true)
      setError('')

      await instance.loginRedirect(loginRequest)

    } catch (err) {
      console.error('Error al iniciar sesión con Microsoft:', err)

      setError('No se pudo iniciar sesión con Microsoft.')

      setLoading(false)
    }
  }

  return (
    <div className="login-page">

      <section className="login-visual">

        <div className="login-brand">
          <Wrench size={28}/>
          <strong>DigitalFix</strong>
        </div>

        <div className="login-copy">

          <span className="eyebrow">
            FIELD SERVICE · CLOUD NATIVE
          </span>

          <h1>
            Mantención eléctrica con control,
            trazabilidad y respuesta rápida.
          </h1>

          <p>
            Administra órdenes, repuestos,
            cuadrillas y auditoría desde una sola plataforma.
          </p>

          <div className="feature-row">

            <div>
              <Activity/>

              <span>
                <b>Operación</b> en tiempo real
              </span>
            </div>

            <div>
              <ShieldCheck/>

              <span>
                <b>Acceso</b> corporativo seguro
              </span>
            </div>

          </div>

        </div>

      </section>

      <section className="login-panel">

        <div className="login-card">

          <span className="eyebrow">
            ACCESO CORPORATIVO
          </span>

          <h2>
            Iniciar sesión
          </h2>

          <p>
            Ingresa con tu cuenta corporativa de Microsoft.
          </p>

          <button
            className="btn microsoft"
            onClick={loginMicrosoft}
            disabled={loading}
          >

            <span className="ms-logo">
              <i></i>
              <i></i>
              <i></i>
              <i></i>
            </span>

            {loading
              ? 'Conectando con Microsoft...'
              : 'Iniciar sesión con Microsoft'
            }

          </button>

          {error && (
            <p
              style={{
                color: '#c62828',
                marginTop: '12px'
              }}
            >
              {error}
            </p>
          )}

          <small className="hint">
            Acceso protegido mediante Microsoft Entra ID.
          </small>

        </div>

      </section>

    </div>
  )
}