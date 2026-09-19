package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.ProductDocument;
import com.contatodo.adapters.outbound.persistence.mapper.ProductPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.ProductMongoRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.repositories.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the product repository port.
 *
 * <p>All reads are company-scoped through {@link #buildBaseQuery()}, which
 * combines the existing filters with the current user's company context.</p>
 */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductMongoRepository productMongoRepository;
    private final ProductPersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates a product repository adapter.
     *
     * @param productMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public ProductRepositoryAdapter(
            ProductMongoRepository productMongoRepository,
            ProductPersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.productMongoRepository = productMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
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
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("_id").is(id));
        ProductDocument document = mongoTemplate.findOne(query, ProductDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findAll() {
        return toEntityList(buildBaseQuery());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findByCode(String code) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("code").is(code));
        ProductDocument document = mongoTemplate.findOne(query, ProductDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByName(String name) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("name").is(name));
        return toEntityList(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findTopByOrderByCodeDesc() {
        Query query = buildBaseQuery();
        query.with(Sort.by(Sort.Direction.DESC, "code"));
        query.limit(1);
        ProductDocument document = mongoTemplate.findOne(query, ProductDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByStockGreaterThan(Integer stock) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("stock").gt(stock));
        return toEntityList(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findByNameAndIsActiveTrue(String name) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("name").is(name).and("isActive").is(true));
        ProductDocument document = mongoTemplate.findOne(query, ProductDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    private List<Product> toEntityList(Query query) {
        return mongoTemplate.find(query, ProductDocument.class).stream()
                .map(persistenceMapper::toEntity)
                .toList();
    }

    /**
     * Builds the base query applying the current company filter.
     *
     * <p>Root users (no {@code companyOid} claim) are not filtered. An
     * authenticated company user must resolve its company or the query is
     * rejected. When there is no security context (internal or test flows)
     * the query is returned unfiltered so they keep working.</p>
     *
     * @return Base query with the company filter when applicable.
     */
    private Query buildBaseQuery() {
        if (companyContextProvider.isRoot()) {
            return new Query();
        }
        return companyContextProvider.currentCompanyOid()
                .map(companyOid -> Query.query(Criteria.where("companyOid").is(companyOid.value())))
                .orElseGet(() -> {
                    if (companyContextProvider.isAuthenticated()) {
                        throw new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED);
                    }
                    return new Query();
                });
    }
}