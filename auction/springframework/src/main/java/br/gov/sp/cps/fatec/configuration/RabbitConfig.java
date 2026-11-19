package br.gov.sp.cps.fatec.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitConfig.class);

    public static final String EXCHANGE = "auction.events";
    public static final String KEY_BID_PLACED = "bid.placed";
    public static final String KEY_AUCTION_CLOSED = "auction.closed";
    public static final String KEY_LOT_CREATED = "lot.created";

    @Bean
    TopicExchange auctionExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }

    /** Mostra no console para onde o RabbitMQ está apontando (sem exibir a senha). */
    @Bean
    CommandLineRunner rabbitTarget(@Value("${spring.rabbitmq.addresses}") String addresses) {
        return args -> {
            if (addresses.contains("localhost") || addresses.contains("127.0.0.1")) {
                log.warn("[RabbitMQ] ATENCAO: apontando para o RabbitMQ LOCAL ({}). Se voce usa o CloudAMQP, crie o arquivo "
                        + "{}/f1-auction.properties com a linha spring.rabbitmq.addresses=amqps://... e reinicie o servico.",
                        addresses.substring(addresses.indexOf('@') + 1), System.getProperty("user.home"));
            } else {
                log.info("[RabbitMQ] Broker configurado: {}", addresses.substring(addresses.indexOf('@') + 1));
            }
        };
    }
}
