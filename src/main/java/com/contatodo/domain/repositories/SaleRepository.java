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
     * Finds sales on a specific date.
     *
     * @param date Sale date.
     * @return List of sales.
     */
    List<Sale> findBySaleDate(LocalDate date);

    /**
     * Finds sales within a date range.
     *
     * @param startOfDay Start of the range.
     * @param endOfDay End of the range.
     * @return List of sales.
     */
    List<Sale> findBySaleDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
