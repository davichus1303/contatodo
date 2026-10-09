package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for units of measure.
 */
public interface UnitOfMeasureRepository {

    /**
     * Saves a unit of measure.
     *
     * @param unitOfMeasure Unit of measure to save.
     * @return Saved unit of measure.
     */
    UnitOfMeasure save(UnitOfMeasure unitOfMeasure);

    /**
     * Finds the active, non-deleted units of measure owned by the given
     * company.
     *
     * @param companyOid Owning company, {@code null} keeps units without a company.
     * @return List of active units of measure owned by that company.
     */
    List<UnitOfMeasure> findActiveByCompany(CompanyOid companyOid);

    /**
     * Finds the active, non-deleted unit of measure with the given name inside
     * the given company.
     *
     * @param companyOid Owning company, {@code null} keeps units without a company.
     * @param name Unit of measure name.
     * @return Optional matching unit of measure.
     */
    Optional<UnitOfMeasure> findActiveByCompanyAndName(CompanyOid companyOid, String name);
}
