import { useCallback, useEffect, useRef, useState } from "react";
import { listLots, listMyResults, placeBid } from "../api";
import { brl, mmss } from "../format";
import type { Lot, Result, ServerEvent, Session, Toast } from "../types";
import { useAuctionSocket } from "../useAuctionSocket";

const POLL_MS = 3000; // plano B: se o WebSocket/RabbitMQ falhar, a tela continua se atualizando sozinha

export default function Auction({ session }: { session: Session }) {
  const dismissedKey = `f1-dismissed-results:${session.userId}`;

  const [lots, setLots] = useState<Lot[]>([]);
  const lotsRef = useRef<Lot[]>([]);
  const [toasts, setToasts] = useState<Toast[]>([]);
  const [now, setNow] = useState(Date.now());
  const [results, setResults] = useState<Result[]>([]);
  const [dismissed, setDismissed] = useState<string[]>(() => {
    try { return JSON.parse(localStorage.getItem(dismissedKey) ?? "[]") as string[]; } catch { return []; }
  });
  const notified = useRef(new Set<string>());
  const seq = useRef(0);

  const dismiss = useCallback((id: number) => setToasts((p) => p.filter((t) => t.id !== id)), []);
  const push = useCallback((t: Omit<Toast, "id">) => {
    const id = ++seq.current;
    setToasts((p) => [...p.slice(-5), { ...t, id }]);
    if (!t.sticky) window.setTimeout(() => dismiss(id), 15000);
  }, [dismiss]);

  /** Avisa que o lance do usuário foi coberto (uma única vez por lote e valor, venha do WebSocket ou da comparação local). */
  const notifyOutbid = useCallback((lotId: string, lotName: string, amount: number, bidderName: string) => {
    const key = `${lotId}:${amount}`;
    if (notified.current.has(key)) return;
    notified.current.add(key);
    push({ kind: "outbid", lotId, amount,
      text: `${bidderName} cobriu seu lance em "${lotName}" com ${brl(amount)}. Quer dar um lance maior?` });
  }, [push]);

  /** Único ponto que altera a lista de lotes: detecta quando quem liderava deixou de liderar. */
  const setLotList = useCallback((fn: (prev: Lot[]) => Lot[]) => {
    const prev = lotsRef.current;
    const next = fn(prev);
    for (const p of prev) {
      if (p.leaderId !== session.userId) continue;
      const n = next.find((l) => l.id === p.id);
      if (n && n.leaderId && n.leaderId !== session.userId) {
        notifyOutbid(n.id, n.name, n.currentBid, n.leaderName ?? "Outro participante");
      }
    }
    lotsRef.current = next;
    setLots(next);
  }, [session.userId, notifyOutbid]);

  const refresh = useCallback((silent: boolean) => {
    listLots().then((l) => setLotList(() => l))
      .catch((e: Error) => { if (!silent) push({ kind: "error", text: e.message }); });
  }, [setLotList, push]);

  const loadResults = useCallback(() => {
    listMyResults().then(setResults).catch(() => undefined);
  }, []);

  useEffect(() => { refresh(false); loadResults(); }, [refresh, loadResults]);
  useEffect(() => {
    const clock = window.setInterval(() => setNow(Date.now()), 500);
    const poll = window.setInterval(() => { refresh(true); loadResults(); }, POLL_MS);
    return () => { window.clearInterval(clock); window.clearInterval(poll); };
  }, [refresh, loadResults]);

  const onEvent = useCallback((e: ServerEvent) => {
    switch (e.type) {
      case "LOT_UPDATED":
        setLotList((p) => p.map((l) => l.id === e.lotId
          ? { ...l, currentBid: e.currentBid, leaderId: e.leaderId, leaderName: e.leaderName } : l));
        break;
      case "LOT_CREATED":
        refresh(true); // novo lote aberto pelo admin: aparece na hora
        break;
      case "LOT_CLOSED":
        setLotList((p) => p.filter((l) => l.id !== e.lotId)); // o produto some do sistema
        loadResults();
        break;
      case "OUTBID":
        notifyOutbid(e.lotId, e.lotName, e.amount, e.bidderName);
        break;
      case "AUCTION_RESULT":
        loadResults();
        break;
    }
  }, [setLotList, refresh, loadResults, notifyOutbid]);

  useAuctionSocket(onEvent, () => { refresh(true); loadResults(); });

  const bid = async (lotId: string, amount: number) => {
    try {
      const updated = await placeBid(lotId, amount);
      setLotList((p) => p.map((l) => (l.id === updated.id ? updated : l)));
      push({ kind: "info", text: `Lance de ${brl(amount)} registrado.` });
    } catch (e) {
      push({ kind: "error", text: (e as Error).message });
      refresh(true); // mostra o estado real do lote (por exemplo, quem passou à frente)
    }
  };

  const hideResults = (ids: string[]) => {
    const next = Array.from(new Set([...dismissed, ...ids]));
    setDismissed(next);
    localStorage.setItem(dismissedKey, JSON.stringify(next));
  };
  const visibleResults = results.filter((r) => !dismissed.includes(r.lotId));

  const resultText = (r: Result) =>
    r.outcome === "WON" ? `Parabéns! Você arrematou "${r.lotName}" por ${brl(r.amount)}.`
    : r.outcome === "LOST" ? `O leilão de "${r.lotName}" terminou. ${r.winnerName} levou por ${brl(r.amount)}. Você não levou desta vez.`
    : `O leilão de "${r.lotName}" terminou sem venda: o maior lance (${brl(r.amount)}) não chegou ao valor mínimo.`;

  return (
    <>
      {visibleResults.length > 0 && (
        <section className="results" aria-live="polite">
          <div className="results-head">
            <h2>Resultados dos seus leilões</h2>
            <button className="btn ghost" onClick={() => hideResults(visibleResults.map((r) => r.lotId))}>Limpar tudo</button>
          </div>
          {visibleResults.map((r) => (
            <div key={r.lotId} className={`toast result ${r.outcome === "WON" ? "won" : "lost"}`}>
              <span>{resultText(r)}</span>
              <button className="btn ghost close" onClick={() => hideResults([r.lotId])}>Fechar</button>
            </div>
          ))}
        </section>
      )}

      <div className="toasts" aria-live="polite">
        {toasts.map((t) => {
          const inc = lots.find((l) => l.id === t.lotId)?.minIncrement ?? 500;
          return (
            <div key={t.id} className={`toast ${t.kind}`}>
              {t.text}
              {t.kind === "outbid" && t.lotId && t.amount !== undefined && (
                <div className="row">
                  <button className="btn red" onClick={() => { bid(t.lotId!, t.amount! + inc); dismiss(t.id); }}>
                    Cobrir com {brl(t.amount + inc)}
                  </button>
                  <button className="btn ghost" onClick={() => dismiss(t.id)}>Agora não</button>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {lots.length === 0 ? (
        <div className="panel empty">Nenhum leilão aberto agora. Peça ao administrador para criar novos lotes.</div>
      ) : (
        <div className="lots">
          {lots.map((lot) => <LotCard key={lot.id} lot={lot} me={session.userId} now={now} onBid={bid} />)}
        </div>
      )}
    </>
  );
}

function LotCard({ lot, me, now, onBid }: { lot: Lot; me: string; now: number; onBid: (id: string, amount: number) => void }) {
  const [value, setValue] = useState("");
  const min = lot.leaderId ? lot.currentBid + lot.minIncrement : lot.startPrice;
  const left = Math.max(0, new Date(lot.endsAt).getTime() - now);
  const price = lot.leaderId ? lot.currentBid : lot.startPrice;
  const pct = Math.min(100, (price / lot.referenceValue) * 100);
  const send = () => { onBid(lot.id, Number(value) || min); setValue(""); };

  return (
    <article className="lot panel">
      <div className="meta">
        <span>Valor de referência {brl(lot.referenceValue)}</span>
        <span className={"time" + (left < 10000 ? " hot" : "")}>{left > 0 ? mmss(left) : "Encerrando"}</span>
      </div>
      <h3>{lot.name}</h3>
      <p className="desc">{lot.description}</p>
      <div className="price">
        <span key={lot.currentBid} className="flash">{brl(price)}</span>{" "}
        {lot.leaderId
          ? <span className={"chip " + (lot.leaderId === me ? "me" : "lead")}>{lot.leaderId === me ? "Você lidera" : `${lot.leaderName} lidera`}</span>
          : <span className="chip">Sem lances</span>}
      </div>
      <div className="track"><b style={{ width: `${pct}%` }} /></div>
      <div className="bidrow">
        <input type="number" inputMode="numeric" placeholder={`Mínimo ${brl(min)}`} value={value}
               aria-label={`Lance para ${lot.name}`}
               onChange={(e) => setValue(e.target.value)} onKeyDown={(e) => e.key === "Enter" && send()} />
        <button className="btn red" onClick={send} disabled={left === 0}>Dar lance</button>
      </div>
    </article>
  );
}
