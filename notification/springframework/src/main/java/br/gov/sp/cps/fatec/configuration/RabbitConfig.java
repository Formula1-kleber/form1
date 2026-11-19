package br.gov.sp.cps.fatec.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitConfig.class);

    public static final String EXCHANGE = "auction.events";

    public static final String QUEUE_BID = "notification.bid";
    public static final String QUEUE_CLOSED = "notification.closed";
    public static final String QUEUE_LOT = "notification.lot";

    @Bean TopicExchange auctionExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Queue bidQueue() { return new Queue(QUEUE_BID, true); }
    @Bean Queue closedQueue() { return new Queue(QUEUE_CLOSED, true); }
    @Bean Queue lotQueue() { return new Queue(QUEUE_LOT, true); }

    @Bean Binding bidBinding(Queue bidQueue, TopicExchange auctionExchange) {
        return BindingBuilder.bind(bidQueue).to(auctionExchange).with("bid.placed");
    }

    @Bean Binding closedBinding(Queue closedQueue, TopicExchange auctionExchange) {
        return BindingBuilder.bind(closedQueue).to(auctionExchange).with("auction.closed");
    }

    @Bean Binding lotBinding(Queue lotQueue, TopicExchange auctionExchange) {
        return BindingBuilder.bind(lotQueue).to(auctionExchange).with("lot.created");
    }

    @Bean
    MessageConverter jsonConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultJacksonJavaTypeMapper mapper = new DefaultJacksonJavaTypeMapper();
        mapper.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED); // usa o tipo do método do listener
        converter.setJavaTypeMapper(mapper);
        return converter;
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
