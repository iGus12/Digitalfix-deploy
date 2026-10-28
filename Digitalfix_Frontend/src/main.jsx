
import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

import { PublicClientApplication } from '@azure/msal-browser'
import { MsalProvider } from '@azure/msal-react'

import App from './App.jsx'
import { msalConfig } from './auth/msalConfig.js'

import './styles/index.css'

const msalInstance = new PublicClientApplication(msalConfig)

async function iniciarApp() {
  const root = ReactDOM.createRoot(
    document.getElementById('root')
  )

  try {
    console.log('[DigitalFix] Inicializando Microsoft MSAL')

    await msalInstance.initialize()

    console.log('[DigitalFix] MSAL inicializado')

    const response = await msalInstance.handleRedirectPromise()

    console.log('[DigitalFix] Redirección procesada')

    if (response?.account) {
      msalInstance.setActiveAccount(response.account)
    }

    if (!msalInstance.getActiveAccount()) {
      const accounts = msalInstance.getAllAccounts()

      if (accounts.length > 0) {
        msalInstance.setActiveAccount(accounts[0])
      }
    }

    console.log('[DigitalFix] Iniciando React')

    root.render(
      <React.StrictMode>
        <MsalProvider instance={msalInstance}>
          <BrowserRouter>
            <App />
          </BrowserRouter>
        </MsalProvider>
      </React.StrictMode>
    )

  } catch (error) {
    console.error(
      '[DigitalFix] Error durante la inicialización:',
      error
    )

    root.render(
      <div style={{
        padding: '30px',
        fontFamily: 'Arial, sans-serif'
      }}>
        <h2>No se pudo iniciar DigitalFix</h2>
        <p>
          Ocurrió un error durante la inicialización
          de la aplicación.
        </p>
        <p>
          Revisa la consola del navegador para
          identificar la causa.
        </p>
      </div>
    )
  }
}

iniciarApp()
