package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.ExpenseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for expenses.
 */
public interface ExpenseMongoRepository extends MongoRepository<ExpenseDocument, String> {
}