package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.service.LoginUseCase;
import br.gov.sp.cps.fatec.service.RegisterUserUseCase;
import br.gov.sp.cps.fatec.configuration.AuthCookieFactory;
import br.gov.sp.cps.fatec.controller.dto.CredentialsRequest;
import br.gov.sp.cps.fatec.controller.dto.SessionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase register;
    private final LoginUseCase login;
    private final AuthCookieFactory cookies;

    public AuthController(RegisterUserUseCase register, LoginUseCase login, AuthCookieFactory cookies) {
        this.register = register;
        this.login = login;
        this.cookies = cookies;
    }

    @PostMapping("/register")
    public ResponseEntity<SessionResponse> register(@RequestBody CredentialsRequest c) {
        return ResponseEntity.ok(SessionResponse.from(register.execute(c.username(), c.password())));
    }

    @PostMapping("/login")
    public ResponseEntity<SessionResponse> login(@RequestBody CredentialsRequest c) {
        return ResponseEntity.ok(SessionResponse.from(login.execute(c.username(), c.password())));
    }

    /** Quem é o usuário da sessão atual (o gateway já validou o token do header Authorization e preencheu os headers X-User-*). */
    @GetMapping("/me")
    public SessionResponse me(@RequestHeader("X-User-Id") String userId,
                              @RequestHeader("X-User-Name") String username,
                              @RequestHeader("X-User-Role") String role) {
        return SessionResponse.identity(userId, username, role);
    }

    /** Logout: o front descarta o token; este endpoint também apaga um cookie antigo, se existir. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookies.clear().toString())
                .build();
    }
}
