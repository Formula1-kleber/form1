import type { CreateLotInput, Lot, Result, Session, SalesReport } from "./types";

export const API_URL: string = import.meta.env.VITE_API_URL ?? "http://localhost:8080";
export const WS_URL: string = API_URL.replace(/^http/, "ws");

export class ApiError extends Error {
  constructor(message: string, public code: string, public status: number) {
    super(message);
  }
}

/** Chamado quando o servidor diz que a sessão acabou (cookie ausente, expirado ou inválido). */
let onUnauthorized: () => void = () => undefined;
export const setUnauthorizedHandler = (fn: () => void) => { onUnauthorized = fn; };

const PUBLIC_PATHS = ["/api/auth/login", "/api/auth/register"];

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let res: Response;
  try {
    res = await fetch(API_URL + path, {
      ...init,
      credentials: "include", // envia o cookie de sessão (HttpOnly) gerado no login
      headers: { "Content-Type": "application/json" },
    });
  } catch {
    throw new ApiError("Não foi possível falar com o servidor. O gateway está rodando?", "REDE", 0);
  }
  if (!res.ok) {
    if (res.status === 401 && !PUBLIC_PATHS.includes(path)) onUnauthorized();
    const body = await res.json().catch(() => ({}));
    const fallback = res.status === 403
      ? "Você não tem permissão para acessar este recurso."
      : `Erro inesperado (HTTP ${res.status}).`;
    throw new ApiError(body.message ?? fallback, body.code ?? "ERRO", res.status);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

// ---- Sessão (cookie) ----
export const login = (username: string, password: string) =>
  request<Session>("/api/auth/login", { method: "POST", body: JSON.stringify({ username, password }) });

export const register = (username: string, password: string) =>
  request<Session>("/api/auth/register", { method: "POST", body: JSON.stringify({ username, password }) });

/** Quem está logado agora, segundo o cookie. */
export const me = () => request<Session>("/api/auth/me");

export const logout = () => request<void>("/api/auth/logout", { method: "POST" });

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
