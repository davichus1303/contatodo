package com.contatodo.infrastructure.controller;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.ExpenseResponse;
import com.contatodo.application.services.ExpenseService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for expense endpoints.
 */
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Creates an expense controller.
     *
     * @param expenseService Expense service.
     */
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Creates a new expense.
     *
     * @param request Create expense request.
     * @return Created expense response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @RequestBody CreateExpenseRequest request
    ) {
        ExpenseResponse expense = expenseService.createExpense(request);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, expense));
    }

    /**
     * Retrieves all active and non-deleted expenses ordered from newest to oldest.
     *
     * @return List of expenses.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses() {
        List<ExpenseResponse> expenses = expenseService.getExpenses();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, expenses));
    }
}
