package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.ExpenseDocument;
import com.contatodo.domain.entities.Expense;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link Expense} domain entities and MongoDB expense
 * documents.
 */
@Component
public class ExpensePersistenceMapper {

    /**
     * Maps an expense entity to a document.
     *
     * @param expense Expense entity.
     * @return Expense document.
     */
    public ExpenseDocument toDocument(Expense expense) {
        ExpenseDocument document = new ExpenseDocument();
        document.setId(expense.getId());
        document.setAcquisitionOid(expense.getAcquisitionOid());
        document.setAcquisitionTypeOid(expense.getAcquisitionTypeOid());
        document.setName(expense.getName());
        document.setQuantity(expense.getQuantity());
        document.setAmount(expense.getAmount());
        document.setCurrency(expense.getCurrency());
        document.setExpenseDate(expense.getExpenseDate());
        document.setUserOid(expense.getUserOid());
        document.setIsActive(expense.getIsActive());
        document.setIsDeleted(expense.getIsDeleted());
        document.setCreatedDate(expense.getCreatedDate());
        document.setUpdatedDate(expense.getUpdatedDate());
        return document;
    }

    /**
     * Maps an expense document to an entity.
     *
     * @param document Expense document.
     * @return Expense entity.
     */
    public Expense toEntity(ExpenseDocument document) {
        return Expense.builder()
                .id(document.getId())
                .acquisitionOid(document.getAcquisitionOid())
                .acquisitionTypeOid(document.getAcquisitionTypeOid())
                .name(document.getName())
                .quantity(document.getQuantity())
                .amount(document.getAmount())
                .currency(document.getCurrency())
                .expenseDate(document.getExpenseDate())
                .userOid(document.getUserOid())
                .isActive(document.getIsActive())
                .isDeleted(document.getIsDeleted())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .build();
    }

    /**
     * Maps a list of expense documents to entities.
     *
     * @param documents Expense documents.
     * @return Expense entities.
     */
    public List<Expense> toEntityList(List<ExpenseDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
