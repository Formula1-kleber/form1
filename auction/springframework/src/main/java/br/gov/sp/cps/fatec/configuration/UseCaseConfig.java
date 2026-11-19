package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.repository.AuctionEventPublisher;
import br.gov.sp.cps.fatec.repository.LotRepository;
import br.gov.sp.cps.fatec.service.CloseExpiredLotsUseCase;
import br.gov.sp.cps.fatec.service.CreateLotUseCase;
import br.gov.sp.cps.fatec.service.ListClosedLotsUseCase;
import br.gov.sp.cps.fatec.service.ListMyResultsUseCase;
import br.gov.sp.cps.fatec.service.ListOpenLotsUseCase;
import br.gov.sp.cps.fatec.service.PlaceBidUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Liga os casos de uso às implementações concretas do Spring. */
@Configuration
public class UseCaseConfig {

    @Bean
    ListOpenLotsUseCase listOpenLotsUseCase(LotRepository lots) {
        return new ListOpenLotsUseCase(lots);
    }

    @Bean
    ListClosedLotsUseCase listClosedLotsUseCase(LotRepository lots) {
        return new ListClosedLotsUseCase(lots);
    }

    @Bean
    ListMyResultsUseCase listMyResultsUseCase(LotRepository lots) {
        return new ListMyResultsUseCase(lots);
    }

    @Bean
    CreateLotUseCase createLotUseCase(LotRepository lots, AuctionEventPublisher events) {
        return new CreateLotUseCase(lots, events);
    }

    @Bean
    PlaceBidUseCase placeBidUseCase(LotRepository lots, AuctionEventPublisher events) {
        return new PlaceBidUseCase(lots, events);
    }

    @Bean
    CloseExpiredLotsUseCase closeExpiredLotsUseCase(LotRepository lots, AuctionEventPublisher events,
                                                    @Value("${app.auction.reserve-ratio}") double reserveRatio) {
        return new CloseExpiredLotsUseCase(lots, events, reserveRatio);
    }

    /** Lotes de exemplo na primeira execução (duração em segundos). */
    @Bean
    CommandLineRunner seedLots(LotRepository lots, CreateLotUseCase create) {
        return args -> {
            if (lots.count() > 0) return;
            create.execute(new CreateLotUseCase.Command("Capacete de piloto autografado", "Réplica oficial assinada pelo piloto", 12000, 3000, 180));
            create.execute(new CreateLotUseCase.Command("Asa dianteira de carbono", "Peça usada em corrida", 30000, 8000, 300));
            create.execute(new CreateLotUseCase.Command("Volante de corrida", "Volante de fibra de carbono", 18000, 5000, 420));
            create.execute(new CreateLotUseCase.Command("Pneus usados do GP de Interlagos", "Jogo de 4 pneus", 6000, 1500, 600));
        };
    }
}
