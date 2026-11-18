package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.DealDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for deals.
 */
public interface DealMongoRepository extends MongoRepository<DealDocument, String> {

    /**
     * Finds all non-deleted deals for a company.
     *
     * @param companyOid Company identifier.
     * @return List of non-deleted deal documents.
     */
    List<DealDocument> findByCompanyOidAndIsDeleteFalse(String companyOid);

    /**
     * Finds all active and non-deleted deals for a company.
     *
     * @param companyOid Company identifier.
     * @return List of active, non-deleted deal documents.
     */
    List<DealDocument> findByCompanyOidAndIsActiveTrueAndIsDeleteFalse(String companyOid);

    /**
     * Finds the active, non-deleted deal for a company and product.
     *
     * @param companyOid Company identifier.
     * @param productOid Product identifier.
     * @return Optional matching deal document.
     */
    Optional<DealDocument> findByCompanyOidAndProductOidAndIsActiveTrueAndIsDeleteFalse(
            String companyOid, String productOid);

    /**
     * Finds any non-deleted deal for a company and product.
     *
     * @param companyOid Company identifier.
     * @param productOid Product identifier.
     * @return Optional matching deal document.
     */
    Optional<DealDocument> findByCompanyOidAndProductOidAndIsDeleteFalse(String companyOid, String productOid);
}
