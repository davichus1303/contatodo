package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for products.
 */
public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {
}