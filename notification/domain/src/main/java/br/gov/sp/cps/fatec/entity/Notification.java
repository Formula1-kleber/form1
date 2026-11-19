package br.gov.sp.cps.fatec.entity;

public sealed interface Notification {
    /** Novo preço de um lote */
    record LotUpdated(String lotId, long currentBid, String leaderId, String leaderName) implements Notification {}

    /** Lote encerrado: some da tela. */
    record LotClosed(String lotId, boolean sold) implements Notification {}

    /** Novo lote aberto*/
    record LotCreated(String lotId) implements Notification {}

    /** Seu lance foi coberto  */
    record Outbid(String lotId, String lotName, long amount, String bidderName) implements Notification {}

    enum Outcome { WON, LOST, UNSOLD }

    /** Resultado final para cada participante */
    record AuctionResult(String lotName, Outcome outcome, String winnerName, long amount) implements Notification {}
}
