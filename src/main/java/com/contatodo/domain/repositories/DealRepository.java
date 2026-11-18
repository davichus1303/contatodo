package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Deal;
import com.contatodo.domain.model.CompanyOid;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for deals.
 */
public interface DealRepository {

    /**
     * Saves a deal.
     *
     * @param deal Deal to save.
     * @return Saved deal.
     */
    Deal save(Deal deal);

    /**
     * Finds all non-deleted deals owned by the given company.
     *
     * @param companyOid Owning company.
     * @return List of deals.
     */
    List<Deal> findByCompanyAndNotDeleted(CompanyOid companyOid);

    /**
     * Finds all active, non-deleted deals owned by the given company.
     *
     * @param companyOid Owning company.
     * @return List of active, non-deleted deals.
     */
    List<Deal> findActiveNotDeletedByCompany(CompanyOid companyOid);

    /**
     * Finds the active, non-deleted deal for the given company and product.
     *
     * @param companyOid Owning company.
     * @param productOid Product identifier.
     * @return Optional matching deal.
     */
    Optional<Deal> findActiveByCompanyAndProduct(CompanyOid companyOid, String productOid);

    /**
     * Finds any non-deleted deal for the given company and product.
     *
     * @param companyOid Owning company.
     * @param productOid Product identifier.
     * @return Optional matching deal.
     */
    Optional<Deal> findNotDeletedByCompanyAndProduct(CompanyOid companyOid, String productOid);

    /**
     * Finds a deal by its identifier.
     *
     * @param id Deal identifier.
     * @return Optional deal.
     */
    Optional<Deal> findById(String id);
}
