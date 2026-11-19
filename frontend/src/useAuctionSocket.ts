import { useEffect, useRef } from "react";
import { WS_URL, getToken } from "./api";
import type { ServerEvent } from "./types";

/**
 * Conecta no WebSocket (via gateway) e reconecta sozinho se a conexão cair.
 * O navegador não permite headers no WebSocket, então o token vai na query string (?token=) só neste canal;
 * o gateway valida o token no handshake, como faz com o header Authorization nas chamadas HTTP.
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
      ws = new WebSocket(`${WS_URL}/ws?token=${encodeURIComponent(getToken() ?? "")}`);
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
