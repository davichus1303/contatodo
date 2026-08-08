package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Expense;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository port for expenses.
 */
public interface ExpenseRepository {

    /**
     * Saves an expense.
     *
     * @param expense Expense to save.
     * @return Saved expense.
     */
    Expense save(Expense expense);

    /**
     * Finds all active and non-deleted expenses ordered from newest to oldest.
     *
     * @return List of expenses ordered by created date descending.
     */
    List<Expense> findActiveAndNotDeletedOrderByCreatedDateDesc();

    /**
     * Finds an expense by ID.
     *
     * @param id Expense ID.
     * @return Expense if found, null otherwise.
     */
    Expense findById(String id);

    /**
     * Finds all active and non-deleted expenses within a date range.
     *
     * @param startDate Start of date range (inclusive).
     * @param endDate End of date range (inclusive).
     * @return List of expenses within the date range.
     */
    List<Expense> findActiveAndNotDeletedByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
