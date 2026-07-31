package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.ProductCostHistory;

import java.util.List;

/**
 * Repository port for product cost history.
 */
public interface ProductCostHistoryRepository {

    /**
     * Saves a product cost history.
     *
     * @param productCostHistory Product cost history to save.
     * @return Saved product cost history.
     */
    ProductCostHistory save(ProductCostHistory productCostHistory);

    /**
     * Finds product cost history by product OID.
     *
     * @param productOid Product OID.
     * @return List of product cost history.
     */
    List<ProductCostHistory> findByProductOid(String productOid);
}
