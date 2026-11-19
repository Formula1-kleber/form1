export type Role = "USER" | "ADMIN";

/** Sessão do usuário logado (sem o token). */
export interface Session {
  userId: string;
  username: string;
  role: Role;
}

/** Resposta do login e do cadastro: a sessão mais o token JWT, enviado depois no header Authorization. */
export interface LoginResponse extends Session {
  token: string;
}

export interface Lot {
  id: string;
  name: string;
  description: string;
  referenceValue: number;
  startPrice: number;
  minIncrement: number;
  currentBid: number;
  leaderId: string | null;
  leaderName: string | null;
  endsAt: string;
}

export interface Result {
  lotId: string;
  lotName: string;
  outcome: "WON" | "LOST" | "UNSOLD";
  winnerName: string;
  amount: number;
  closedAt: string;
}

export interface Sale {
  lotId: string;
  lotName: string;
  buyerName: string | null;
  finalPrice: number;
  referenceValue: number;
  sold: boolean;
  closedAt: number;
}

export interface SalesReport {
  totalRevenue: number;
  soldCount: number;
  unsoldCount: number;
  sales: Sale[];
}

export interface CreateLotInput {
  name: string;
  description: string;
  referenceValue: number;
  startPrice: number;
  durationSeconds: number;
}

/** Mensagens enviadas pelo notification-service via WebSocket. */
export type ServerEvent =
  | { type: "LOT_UPDATED"; lotId: string; currentBid: number; leaderId: string; leaderName: string }
  | { type: "LOT_CLOSED"; lotId: string; sold: boolean }
  | { type: "OUTBID"; lotId: string; lotName: string; amount: number; bidderName: string }
  | { type: "LOT_CREATED"; lotId: string }
  | { type: "AUCTION_RESULT"; lotName: string; outcome: "WON" | "LOST" | "UNSOLD"; winnerName: string; amount: number };

export interface Toast {
  id: number;
  kind: "info" | "error" | "outbid" | "won" | "lost";
  sticky?: boolean; // resultado do leilão: fica na tela até o usuário fechar
  text: string;
  lotId?: string;
  amount?: number;
}
