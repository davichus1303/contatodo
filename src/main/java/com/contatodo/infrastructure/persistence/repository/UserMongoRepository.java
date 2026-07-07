package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.UserDocument;
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
     * @param isDelete Delete flag.
     * @return List of user documents.
     */
    List<UserDocument> findByIsDelete(boolean isDelete);

    /**
     * Finds a user by email and delete status.
     *
     * @param email User email.
     * @param isDelete Delete flag.
     * @return Optional user document.
     */
    Optional<UserDocument> findByEmailAndIsDelete(String email, boolean isDelete);

    /**
     * Checks if a user exists by email.
     *
     * @param email User email.
     * @return True if user exists.
     */
    boolean existsByEmail(String email);
}
