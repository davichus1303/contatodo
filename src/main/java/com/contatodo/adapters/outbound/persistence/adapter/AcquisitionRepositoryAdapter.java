package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.AcquisitionDocument;
import com.contatodo.adapters.outbound.persistence.mapper.AcquisitionPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.AcquisitionMongoRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.repositories.AcquisitionRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the acquisition repository port.
 *
 * <p>All reads are company-scoped through {@link #buildBaseQuery()}, which
 * combines the existing filters with the current user's company context.</p>
 */
@Repository
public class AcquisitionRepositoryAdapter implements AcquisitionRepository {

    private final AcquisitionMongoRepository acquisitionMongoRepository;
    private final AcquisitionPersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates an acquisition repository adapter.
     *
     * @param acquisitionMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public AcquisitionRepositoryAdapter(
            AcquisitionMongoRepository acquisitionMongoRepository,
            AcquisitionPersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.acquisitionMongoRepository = acquisitionMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Acquisition save(Acquisition acquisition) {
        AcquisitionDocument document = persistenceMapper.toDocument(acquisition);
        AcquisitionDocument savedDocument = acquisitionMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByAcquisitionDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("acquisitionDate").gte(startDate).lte(endDate)
                .and("isDeleted").is(false));
        return toEntityList(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByProductOid(String productOid) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("productOid").is(productOid).and("isDeleted").is(false));
        return toEntityList(query);
    }

    private List<Acquisition> toEntityList(Query query) {
        return mongoTemplate.find(query, AcquisitionDocument.class).stream()
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