import type { CreateLotInput, Lot, LoginResponse, Result, Session, SalesReport } from "./types";

export const API_URL: string = import.meta.env.VITE_API_URL ?? "http://localhost:8080";
export const WS_URL: string = API_URL.replace(/^http/, "ws");

export class ApiError extends Error {
  constructor(message: string, public code: string, public status: number) {
    super(message);
  }
}

// ---- Token (enviado no header Authorization) ----
const TOKEN_KEY = "f1_token";
export const getToken = (): string | null => {
  try { return localStorage.getItem(TOKEN_KEY); } catch { return null; }
};
const setToken = (t: string) => { try { localStorage.setItem(TOKEN_KEY, t); } catch { /* ignora */ } };
const clearToken = () => { try { localStorage.removeItem(TOKEN_KEY); } catch { /* ignora */ } };

/** Chamado quando o servidor diz que a sessão acabou (token ausente, expirado ou inválido). */
let onUnauthorized: () => void = () => undefined;
export const setUnauthorizedHandler = (fn: () => void) => { onUnauthorized = fn; };

const PUBLIC_PATHS = ["/api/auth/login", "/api/auth/register"];

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let res: Response;
  try {
    const token = getToken();
    res = await fetch(API_URL + path, {
      ...init,
      headers: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    });
  } catch {
    throw new ApiError("Não foi possível falar com o servidor. O gateway está rodando?", "REDE", 0);
  }
  if (!res.ok) {
    if (res.status === 401 && !PUBLIC_PATHS.includes(path)) { clearToken(); onUnauthorized(); }
    const body = await res.json().catch(() => ({}));
    const fallback = res.status === 403
      ? "Você não tem permissão para acessar este recurso."
      : `Erro inesperado (HTTP ${res.status}).`;
    throw new ApiError(body.message ?? fallback, body.code ?? "ERRO", res.status);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

// ---- Sessão (token no header Authorization) ----
/** Guarda o token recebido e devolve só os dados da sessão. */
const startSession = ({ token, ...session }: LoginResponse): Session => {
  setToken(token);
  return session;
};

export const login = (username: string, password: string) =>
  request<LoginResponse>("/api/auth/login", { method: "POST", body: JSON.stringify({ username, password }) })
    .then(startSession);

export const register = (username: string, password: string) =>
  request<LoginResponse>("/api/auth/register", { method: "POST", body: JSON.stringify({ username, password }) })
    .then(startSession);

/** Quem está logado agora, segundo o token guardado. Sem token, nem chama o servidor. */
export const me = (): Promise<Session> =>
  getToken() ? request<Session>("/api/auth/me") : Promise.reject(new ApiError("Sem sessão.", "SEM_TOKEN", 401));

export const logout = async (): Promise<void> => {
  try { await request<void>("/api/auth/logout", { method: "POST" }); } finally { clearToken(); }
};

// ---- API de leilão: participante (somente USER) ----
export const listLots = () => request<Lot[]>("/api/auctions");

export const listMyResults = () => request<Result[]>("/api/auctions/results/mine");

export const placeBid = (lotId: string, amount: number) =>
  request<Lot>(`/api/auctions/${lotId}/bids`, { method: "POST", body: JSON.stringify({ amount }) });

// ---- API de leilão: administrador (somente ADMIN) ----
export const listOpenLotsAdmin = () => request<Lot[]>("/api/admin/auctions");

export const createLot = (input: CreateLotInput) =>
  request<Lot>("/api/admin/auctions", { method: "POST", body: JSON.stringify(input) });

// ---- API de relatório: somente leitura (ADMIN) ----
export const getReport = () => request<SalesReport>("/api/reports/sales");
