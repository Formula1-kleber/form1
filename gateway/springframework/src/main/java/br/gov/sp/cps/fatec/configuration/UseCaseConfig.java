package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.service.AuthorizeRequestUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Liga o caso de uso (Java puro) ao Spring. */
@Configuration
public class UseCaseConfig {

    @Bean
    AuthorizeRequestUseCase authorizeRequestUseCase() {
        return new AuthorizeRequestUseCase();
    }
}
