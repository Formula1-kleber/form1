package br.gov.sp.cps.fatec.entity;

/** Resultado da checagem de acesso a uma rota. */
public enum Decision {
    ALLOW,
    UNAUTHENTICATED,   // sem sessão válida -> 401
    FORBIDDEN          // logado, mas o papel não pode acessar a rota -> 403
}
