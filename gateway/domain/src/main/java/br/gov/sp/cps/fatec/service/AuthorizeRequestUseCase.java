package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.entity.Decision;

/**
 * Regras de acesso do sistema (Java puro, sem Spring):
 * <ul>
 *   <li>login, cadastro e logout são públicos;</li>
 *   <li>/api/admin/** e /api/reports/** -> somente ADMIN;</li>
 *   <li>/api/auctions/** (tela de lances) -> somente USER;</li>
 *   <li>/api/auth/me e /ws -> qualquer usuário logado;</li>
 *   <li>qualquer outra rota é negada (deny by default).</li>
 * </ul>
 */
public class AuthorizeRequestUseCase {

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    /**
     * @param method método HTTP
     * @param path   caminho da requisição
     * @param role   papel vindo do token já validado; {@code null} quando não há sessão válida
     */
    public Decision execute(String method, String path, String role) {
        if ("OPTIONS".equalsIgnoreCase(method) || isPublic(path)) return Decision.ALLOW;
        if (role == null) return Decision.UNAUTHENTICATED;

        if (under(path, "/api/admin") || under(path, "/api/reports")) return only(ADMIN, role);
        if (under(path, "/api/auctions")) return only(USER, role);
        if (under(path, "/api/auth/me") || under(path, "/ws")) return Decision.ALLOW;
        return Decision.FORBIDDEN;
    }

    private boolean isPublic(String path) {
        return path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.equals("/api/auth/logout");
    }

    private Decision only(String required, String role) {
        return required.equals(role) ? Decision.ALLOW : Decision.FORBIDDEN;
    }

    private boolean under(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
