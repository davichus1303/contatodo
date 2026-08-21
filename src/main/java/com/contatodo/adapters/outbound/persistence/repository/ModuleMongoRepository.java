package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.ModuleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for modules.
 */
@Repository
public interface ModuleMongoRepository extends MongoRepository<ModuleDocument, String> {

    /**
     * Finds all active, non-deleted modules.
     *
     * @return List of active module documents.
     */
    @Query("{ 'isActive': true, 'isDelete': false }")
    List<ModuleDocument> findActiveModules();
}
