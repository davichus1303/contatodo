package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Module;

import java.util.List;
import java.util.Optional;

/**
 * Port for module persistence operations.
 */
public interface ModuleRepository {

    /**
     * Saves a module.
     *
     * @param module Module to save.
     * @return Saved module.
     */
    Module save(Module module);

    /**
     * Finds a module by identifier.
     *
     * @param id Module identifier.
     * @return Optional module.
     */
    Optional<Module> findById(String id);

    /**
     * Finds active, non-deleted modules by user identifier.
     *
     * @param userOid User identifier.
     * @return List of modules accessible to the user.
     */
    List<Module> findActiveModulesByUserOid(String userOid);
}
