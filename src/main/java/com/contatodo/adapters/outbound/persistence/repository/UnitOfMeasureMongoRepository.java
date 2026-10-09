package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.UnitOfMeasureDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for units of measure.
 *
 * <p>The active, non-deleted and company filters are part of the derived query
 * so MongoDB returns only the matching documents.</p>
 */
public interface UnitOfMeasureMongoRepository extends MongoRepository<UnitOfMeasureDocument, String> {

    /**
     * Finds active and non-deleted units of measure for the given company.
     *
     * @param companyOid Owning company identifier.
     * @return List of unit of measure documents ordered by name.
     */
    List<UnitOfMeasureDocument> findByIsActiveTrueAndIsDeletedFalseAndCompanyOidOrderByNameAsc(String companyOid);

    /**
     * Finds the active and non-deleted unit of measure with the given name for
     * the given company.
     *
     * @param companyOid Owning company identifier.
     * @param name Unit of measure name.
     * @return Optional matching unit of measure document.
     */
    Optional<UnitOfMeasureDocument> findByIsActiveTrueAndIsDeletedFalseAndCompanyOidAndName(String companyOid, String name);
}
