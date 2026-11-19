package br.gov.sp.cps.fatec.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByUsername(String username);
}
