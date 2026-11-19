package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.service.CloseExpiredLotsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class ClosingJob {
    private final CloseExpiredLotsUseCase closeExpired;

    ClosingJob(CloseExpiredLotsUseCase closeExpired) {
        this.closeExpired = closeExpired;
    }

    @Scheduled(fixedDelay = 1000)
    void run() {
        closeExpired.execute();
    }
}
