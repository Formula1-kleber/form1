package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.exception.AuthException;
import br.gov.sp.cps.fatec.exception.AuthException.Reason;
import br.gov.sp.cps.fatec.entity.User;
import br.gov.sp.cps.fatec.repository.PasswordHasher;
import br.gov.sp.cps.fatec.repository.TokenIssuer;
import br.gov.sp.cps.fatec.repository.UserRepository;

public class RegisterUserUseCase {
    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenIssuer tokens;

    public RegisterUserUseCase(UserRepository users, PasswordHasher hasher, TokenIssuer tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public AuthResult execute(String username, String password) {
        String name = username == null ? "" : username.trim().toLowerCase();
        if (name.length() < 3 || password == null || password.length() < 4)
            throw new AuthException(Reason.INVALID_DATA, "Usuário (mín. 3) e senha (mín. 4) são obrigatórios.");
        if (users.findByUsername(name).isPresent())
            throw new AuthException(Reason.USERNAME_TAKEN, "Este usuário já existe.");
        User saved = users.save(new User(null, name, hasher.hash(password), "USER"));
        return new AuthResult(tokens.issue(saved), saved.id(), saved.username(), saved.role());
    }
}
