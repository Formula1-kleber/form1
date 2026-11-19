package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public record UserDocument(@Id String id, @Indexed(unique = true) String username, String passwordHash, String role) {

    static UserDocument from(User u) {
        return new UserDocument(u.id(), u.username(), u.passwordHash(), u.role());
    }

    User toDomain() {
        return new User(id, username, passwordHash, role);
    }
}
