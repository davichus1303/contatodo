package com.contatodo.infrastructure.persistence.repository;

import com.contatodo.infrastructure.persistence.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for products.
 */
public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {

    /**
     * Finds a product by code.
     *
     * @param code Product code.
     * @return Optional product document.
     */
    Optional<ProductDocument> findByCode(String code);

    /**
     * Finds products by name.
     *
     * @param name Product name.
     * @return List of product documents.
     */
    List<ProductDocument> findByName(String name);

    /**
     * Finds the product with the highest code.
     *
     * @return Optional product document.
     */
    Optional<ProductDocument> findTopByOrderByCodeDesc();
}
