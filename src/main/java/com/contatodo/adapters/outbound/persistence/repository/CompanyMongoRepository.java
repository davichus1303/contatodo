package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.CompanyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for companies.
 */
public interface CompanyMongoRepository extends MongoRepository<CompanyDocument, String> {

    /**
     * Finds all companies that are not deleted (both active and inactive).
     *
     * @return List of non-deleted company documents ordered by name.
     */
    List<CompanyDocument> findByIsDeletedFalseOrderByNameAsc();
}
