package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.ProductCostHistoryDocument;
import com.contatodo.adapters.outbound.persistence.mapper.ProductCostHistoryPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.ProductCostHistoryMongoRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter implementing the product cost history repository port.
 *
 * <p>All reads are company-scoped through {@link #buildBaseQuery()}, which
 * applies the current user's company context.</p>
 */
@Repository
public class ProductCostHistoryRepositoryAdapter implements ProductCostHistoryRepository {

    private final ProductCostHistoryMongoRepository productCostHistoryMongoRepository;
    private final ProductCostHistoryPersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates a product cost history repository adapter.
     *
     * @param productCostHistoryMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public ProductCostHistoryRepositoryAdapter(
            ProductCostHistoryMongoRepository productCostHistoryMongoRepository,
            ProductCostHistoryPersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.productCostHistoryMongoRepository = productCostHistoryMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductCostHistory save(ProductCostHistory history) {
        ProductCostHistoryDocument document = persistenceMapper.toDocument(history);
        ProductCostHistoryDocument savedDocument = productCostHistoryMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductCostHistory> findByProductOid(String productOid) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("productOid").is(productOid));
        return mongoTemplate.find(query, ProductCostHistoryDocument.class).stream()
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