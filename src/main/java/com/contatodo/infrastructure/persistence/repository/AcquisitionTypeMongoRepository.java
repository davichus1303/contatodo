package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.AcquisitionTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for acquisition types.
 */
public interface AcquisitionTypeMongoRepository extends MongoRepository<AcquisitionTypeDocument, String> {

    /**
     * Finds active and non-deleted acquisition types.
     *
     * @return List of acquisition type documents.
     */
    List<AcquisitionTypeDocument> findByIsActiveTrueAndIsDeletedFalse();

    /**
     * Finds an acquisition type by name.
     *
     * @param name Acquisition type name.
     * @return Optional acquisition type document.
     */
    Optional<AcquisitionTypeDocument> findByName(String name);

    /**
     * Finds all acquisition types that are not deleted (both active and inactive).
     *
     * @return List of non-deleted acquisition type documents ordered by name.
     */
    List<AcquisitionTypeDocument> findByIsDeletedFalseOrderByNameAsc();
}
