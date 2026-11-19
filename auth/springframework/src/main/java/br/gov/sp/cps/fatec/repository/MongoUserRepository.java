package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.User;
import br.gov.sp.cps.fatec.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adaptador: implementa a porta do domínio usando Spring Data MongoDB. */
@Component
class MongoUserRepository implements UserRepository {

    private final UserMongoRepository mongo;

    MongoUserRepository(UserMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongo.findByUsername(username).map(UserDocument::toDomain);
    }

    @Override
    public User save(User user) {
        return mongo.save(UserDocument.from(user)).toDomain();
    }
}
