# DigitalFix - React + Vite

Frontend de demostración para el caso DigitalFix. Incluye rutas y vistas para Login, Dashboard, Órdenes de trabajo, Catálogo, Reportería y Auditoría.

## Ejecutar

```bash
npm install
npm run dev
```

Abre `http://localhost:5173`.

## Build

```bash
npm run build
```

## Backend

Configura `VITE_API_BASE_URL` en un archivo `.env` tomando como base `.env.example`.

## Importante

Este proyecto usa autenticación demo local únicamente para probar la interfaz. La integración real con Microsoft Entra ID y MSAL debe reemplazar `src/auth/demoAuth.js` y agregar el token Bearer en `src/services/api.js`.
