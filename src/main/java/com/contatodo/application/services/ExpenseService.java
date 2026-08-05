package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.ExpenseResponse;
import com.contatodo.application.mapper.ExpenseMapper;
import com.contatodo.application.validators.ExpenseValidator;
import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.repositories.ExpenseRepository;
import com.contatodo.shared.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service containing expense business logic.
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseValidator expenseValidator;
    private final ExpenseMapper expenseMapper;
    private final UserService userService;

    /**
     * Creates an expense service.
     *
     * @param expenseRepository Expense repository port.
     * @param expenseValidator Expense validator.
     * @param expenseMapper Expense mapper.
     * @param userService User service for security context.
     */
    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseValidator expenseValidator,
            ExpenseMapper expenseMapper,
            UserService userService
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseValidator = expenseValidator;
        this.expenseMapper = expenseMapper;
        this.userService = userService;
    }

    /**
     * Creates a new expense.
     *
     * @param request Create expense request.
     * @return Created expense response.
     */
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        expenseValidator.validateCreateRequest(request);

        String userOid = SecurityUtils.getCurrentUserOid(userService);

        Expense expense = expenseMapper.toEntity(request);
        expense.setUserOid(userOid);

        // Parse expenseDate if provided
        if (request.getExpenseDate() != null && !request.getExpenseDate().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            expense.setExpenseDate(LocalDateTime.parse(request.getExpenseDate(), formatter));
        } else {
            expense.setExpenseDate(LocalDateTime.now());
        }

        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponse(savedExpense);
    }

    /**
     * Retrieves all active and non-deleted expenses ordered from newest to oldest.
     *
     * @return List of expense responses.
     */
    public List<ExpenseResponse> getExpenses() {
        List<Expense> expenses = expenseRepository.findActiveAndNotDeletedOrderByCreatedDateDesc();
        return expenseMapper.toResponseList(expenses);
    }
}
