package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.ProductDocument;
import com.contatodo.infrastructure.persistence.repository.ProductMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the product repository port.
 */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductMongoRepository productMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates a product repository adapter.
     *
     * @param productMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ProductRepositoryAdapter(ProductMongoRepository productMongoRepository, PersistenceMapper persistenceMapper) {
        this.productMongoRepository = productMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product save(Product product) {
        ProductDocument document = persistenceMapper.toProductDocument(product);
        ProductDocument savedDocument = productMongoRepository.save(document);
        return persistenceMapper.toProductEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findById(String id) {
        return productMongoRepository.findById(id).map(persistenceMapper::toProductEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findAll() {
        return persistenceMapper.toProductEntityList(productMongoRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findByCode(String code) {
        return productMongoRepository.findByCode(code).map(persistenceMapper::toProductEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByName(String name) {
        return persistenceMapper.toProductEntityList(productMongoRepository.findByName(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findTopByOrderByCodeDesc() {
        return productMongoRepository.findTopByOrderByCodeDesc().map(persistenceMapper::toProductEntity);
    }
}
