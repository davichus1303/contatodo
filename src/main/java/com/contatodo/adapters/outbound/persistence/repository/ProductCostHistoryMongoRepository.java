package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.ProductCostHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for product cost history.
 */
public interface ProductCostHistoryMongoRepository extends MongoRepository<ProductCostHistoryDocument, String> {

    /**
     * Finds product cost history by product OID.
     *
     * @param productOid Product OID.
     * @return List of product cost history documents.
     */
    List<ProductCostHistoryDocument> findByProductOid(String productOid);
}
