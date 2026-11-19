package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.exception.AuthException;
import br.gov.sp.cps.fatec.exception.AuthException.Reason;
import br.gov.sp.cps.fatec.entity.User;
import br.gov.sp.cps.fatec.repository.PasswordHasher;
import br.gov.sp.cps.fatec.repository.TokenIssuer;
import br.gov.sp.cps.fatec.repository.UserRepository;

public class LoginUseCase {
    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenIssuer tokens;

    public LoginUseCase(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public AuthResult execute(String username, String password) {
        String name = username == null ? "" : username.trim().toLowerCase();
        User user = users.findByUsername(name)
                .filter(u -> password != null && hasher.matches(password, u.passwordHash()))
                .orElseThrow(() -> new AuthException(Reason.BAD_CREDENTIALS, "Usuário ou senha inválidos."));
        return new AuthResult(tokens.issue(user), user.id(), user.username(), user.role());
    }
}
