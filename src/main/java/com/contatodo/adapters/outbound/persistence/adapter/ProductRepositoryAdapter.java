package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.adapters.outbound.persistence.mapper.ProductPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.ProductDocument;
import com.contatodo.adapters.outbound.persistence.repository.ProductMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the product repository port.
 */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductMongoRepository productMongoRepository;
    private final ProductPersistenceMapper persistenceMapper;

    /**
     * Creates a product repository adapter.
     *
     * @param productMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ProductRepositoryAdapter(ProductMongoRepository productMongoRepository, ProductPersistenceMapper persistenceMapper) {
        this.productMongoRepository = productMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product save(Product product) {
        ProductDocument document = persistenceMapper.toDocument(product);
        ProductDocument savedDocument = productMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findById(String id) {
        return productMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findAll() {
        return persistenceMapper.toEntityList(productMongoRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findByCode(String code) {
        return productMongoRepository.findByCode(code).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByName(String name) {
        return persistenceMapper.toEntityList(productMongoRepository.findByName(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findTopByOrderByCodeDesc() {
        return productMongoRepository.findTopByOrderByCodeDesc().map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByUserOidAndStockGreaterThan(String userOid, Integer stock) {
        return persistenceMapper.toEntityList(productMongoRepository.findByUserOidAndStockGreaterThan(userOid, stock));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findByNameAndUserOid(String name, String userOid) {
        return productMongoRepository.findByNameAndUserOidAndIsActiveTrue(name, userOid).map(persistenceMapper::toEntity);
    }
}
