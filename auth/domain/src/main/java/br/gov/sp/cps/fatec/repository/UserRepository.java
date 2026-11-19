package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}
