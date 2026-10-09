package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;

import java.util.List;

/**
 * Repository port for units of measure.
 */
public interface UnitOfMeasureRepository {

    /**
     * Finds the active, non-deleted units of measure owned by the given
     * company.
     *
     * @param companyOid Owning company, {@code null} keeps units without a company.
     * @return List of active units of measure owned by that company.
     */
    List<UnitOfMeasure> findActiveByCompany(CompanyOid companyOid);
}
