package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.model.CompanyOid;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository port for acquisitions.
 */
public interface AcquisitionRepository {

    /**
     * Saves an acquisition.
     *
     * @param acquisition Acquisition to save.
     * @return Saved acquisition.
     */
    Acquisition save(Acquisition acquisition);

    /**
     * Finds acquisitions of the given company by date range.
     *
     * @param companyOid Owning company, {@code null} keeps acquisitions without a company.
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of acquisitions.
     */
    List<Acquisition> findByAcquisitionDateBetween(CompanyOid companyOid, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds acquisitions by product OID.
     *
     * @param productOid Product OID.
     * @return List of acquisitions for the product.
     */
    List<Acquisition> findByProductOid(String productOid);
}
