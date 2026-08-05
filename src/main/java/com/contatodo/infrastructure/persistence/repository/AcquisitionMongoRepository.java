package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.AcquisitionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data MongoDB repository for acquisitions.
 */
public interface AcquisitionMongoRepository extends MongoRepository<AcquisitionDocument, String> {

    /**
     * Finds acquisitions by user OID and date range.
     *
     * @param userOid User OID.
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of acquisition documents.
     */
    List<AcquisitionDocument> findByUserOidAndAcquisitionDateBetweenAndIsDeletedFalse(
            String userOid, LocalDateTime startDate, LocalDateTime endDate
    );

    /**
     * Finds acquisitions by user OID ordered by acquisition date descending.
     *
     * @param userOid User OID.
     * @return List of acquisition documents.
     */
    List<AcquisitionDocument> findByUserOidAndIsDeletedFalseOrderByAcquisitionDateDesc(String userOid);

    /**
     * Finds acquisitions by product OID.
     *
     * @param productOid Product OID.
     * @return List of acquisition documents.
     */
    List<AcquisitionDocument> findByProductOidAndIsDeletedFalse(String productOid);
}
