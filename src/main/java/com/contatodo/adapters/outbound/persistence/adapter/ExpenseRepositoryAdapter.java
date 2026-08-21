package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.repositories.ExpenseRepository;
import com.contatodo.adapters.outbound.persistence.mapper.ExpensePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.ExpenseDocument;
import com.contatodo.adapters.outbound.persistence.repository.ExpenseMongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the expense repository port.
 */
@Repository
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseMongoRepository expenseMongoRepository;
    private final ExpensePersistenceMapper persistenceMapper;

    /**
     * Creates an expense repository adapter.
     *
     * @param expenseMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ExpenseRepositoryAdapter(
            ExpenseMongoRepository expenseMongoRepository,
            ExpensePersistenceMapper persistenceMapper
    ) {
        this.expenseMongoRepository = expenseMongoRepository;
        this.persistenceMapper = persistenceMapper;
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
        return persistenceMapper.toEntityList(
                expenseMongoRepository.findByIsActiveTrueAndIsDeletedFalseOrderByCreatedDateDesc()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense findById(String id) {
        return expenseMongoRepository.findById(id).map(persistenceMapper::toEntity).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> findActiveAndNotDeletedByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return persistenceMapper.toEntityList(
                expenseMongoRepository.findByIsActiveTrueAndIsDeletedFalseAndExpenseDateBetween(startDate, endDate)
        );
    }
}
