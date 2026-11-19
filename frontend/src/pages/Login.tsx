import { useState } from "react";
import { login, register } from "../api";
import type { Session } from "../types";

export default function Login({ onLogin }: { onLogin: (s: Session) => void }) {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setBusy(true);
    setError("");
    try {
      onLogin(await (mode === "login" ? login : register)(username, password));
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <section className="panel login">
      <h2>{mode === "login" ? "Entrar na pista" : "Criar conta"}</h2>
      <label>Usuário
        <input value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
      </label>
      <label>Senha
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
               onKeyDown={(e) => e.key === "Enter" && submit()} />
      </label>
      {error && <div className="toast error">{error}</div>}
      <button className="btn red" onClick={submit} disabled={busy}>{mode === "login" ? "Entrar" : "Cadastrar"}</button>
      <button className="btn ghost" onClick={() => setMode(mode === "login" ? "register" : "login")}>
        {mode === "login" ? "Ainda não tenho conta" : "Já tenho conta"}
      </button>
    </section>
  );
}
