import { useEffect, useState } from "react";
import type { ReactNode } from "react";
import { Link, Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { logout as apiLogout, me, setUnauthorizedHandler } from "./api";
import type { Role, Session } from "./types";
import Login from "./pages/Login";
import Auction from "./pages/Auction";
import Admin from "./pages/Admin";
import Home from "./pages/Home";

/** Tela inicial de cada papel: USER só vê /leilao e ADMIN só vê /admin. */
const homeFor = (s: Session | null): string =>
  !s ? "/login" : s.role === "ADMIN" ? "/admin" : "/leilao";

/**
 * Guarda de rota: sem sessão vai para o login; com o papel errado volta para a própria tela.
 * (A barreira de verdade está no gateway, que responde 401/403 mesmo se alguém burlar o front.)
 */
function RequireRole({ session, role, children }: { session: Session | null; role: Role; children: (s: Session) => ReactNode }) {
  if (!session) return <Navigate to="/login" replace />;
  if (session.role !== role) return <Navigate to={homeFor(session)} replace />;
  return <>{children(session)}</>;
}

export default function App() {
  const [session, setSession] = useState<Session | null>(null);
  const [checking, setChecking] = useState(true);
  const navigate = useNavigate();

  // Ao abrir o site (ou dar F5), o cookie diz quem está logado.
  useEffect(() => {
    me().then(setSession).catch(() => setSession(null)).finally(() => setChecking(false));
  }, []);

  // Sessão expirada em qualquer chamada: volta para o login.
  useEffect(() => setUnauthorizedHandler(() => setSession(null)), []);

  const logout = async () => {
    try { await apiLogout(); } catch { /* o cookie expira sozinho de qualquer forma */ }
    setSession(null);
    navigate("/login", { replace: true });
  };

  return (
    <>
      <header className="top">
        <Link to="/" className="brand"><h1>Leilão <span>Pit Lane</span></h1></Link>
        <div className="lights" aria-hidden="true"><i /><i /><i /><i /><i /></div>
        <nav>
          <Link to="/">Início</Link>
          {session ? (
            <>
              <Link to={homeFor(session)}>{session.role === "ADMIN" ? "Painel admin" : "Leilão"}</Link>
              <button onClick={logout}>Sair ({session.username})</button>
            </>
          ) : (
            <Link className="red" to="/login">Entrar</Link>
          )}
        </nav>
      </header>
      <main>
        {checking ? <div className="panel empty">Carregando...</div> : (
          <Routes>
            <Route path="/" element={<Home session={session} />} />
            <Route path="/login" element={session ? <Navigate to={homeFor(session)} replace /> : <Login onLogin={setSession} />} />
            <Route path="/leilao" element={<RequireRole session={session} role="USER">{(s) => <Auction key={s.userId} session={s} />}</RequireRole>} />
            <Route path="/admin" element={<RequireRole session={session} role="ADMIN">{() => <Admin />}</RequireRole>} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        )}
      </main>
    </>
  );
}
