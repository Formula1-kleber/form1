import { useCallback, useEffect, useState } from "react";
import { createLot, getReport, listOpenLotsAdmin } from "../api";
import { brl, mmss } from "../format";
import type { Lot, SalesReport } from "../types";

export default function Admin() {
  const [report, setReport] = useState<SalesReport | null>(null);
  const [lots, setLots] = useState<Lot[]>([]);
  const [now, setNow] = useState(Date.now());
  const [msg, setMsg] = useState("");
  const [form, setForm] = useState({ name: "", description: "", referenceValue: "", startPrice: "", minutes: "5" });

  const load = useCallback(() => {
    getReport().then(setReport).catch((e: Error) => setMsg(e.message));
    listOpenLotsAdmin().then(setLots).catch((e: Error) => setMsg(e.message));
  }, []);

  useEffect(() => {
    load();
    const poll = window.setInterval(load, 3000);
    const clock = window.setInterval(() => setNow(Date.now()), 500);
    return () => { window.clearInterval(poll); window.clearInterval(clock); };
  }, [load]);

  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value });

  const submit = async () => {
    try {
      await createLot({
        name: form.name, description: form.description,
        referenceValue: Number(form.referenceValue), startPrice: Number(form.startPrice),
        durationSeconds: Number(form.minutes) * 60,
      });
      setMsg(`Lote "${form.name}" criado e aberto para lances.`);
      setForm({ name: "", description: "", referenceValue: "", startPrice: "", minutes: "5" });
      load();
    } catch (e) {
      setMsg((e as Error).message);
    }
  };

  const closed = report?.sales ?? [];

  return (
    <>
      <div className="stats">
        <div className="stat"><b>{lots.length}</b>Em andamento</div>
        <div className="stat"><b>{report?.soldCount ?? 0}</b>Vendidos</div>
        <div className="stat"><b>{brl(report?.totalRevenue ?? 0)}</b>Arrecadado</div>
      </div>

      <section className="panel scroll" style={{ marginBottom: 16 }}>
        <h2>Em andamento</h2>
        {lots.length === 0 ? <div className="empty">Nenhum leilão aberto.</div> : (
          <table>
            <thead><tr><th>Produto</th><th>Lance atual</th><th>Líder</th><th>Tempo restante</th></tr></thead>
            <tbody>
              {lots.map((l) => (
                <tr key={l.id}>
                  <td>{l.name}</td>
                  <td>{brl(l.leaderId ? l.currentBid : l.startPrice)}</td>
                  <td>{l.leaderName ?? "Sem lances"}</td>
                  <td>{mmss(Math.max(0, new Date(l.endsAt).getTime() - now))}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section className="panel scroll" style={{ marginBottom: 16 }}>
        <h2>Encerrados</h2>
        {closed.length === 0 ? <div className="empty">Nenhum lote encerrado ainda.</div> : (
          <table>
            <thead><tr><th>Produto</th><th>Resultado</th><th>Comprador</th><th>Valor final</th></tr></thead>
            <tbody>
              {closed.map((s) => (
                <tr key={s.lotId}>
                  <td>{s.lotName}</td>
                  <td><span className={"chip " + (s.sold ? "me" : "")}>{s.sold ? "Vendido" : "Sem venda"}</span></td>
                  <td>{s.sold ? s.buyerName : "-"}</td>
                  <td>{s.sold ? brl(s.finalPrice) : "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section className="panel">
        <h2>Novo lote</h2>
        <div className="form">
          <label>Nome<input value={form.name} onChange={set("name")} /></label>
          <label>Descrição<input value={form.description} onChange={set("description")} /></label>
          <label>Valor de referência (R$)<input type="number" value={form.referenceValue} onChange={set("referenceValue")} /></label>
          <label>Lance inicial (R$)<input type="number" value={form.startPrice} onChange={set("startPrice")} /></label>
          <label>Duração (minutos)<input type="number" value={form.minutes} onChange={set("minutes")} /></label>
        </div>
        <button className="btn red" onClick={submit}>Abrir leilão</button>
        {msg && <div className="toast info" style={{ marginTop: 10 }}>{msg}</div>}
      </section>
    </>
  );
}
