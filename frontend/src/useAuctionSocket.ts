import { useEffect, useRef } from "react";
import { WS_URL } from "./api";
import type { ServerEvent } from "./types";

/**
 * Conecta no WebSocket (via gateway) e reconecta sozinho se a conexão cair.
 * A autenticação vai no cookie HttpOnly enviado pelo navegador no handshake: nada de token na URL.
 */
export function useAuctionSocket(onEvent: (e: ServerEvent) => void, onOpen: () => void) {
  const eventRef = useRef(onEvent);
  const openRef = useRef(onOpen);
  eventRef.current = onEvent;
  openRef.current = onOpen;

  useEffect(() => {
    let ws: WebSocket | undefined;
    let timer: number | undefined;
    let closed = false;

    const connect = () => {
      ws = new WebSocket(`${WS_URL}/ws`);
      ws.onopen = () => openRef.current();
      ws.onmessage = (m) => eventRef.current(JSON.parse(m.data) as ServerEvent);
      ws.onclose = () => {
        if (!closed) timer = window.setTimeout(connect, 2000);
      };
    };
    connect();

    return () => {
      closed = true;
      window.clearTimeout(timer);
      ws?.close();
    };
  }, []);
}
