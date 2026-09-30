package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.model.CompanyOid;

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
     * Finds all products for the given company.
     *
     * @param companyOid Owning company, {@code null} keeps products without a company.
     * @return List of products.
     */
    List<Product> findAll(CompanyOid companyOid);

    /**
     * Finds a product by code for the given company.
     *
     * @param companyOid Owning company, {@code null} keeps products without a company.
     * @param code Product code.
     * @return Optional product.
     */
    Optional<Product> findByCode(CompanyOid companyOid, String code);

    /**
     * Finds products by name for the given company.
     *
     * @param companyOid Owning company, {@code null} keeps products without a company.
     * @param name Product name.
     * @return List of products.
     */
    List<Product> findByName(CompanyOid companyOid, String name);

    /**
     * Finds the product with the highest code.
     *
     * @return Optional product with highest code.
     */
    Optional<Product> findTopByOrderByCodeDesc();

    /**
     * Finds products with stock greater than the given amount, for the given company.
     *
     * @param companyOid Owning company, {@code null} keeps products without a company.
     * @param stock Minimum stock.
     * @return List of products.
     */
    List<Product> findByStockGreaterThan(CompanyOid companyOid, Integer stock);

    /**
     * Finds an active product by name.
     *
     * @param name Product name.
     * @return Optional product.
     */
    Optional<Product> findByNameAndIsActiveTrue(String name);
}
