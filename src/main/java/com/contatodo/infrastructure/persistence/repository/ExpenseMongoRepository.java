package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.ExpenseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for expenses.
 */
public interface ExpenseMongoRepository extends MongoRepository<ExpenseDocument, String> {

    /**
     * Finds active and non-deleted expenses ordered by created date descending.
     *
     * @return List of expense documents.
     */
    List<ExpenseDocument> findByIsActiveTrueAndIsDeletedFalseOrderByCreatedDateDesc();

    /**
     * Finds an expense by ID.
     *
     * @param id Expense ID.
     * @return Optional expense document.
     */
    Optional<ExpenseDocument> findById(String id);
}
