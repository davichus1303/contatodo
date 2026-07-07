package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Port for user persistence operations.
 */
public interface UserRepository {

    /**
     * Saves a user.
     *
     * @param user User to save.
     * @return Saved user.
     */
    User save(User user);

    /**
     * Finds a user by identifier.
     *
     * @param id User identifier.
     * @return Optional user.
     */
    Optional<User> findById(String id);

    /**
     * Finds a user by email.
     *
     * @param email User email.
     * @return Optional user.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all active users.
     *
     * @return List of active users.
     */
    List<User> findAllActive();

    /**
     * Checks if a user exists by email.
     *
     * @param email User email.
     * @return True if user exists.
     */
    boolean existsByEmail(String email);

    /**
     * Finds an active user by email.
     *
     * @param email User email.
     * @param isDelete Whether the user is deleted.
     * @return Optional user.
     */
    Optional<User> findActiveUserByEmail(String email, boolean isDelete);
}
