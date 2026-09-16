package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for users.
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
     * Finds all users that are not deleted.
     *
     * @param isDeleted Delete flag.
     * @return List of user documents.
     */
    List<UserDocument> findByIsDeleted(boolean isDeleted);

    /**
     * Finds a user by email and delete status.
     *
     * @param email User email.
     * @param isDeleted Delete flag.
     * @return Optional user document.
     */
    Optional<UserDocument> findByEmailAndIsDeleted(String email, boolean isDeleted);

    /**
     * Checks if a user exists by email.
     *
     * @param email User email.
     * @return True if user exists.
     */
    boolean existsByEmail(String email);
}
