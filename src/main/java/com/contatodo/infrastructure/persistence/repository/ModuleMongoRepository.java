package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.ModuleDocument;
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
     * Finds active, non-deleted modules by user identifier.
     *
     * @param userOid User identifier.
     * @return List of module documents accessible to the user.
     */
    @Query("{ 'isActive': true, 'isDelete': false, 'permissions.userOid': ?0 }")
    List<ModuleDocument> findActiveModulesByUserOid(String userOid);
}
