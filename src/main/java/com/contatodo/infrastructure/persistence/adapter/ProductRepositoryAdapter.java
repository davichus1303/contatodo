package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.ProductDocument;
import com.contatodo.infrastructure.persistence.repository.ProductMongoRepository;
import com.contatodo.infrastructure.persistence.repository.UserMongoRepository;
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
    private final UserMongoRepository userMongoRepository;

    /**
     * Creates a product repository adapter.
     *
     * @param productMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param userMongoRepository User Mongo repository.
     */
    public ProductRepositoryAdapter(ProductMongoRepository productMongoRepository, PersistenceMapper persistenceMapper, UserMongoRepository userMongoRepository) {
        this.productMongoRepository = productMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.userMongoRepository = userMongoRepository;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByUserOidAndStockGreaterThan(String userOid, Integer stock) {
        return persistenceMapper.toProductEntityList(productMongoRepository.findByUserOidAndStockGreaterThan(userOid, stock));
    }

    /**
     * Finds a user by email.
     *
     * @param email User email.
     * @return Optional user.
     */
    public Optional<User> findUserByEmail(String email) {
        return userMongoRepository.findByEmail(email).map(persistenceMapper::toUserEntity);
    }
}
