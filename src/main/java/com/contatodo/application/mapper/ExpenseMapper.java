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
     * @return Expense entity.
     */
    public Expense toEntity(CreateExpenseRequest request) {
        Expense expense = new Expense();
        expense.setAcquisitionOid(request.getAcquisitionOid());
        expense.setAcquisitionTypeOid(request.getAcquisitionTypeOid());
        expense.setName(request.getName());
        expense.setQuantity(request.getQuantity());
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setIsActive(true);
        expense.setIsDeleted(false);
        expense.setCreatedDate(LocalDateTime.now());
        expense.setUpdatedDate(LocalDateTime.now());
        return expense;
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
