package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Role;

import java.util.List;
import java.util.Optional;

/**
 * Port for role persistence operations.
 */
public interface RoleRepository {

    /**
     * Saves a role.
     *
     * @param role Role to save.
     * @return Saved role.
     */
    Role save(Role role);

    /**
     * Finds a role by identifier.
     *
     * @param id Role identifier.
     * @return Optional role.
     */
    Optional<Role> findById(String id);

    /**
     * Finds all roles that are not deleted.
     *
     * @return List of active roles.
     */
    List<Role> findAllNotDeleted();
}
