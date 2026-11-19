package br.gov.sp.cps.fatec.controller.dto;

import br.gov.sp.cps.fatec.service.AuthResult;

/**
 * Dados da sessão devolvidos ao front. No login e no cadastro o campo {@code token} traz o JWT, que o front
 * guarda e envia depois no header {@code Authorization: Bearer <token>}. Em /me o token vem nulo.
 */
public record SessionResponse(String userId, String username, String role, String token) {

    public static SessionResponse from(AuthResult r) {
        return new SessionResponse(r.userId(), r.username(), r.role(), r.token());
    }

    public static SessionResponse identity(String userId, String username, String role) {
        return new SessionResponse(userId, username, role, null);
    }
}
