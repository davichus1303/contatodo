package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.ExpenseDocument;
import com.contatodo.adapters.outbound.persistence.mapper.ExpensePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.ExpenseMongoRepository;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.repositories.ExpenseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the expense repository port.
 *
 * <p>All reads are company-scoped through {@link #buildBaseQuery()}, which
 * combines the existing filters with the current user's company context.</p>
 */
@Repository
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseMongoRepository expenseMongoRepository;
    private final ExpensePersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates an expense repository adapter.
     *
     * @param expenseMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     * @param companyContextProvider Company context provider.
     */
    public ExpenseRepositoryAdapter(
            ExpenseMongoRepository expenseMongoRepository,
            ExpensePersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate,
            CompanyContextProvider companyContextProvider
    ) {
        this.expenseMongoRepository = expenseMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
        this.companyContextProvider = companyContextProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense save(Expense expense) {
        ExpenseDocument document = persistenceMapper.toDocument(expense);
        ExpenseDocument savedDocument = expenseMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> findActiveAndNotDeletedOrderByCreatedDateDesc() {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("isActive").is(true).and("isDeleted").is(false));
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));
        return mongoTemplate.find(query, ExpenseDocument.class).stream()
                .map(persistenceMapper::toEntity)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense findById(String id) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("_id").is(id));
        ExpenseDocument document = mongoTemplate.findOne(query, ExpenseDocument.class);
        return document != null ? persistenceMapper.toEntity(document) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> findActiveAndNotDeletedByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Query query = buildBaseQuery();
        query.addCriteria(Criteria.where("isActive").is(true)
                .and("isDeleted").is(false)
                .and("expenseDate").gte(startDate).lte(endDate));
        return mongoTemplate.find(query, ExpenseDocument.class).stream()
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