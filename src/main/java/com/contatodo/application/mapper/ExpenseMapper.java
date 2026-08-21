package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.ExpenseResponse;
import com.contatodo.domain.entities.Expense;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for expense entities and DTOs.
 */
@Component
public class ExpenseMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create expense request.
     * @param userOid Authenticated user identifier.
     * @param expenseDate Resolved expense date.
     * @return Expense entity.
     */
    public Expense toEntity(CreateExpenseRequest request, String userOid, LocalDateTime expenseDate) {
        LocalDateTime now = LocalDateTime.now();
        return Expense.builder()
                .acquisitionOid(request.getAcquisitionOid())
                .acquisitionTypeOid(request.getAcquisitionTypeOid())
                .name(request.getName())
                .quantity(request.getQuantity())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .expenseDate(expenseDate)
                .userOid(userOid)
                .isActive(true)
                .isDeleted(false)
                .createdDate(now)
                .updatedDate(now)
                .build();
    }

    /**
     * Maps an expense entity to a response DTO.
     *
     * @param expense Expense entity.
     * @return Expense response.
     */
    public ExpenseResponse toResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setAcquisitionOid(expense.getAcquisitionOid());
        response.setAcquisitionTypeOid(expense.getAcquisitionTypeOid());
        response.setName(expense.getName());
        response.setQuantity(expense.getQuantity());
        response.setAmount(expense.getAmount());
        response.setCurrency(expense.getCurrency());
        response.setExpenseDate(expense.getExpenseDate());
        response.setUserOid(expense.getUserOid());
        response.setIsActive(expense.getIsActive());
        response.setIsDeleted(expense.getIsDeleted());
        response.setCreatedDate(expense.getCreatedDate());
        response.setUpdatedDate(expense.getUpdatedDate());
        return response;
    }

    /**
     * Maps a list of expenses to response DTOs.
     *
     * @param expenses Expense entities.
     * @return Expense responses.
     */
    public List<ExpenseResponse> toResponseList(List<Expense> expenses) {
        return expenses.stream().map(this::toResponse).toList();
    }
}
