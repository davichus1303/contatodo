package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.SaleDocument;
import com.contatodo.infrastructure.persistence.repository.SaleMongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the sale repository port.
 */
@Repository
public class SaleRepositoryAdapter implements SaleRepository {

    private final SaleMongoRepository saleMongoRepository;
    private final PersistenceMapper persistenceMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Creates a sale repository adapter.
     *
     * @param saleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param productRepository Product repository.
     * @param userRepository User repository.
     */
    public SaleRepositoryAdapter(
            SaleMongoRepository saleMongoRepository,
            PersistenceMapper persistenceMapper,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.saleMongoRepository = saleMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sale save(Sale sale) {
        SaleDocument document = persistenceMapper.toSaleDocument(sale);
        SaleDocument savedDocument = saleMongoRepository.save(document);
        return persistenceMapper.toSaleEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Sale> findById(String id) {
        return saleMongoRepository.findById(id).map(persistenceMapper::toSaleEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findByUserOidAndSaleDate(String userOid, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return persistenceMapper.toSaleEntityList(saleMongoRepository.findByUserOidAndSaleDateBetween(userOid, startOfDay, endOfDay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Sale> findTopBySaleDateOrderBySaleNumberDesc(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return saleMongoRepository.findTopBySaleDateBetweenOrderBySaleNumberDesc(startOfDay, endOfDay).map(persistenceMapper::toSaleEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findByUserOidAndSaleDateBetween(String userOid, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return persistenceMapper.toSaleEntityList(saleMongoRepository.findByUserOidAndSaleDateBetween(userOid, startOfDay, endOfDay));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Sale> findTopBySaleDateBetweenOrderBySaleNumberDesc(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return saleMongoRepository.findTopBySaleDateBetweenOrderBySaleNumberDesc(startOfDay, endOfDay).map(persistenceMapper::toSaleEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findProductById(String productOid) {
        return productRepository.findById(productOid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findUserById(String userOid) {
        return userRepository.findById(userOid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
}
