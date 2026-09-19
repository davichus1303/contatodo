package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.AcquisitionTypeDocument;
import com.contatodo.adapters.outbound.persistence.mapper.AcquisitionTypePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.AcquisitionTypeMongoRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the acquisition type repository port.
 *
 * <p>All reads are company-scoped through {@link #buildBaseQuery()}, which
 * combines the existing filters with the current user's company context.</p>
 */
@Repository
public class AcquisitionTypeRepositoryAdapter implements AcquisitionTypeRepository {

    private final AcquisitionTypeMongoRepository acquisitionTypeMongoRepository;
    private final AcquisitionTypePersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates an acquisition type repository adapter.
     *
     * @param acquisitionTypeMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public AcquisitionTypeRepositoryAdapter(
            AcquisitionTypeMongoRepository acquisitionTypeMongoRepository,
            AcquisitionTypePersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.acquisitionTypeMongoRepository = acquisitionTypeMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AcquisitionType save(AcquisitionType acquisitionType) {
        AcquisitionTypeDocument document = persistenceMapper.toDocument(acquisitionType);
        AcquisitionTypeDocument savedDocument = acquisitionTypeMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AcquisitionType> findById(String id) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("_id").is(id));
        AcquisitionTypeDocument document = mongoTemplate.findOne(query, AcquisitionTypeDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AcquisitionType> findActiveAndNotDeleted() {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("isActive").is(true).and("isDeleted").is(false));
        return toEntityList(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AcquisitionType> findByName(String name) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("name").is(name));
        AcquisitionTypeDocument document = mongoTemplate.findOne(query, AcquisitionTypeDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AcquisitionType> findAllNotDeleted() {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC, "name"));
        return toEntityList(query);
    }

    private List<AcquisitionType> toEntityList(Query query) {
        return mongoTemplate.find(query, AcquisitionTypeDocument.class).stream()
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