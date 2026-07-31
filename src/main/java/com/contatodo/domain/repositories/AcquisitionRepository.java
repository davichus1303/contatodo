package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Acquisition;

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
     * Finds acquisitions by user OID and date range.
     *
     * @param userOid User OID.
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of acquisitions.
     */
    List<Acquisition> findByUserOidAndAcquisitionDateBetween(String userOid, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds acquisitions by user OID ordered by acquisition date descending.
     *
     * @param userOid User OID.
     * @return List of acquisitions.
     */
    List<Acquisition> findByUserOidOrderByAcquisitionDateDesc(String userOid);

    /**
     * Finds acquisitions by product OID.
     *
     * @param productOid Product OID.
     * @return List of acquisitions for the product.
     */
    List<Acquisition> findByProductOid(String productOid);
}
