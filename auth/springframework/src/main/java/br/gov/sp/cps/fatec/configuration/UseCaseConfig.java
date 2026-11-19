package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.entity.User;
import br.gov.sp.cps.fatec.repository.PasswordHasher;
import br.gov.sp.cps.fatec.repository.TokenIssuer;
import br.gov.sp.cps.fatec.repository.UserRepository;
import br.gov.sp.cps.fatec.service.LoginUseCase;
import br.gov.sp.cps.fatec.service.RegisterUserUseCase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Liga os casos de uso (Java puro) às implementações concretas do Spring. */
@Configuration
public class UseCaseConfig {

    @Bean
    RegisterUserUseCase registerUserUseCase(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        return new RegisterUserUseCase(users, hasher, tokens);
    }

    @Bean
    LoginUseCase loginUseCase(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        return new LoginUseCase(users, hasher, tokens);
    }

    /** Cria o administrador padrão  */
    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordHasher hasher) {
        return args -> {
            if (users.findByUsername("admin").isEmpty()) {
                users.save(new User(null, "admin", hasher.hash("admin123"), "ADMIN"));
            }
        };
    }
}
