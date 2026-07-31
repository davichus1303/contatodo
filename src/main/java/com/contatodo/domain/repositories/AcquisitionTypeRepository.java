package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.AcquisitionType;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for acquisition types.
 */
public interface AcquisitionTypeRepository {

    /**
     * Saves an acquisition type.
     *
     * @param acquisitionType Acquisition type to save.
     * @return Saved acquisition type.
     */
    AcquisitionType save(AcquisitionType acquisitionType);

    /**
     * Finds an acquisition type by ID.
     *
     * @param id Acquisition type ID.
     * @return Optional acquisition type.
     */
    Optional<AcquisitionType> findById(String id);

    /**
     * Finds all active and non-deleted acquisition types.
     *
     * @return List of active acquisition types.
     */
    List<AcquisitionType> findActiveAndNotDeleted();

    /**
     * Finds an acquisition type by name.
     *
     * @param name Acquisition type name.
     * @return Optional acquisition type.
     */
    Optional<AcquisitionType> findByName(String name);
}
