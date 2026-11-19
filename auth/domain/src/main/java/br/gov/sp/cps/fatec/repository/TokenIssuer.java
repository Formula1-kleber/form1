package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.User;

public interface TokenIssuer {
    String issue(User user);
}
