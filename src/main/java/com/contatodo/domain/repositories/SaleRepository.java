package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Sale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * Finds sales of a user on a specific date.
     *
     * @param userOid User identifier.
     * @param date Sale date.
     * @return List of sales.
     */
    List<Sale> findByUserOidAndSaleDate(String userOid, LocalDate date);

    /**
     * Finds sales of a user within a date range.
     *
     * @param userOid User identifier.
     * @param startOfDay Start of the range.
     * @param endOfDay End of the range.
     * @return List of sales.
     */
    List<Sale> findByUserOidAndSaleDateBetween(String userOid, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
