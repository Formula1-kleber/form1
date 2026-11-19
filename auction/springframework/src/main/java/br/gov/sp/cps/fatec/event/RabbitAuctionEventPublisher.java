package br.gov.sp.cps.fatec.event;

import br.gov.sp.cps.fatec.event.AuctionClosedEvent;
import br.gov.sp.cps.fatec.event.BidPlacedEvent;
import br.gov.sp.cps.fatec.event.LotCreatedEvent;
import br.gov.sp.cps.fatec.repository.AuctionEventPublisher;
import br.gov.sp.cps.fatec.configuration.RabbitConfig;
import br.gov.sp.cps.fatec.event.dto.AuctionClosedMessage;
import br.gov.sp.cps.fatec.event.dto.AuctionMessage;
import br.gov.sp.cps.fatec.event.dto.BidPlacedMessage;
import br.gov.sp.cps.fatec.event.dto.LotCreatedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Adaptador: converte os eventos do domínio em DTOs e os publica na exchange "auction.events" do RabbitMQ. */
@Component
class RabbitAuctionEventPublisher implements AuctionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitAuctionEventPublisher.class);

    private final RabbitTemplate rabbit;

    RabbitAuctionEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Override
    public void bidPlaced(BidPlacedEvent event) {
        publish(RabbitConfig.KEY_BID_PLACED, BidPlacedMessage.from(event));
    }

    @Override
    public boolean auctionClosed(AuctionClosedEvent event) {
        return publish(RabbitConfig.KEY_AUCTION_CLOSED, AuctionClosedMessage.from(event));
    }

    @Override
    public void lotCreated(LotCreatedEvent event) {
        publish(RabbitConfig.KEY_LOT_CREATED, LotCreatedMessage.from(event));
    }

    /** O lance/lote já foi gravado no banco. Se o RabbitMQ falhar, registra no log em vez de devolver erro ao usuário. */
    private boolean publish(String routingKey, AuctionMessage message) {
        try {
            rabbit.convertAndSend(RabbitConfig.EXCHANGE, routingKey, message);
            log.info("[RabbitMQ] PUBLICADO exchange={} routingKey={} mensagem={}", RabbitConfig.EXCHANGE, routingKey, message);
            return true;
        } catch (Exception e) {
            log.error("Falha ao publicar '{}' no RabbitMQ: {}", routingKey, e.getMessage());
            return false;
        }
    }
}
