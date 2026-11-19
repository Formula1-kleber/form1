package br.gov.sp.cps.fatec.controller.dto;

import br.gov.sp.cps.fatec.service.AuthResult;

/** Dados da sessão devolvidos ao front. O token NÃO vai no corpo: ele só trafega no cookie HttpOnly. */
public record SessionResponse(String userId, String username, String role) {

    public static SessionResponse from(AuthResult r) {
        return new SessionResponse(r.userId(), r.username(), r.role());
    }
}
