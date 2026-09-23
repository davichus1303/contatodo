package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.SaleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for sales.
 */
public interface SaleMongoRepository extends MongoRepository<SaleDocument, String> {
}