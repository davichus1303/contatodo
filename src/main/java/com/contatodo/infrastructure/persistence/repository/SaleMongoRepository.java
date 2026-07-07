package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.SaleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB repository for sales.
 */
@Repository
public interface SaleMongoRepository extends MongoRepository<SaleDocument, String> {

    /**
     * Finds sales by user identifier and sale date range.
     *
     * @param userOid User identifier.
     * @param startOfDay Start of the day.
     * @param endOfDay End of the day.
     * @return List of sale documents.
     */
    @Query("{ 'userOid': ?0, 'saleDate': { $gte: ?1, $lte: ?2 } }")
    List<SaleDocument> findByUserOidAndSaleDateBetween(String userOid, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * Finds the top sale by sale date range ordered by sale number descending.
     *
     * @param startOfDay Start of the day.
     * @param endOfDay End of the day.
     * @return Optional sale document.
     */
    @Query("{ 'saleDate': { $gte: ?0, $lte: ?1 } }")
    java.util.Optional<SaleDocument> findTopBySaleDateBetweenOrderBySaleNumberDesc(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
