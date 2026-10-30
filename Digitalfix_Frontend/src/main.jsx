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
  // 1. Inicializar MSAL
  await msalInstance.initialize()

  // 2. Procesar la respuesta cuando Microsoft redirige
  // nuevamente a localhost
  const response = await msalInstance.handleRedirectPromise()

  // 3. Si acabamos de iniciar sesión, guardar esa cuenta
  // como la cuenta activa
  if (response?.account) {
    msalInstance.setActiveAccount(response.account)

    console.log(
      'Cuenta autenticada con Microsoft:',
      response.account
    )
  }

  // 4. Si ya existía una sesión anterior,
  // recuperar la cuenta guardada
  if (!msalInstance.getActiveAccount()) {
    const accounts = msalInstance.getAllAccounts()

    if (accounts.length > 0) {
      msalInstance.setActiveAccount(accounts[0])
    }
  }

  // 5. Levantar React
  ReactDOM.createRoot(
    document.getElementById('root')
  ).render(
    <React.StrictMode>
      <MsalProvider instance={msalInstance}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </MsalProvider>
    </React.StrictMode>
  )
}

iniciarApp()