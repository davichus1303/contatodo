package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Sale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Port for sale persistence operations.
 */
public interface SaleRepository {

    /**
     * Saves a sale.
     *
     * @param sale Sale to save.
     * @return Saved sale.
     */
    Sale save(Sale sale);

    /**
     * Finds a sale by identifier.
     *
     * @param id Sale identifier.
     * @return Optional sale.
     */
    Optional<Sale> findById(String id);

    /**
     * Finds sales by user identifier and date.
     *
     * @param userOid User identifier.
     * @param date Sale date.
     * @return List of sales.
     */
    List<Sale> findByUserOidAndSaleDate(String userOid, LocalDate date);

    /**
     * Finds the highest sale number for a specific date.
     *
     * @param date Sale date.
     * @return Optional sale with highest number.
     */
    Optional<Sale> findTopBySaleDateOrderBySaleNumberDesc(LocalDate date);

    /**
     * Finds sales by user identifier and date range.
     *
     * @param userOid User identifier.
     * @param startOfDay Start of the day.
     * @param endOfDay End of the day.
     * @return List of sales.
     */
    List<Sale> findByUserOidAndSaleDateBetween(String userOid, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * Finds the top sale by date range ordered by sale number descending.
     *
     * @param startOfDay Start of the day.
     * @param endOfDay End of the day.
     * @return Optional sale with highest number.
     */
    Optional<Sale> findTopBySaleDateBetweenOrderBySaleNumberDesc(LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * Finds a product by identifier.
     *
     * @param productOid Product identifier.
     * @return Optional product.
     */
    Optional<com.contatodo.domain.entities.Product> findProductById(String productOid);

    /**
     * Finds a user by identifier.
     *
     * @param userOid User identifier.
     * @return Optional user.
     */
    Optional<com.contatodo.domain.entities.User> findUserById(String userOid);

    /**
     * Finds a user by email.
     *
     * @param email User email.
     * @return Optional user.
     */
    Optional<com.contatodo.domain.entities.User> findUserByEmail(String email);

    /**
     * Updates a product.
     *
     * @param product Product to update.
     * @return Updated product.
     */
    com.contatodo.domain.entities.Product updateProduct(com.contatodo.domain.entities.Product product);
}
