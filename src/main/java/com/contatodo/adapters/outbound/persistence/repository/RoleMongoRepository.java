package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.RoleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for roles.
 */
@Repository
public interface RoleMongoRepository extends MongoRepository<RoleDocument, String> {

    /**
     * Finds all roles that are not logically deleted.
     *
     * @return List of non-deleted role documents.
     */
    @Query("{ 'isDeleted': false }")
    List<RoleDocument> findAllNotDeleted();
}
