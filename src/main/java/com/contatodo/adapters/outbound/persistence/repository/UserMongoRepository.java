package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for users.
 *
 * <p>Only the unscoped lookups needed for authentication live here; all
 * company-scoped reads are implemented in {@code UserRepositoryAdapter}.</p>
 */
public interface UserMongoRepository extends MongoRepository<UserDocument, String> {

    /**
     * Finds a user by email.
     *
     * @param email User email.
     * @return Optional user document.
     */
    Optional<UserDocument> findByEmail(String email);

    /**
     * Checks if a user exists by email.
     *
     * @param email User email.
     * @return True if user exists.
     */
    boolean existsByEmail(String email);
}