package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.SaleDocument;
import com.contatodo.adapters.outbound.persistence.mapper.SalePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.SaleMongoRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.shared.utils.DateUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the sale repository port on top of MongoDB.
 *
 * <p>All reads are company-scoped through {@link #buildBaseQuery()}, which
 * combines the existing filters with the current user's company context.</p>
 */
@Repository
public class SaleRepositoryAdapter implements SaleRepository {

    private final SaleMongoRepository saleMongoRepository;
    private final SalePersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates a sale repository adapter.
     *
     * @param saleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public SaleRepositoryAdapter(
            SaleMongoRepository saleMongoRepository,
            SalePersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.saleMongoRepository = saleMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sale save(Sale sale) {
        SaleDocument document = persistenceMapper.toDocument(sale);
        SaleDocument savedDocument = saleMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findBySaleDate(LocalDate date) {
        LocalDateTime startOfDay = DateUtils.startOfDay(date);
        LocalDateTime endOfDay = DateUtils.endOfDay(date);
        return findBySaleDateBetween(startOfDay, endOfDay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findBySaleDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("saleDate").gte(startOfDay).lte(endOfDay));
        return toEntityList(query);
    }

    private List<Sale> toEntityList(Query query) {
        return mongoTemplate.find(query, SaleDocument.class).stream()
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