package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.ProductCostHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for product cost history.
 */
public interface ProductCostHistoryMongoRepository extends MongoRepository<ProductCostHistoryDocument, String> {
}