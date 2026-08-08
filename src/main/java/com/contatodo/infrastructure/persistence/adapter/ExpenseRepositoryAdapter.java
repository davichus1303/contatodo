package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.repositories.ExpenseRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.ExpenseDocument;
import com.contatodo.infrastructure.persistence.repository.ExpenseMongoRepository;
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
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates an expense repository adapter.
     *
     * @param expenseMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ExpenseRepositoryAdapter(
            ExpenseMongoRepository expenseMongoRepository,
            PersistenceMapper persistenceMapper
    ) {
        this.expenseMongoRepository = expenseMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense save(Expense expense) {
        ExpenseDocument document = persistenceMapper.toExpenseDocument(expense);
        ExpenseDocument savedDocument = expenseMongoRepository.save(document);
        return persistenceMapper.toExpenseEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> findActiveAndNotDeletedOrderByCreatedDateDesc() {
        return persistenceMapper.toExpenseEntityList(
                expenseMongoRepository.findByIsActiveTrueAndIsDeletedFalseOrderByCreatedDateDesc()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense findById(String id) {
        return expenseMongoRepository.findById(id).map(persistenceMapper::toExpenseEntity).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Expense> findActiveAndNotDeletedByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return persistenceMapper.toExpenseEntityList(
                expenseMongoRepository.findByIsActiveTrueAndIsDeletedFalseAndExpenseDateBetween(startDate, endDate)
        );
    }
}
