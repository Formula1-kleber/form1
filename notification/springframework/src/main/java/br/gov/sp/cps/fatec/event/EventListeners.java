package br.gov.sp.cps.fatec.event;

import br.gov.sp.cps.fatec.service.NotifyAuctionClosedUseCase;
import br.gov.sp.cps.fatec.service.NotifyBidPlacedUseCase;
import br.gov.sp.cps.fatec.service.NotifyLotCreatedUseCase;
import br.gov.sp.cps.fatec.configuration.RabbitConfig;
import br.gov.sp.cps.fatec.event.dto.AuctionClosedMessage;
import br.gov.sp.cps.fatec.event.dto.BidPlacedMessage;
import br.gov.sp.cps.fatec.event.dto.LotCreatedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada: recebe os DTOs do RabbitMQ, converte para o evento do domínio e aciona o caso de uso.
 * A configuração do RabbitMQ (filas, bindings, conversor) fica em {@link RabbitConfig}.
 */
@Component
class EventListeners {

    private static final Logger log = LoggerFactory.getLogger(EventListeners.class);

    private final NotifyBidPlacedUseCase bidPlaced;
    private final NotifyAuctionClosedUseCase auctionClosed;
    private final NotifyLotCreatedUseCase lotCreated;

    EventListeners(NotifyBidPlacedUseCase bidPlaced, NotifyAuctionClosedUseCase auctionClosed, NotifyLotCreatedUseCase lotCreated) {
        this.bidPlaced = bidPlaced;
        this.auctionClosed = auctionClosed;
        this.lotCreated = lotCreated;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_BID)
    void onBidPlaced(BidPlacedMessage message) {
        log.info("[RabbitMQ] RECEBIDO fila={} mensagem={}", RabbitConfig.QUEUE_BID, message);
        bidPlaced.execute(message.toEvent());
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_LOT)
    void onLotCreated(LotCreatedMessage message) {
        log.info("[RabbitMQ] RECEBIDO fila={} mensagem={}", RabbitConfig.QUEUE_LOT, message);
        lotCreated.execute(message.toEvent());
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_CLOSED)
    void onClosed(AuctionClosedMessage message) {
        log.info("[RabbitMQ] RECEBIDO fila={} mensagem={}", RabbitConfig.QUEUE_CLOSED, message);
        auctionClosed.execute(message.toEvent());
    }
}
