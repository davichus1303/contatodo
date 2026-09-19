package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.adapters.outbound.persistence.mapper.UserPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.UserDocument;
import com.contatodo.adapters.outbound.persistence.repository.UserMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the user repository port.
 *
 * <p>Company-scoped reads build their query through {@link #buildBaseQuery()}.
 * Email lookups used during authentication stay unscoped on purpose.</p>
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository userMongoRepository;
    private final UserPersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates a user repository adapter.
     *
     * @param userMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public UserRepositoryAdapter(
            UserMongoRepository userMongoRepository,
            UserPersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.userMongoRepository = userMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        UserDocument document = persistenceMapper.toDocument(user);
        UserDocument savedDocument = userMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findById(String id) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("_id").is(id));
        UserDocument document = mongoTemplate.findOne(query, UserDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userMongoRepository.findByEmail(email).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAllActive() {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        return mongoTemplate.find(query, UserDocument.class).stream()
                .map(persistenceMapper::toEntity)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByEmail(String email) {
        return userMongoRepository.existsByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findActiveUserByEmail(String email, boolean isDeleted) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("email").is(email).and("isDeleted").is(isDeleted));
        UserDocument document = mongoTemplate.findOne(query, UserDocument.class);
        return document != null ? Optional.of(persistenceMapper.toEntity(document)) : Optional.empty();
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