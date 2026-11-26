import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import "./styles.css";

// Plano grátis do Render: os serviços dormem sem uso. Ao abrir o site, "cutuca" cada um (VITE_WAKE_URLS, separadas
// por vírgula) para acordarem em paralelo. no-cors: não lemos a resposta, só precisamos que o pedido chegue.
(import.meta.env.VITE_WAKE_URLS ?? "")
  .split(",")
  .map((u: string) => u.trim())
  .filter(Boolean)
  .forEach((u: string) => { fetch(u, { mode: "no-cors" }).catch(() => undefined); });

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
