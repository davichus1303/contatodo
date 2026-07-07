package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Product;

import java.util.List;
import java.util.Optional;

/**
 * Port for product persistence operations.
 */
public interface ProductRepository {

    /**
     * Saves a product.
     *
     * @param product Product to save.
     * @return Saved product.
     */
    Product save(Product product);

    /**
     * Finds a product by identifier.
     *
     * @param id Product identifier.
     * @return Optional product.
     */
    Optional<Product> findById(String id);

    /**
     * Finds all products.
     *
     * @return List of products.
     */
    List<Product> findAll();

    /**
     * Finds a product by code.
     *
     * @param code Product code.
     * @return Optional product.
     */
    Optional<Product> findByCode(String code);

    /**
     * Finds products by name.
     *
     * @param name Product name.
     * @return List of products.
     */
    List<Product> findByName(String name);

    /**
     * Finds the product with the highest code.
     *
     * @return Optional product with highest code.
     */
    Optional<Product> findTopByOrderByCodeDesc();

    /**
     * Finds products by user OID with stock greater than 0.
     *
     * @param userOid User OID.
     * @return List of products.
     */
    List<Product> findByUserOidAndStockGreaterThan(String userOid, Integer stock);
}
