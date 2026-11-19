package br.gov.sp.cps.fatec.event.dto;

/** Contrato de tudo que o auction publica no RabbitMQ. Só os DTOs abaixo podem ser publicados. */
public sealed interface AuctionMessage permits BidPlacedMessage, AuctionClosedMessage, LotCreatedMessage {}
