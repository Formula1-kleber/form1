package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.repository.NotificationGateway;
import br.gov.sp.cps.fatec.service.NotifyAuctionClosedUseCase;
import br.gov.sp.cps.fatec.service.NotifyBidPlacedUseCase;
import br.gov.sp.cps.fatec.service.NotifyLotCreatedUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    NotifyBidPlacedUseCase notifyBidPlacedUseCase(NotificationGateway gateway) {
        return new NotifyBidPlacedUseCase(gateway);
    }

    @Bean
    NotifyLotCreatedUseCase notifyLotCreatedUseCase(NotificationGateway gateway) {
        return new NotifyLotCreatedUseCase(gateway);
    }

    @Bean
    NotifyAuctionClosedUseCase notifyAuctionClosedUseCase(NotificationGateway gateway) {
        return new NotifyAuctionClosedUseCase(gateway);
    }
}
