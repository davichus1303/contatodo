package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.ExpenseResponse;
import com.contatodo.application.mapper.ExpenseMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.ExpenseValidator;
import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.domain.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the company attribution rule on {@link ExpenseService}:
 * the company is always taken from the session, and is left null when the
 * token carries no company claim.
 */
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseValidator expenseValidator;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private CompanyService companyService;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(
                expenseRepository, expenseValidator, expenseMapper,
                authenticatedUserProvider, companyService
        );
    }

    private CreateExpenseRequest request() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(100.0);
        request.setName("Papeleria");
        return request;
    }

    @Test
    void createExpenseUsesTheCompanyFromTheToken() {
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(expenseMapper.toEntity(any(), eq("user-1"), any(), eq(CompanyOid.of("company-1"))))
                .thenReturn(mock(Expense.class));
        when(expenseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(expenseMapper.toResponse(any())).thenReturn(new ExpenseResponse());

        expenseService.createExpense(request());

        verify(expenseMapper).toEntity(any(), eq("user-1"), any(), eq(CompanyOid.of("company-1")));
    }

    @Test
    void createExpenseRejectsARequestedCompanyThatDoesNotExist() {
        CreateExpenseRequest request = request();
        request.setCompanyOid("company-404");
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid("company-404")).thenThrow(
                new ResourceNotFoundException("Company is not available. Id: company-404")
        );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.createExpense(request)
        );

        assertTrue(exception.getMessage().contains("company-404"));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpenseUsesTheRequestedCompanyWhenTheTokenHasNone() {
        CreateExpenseRequest request = request();
        request.setCompanyOid("company-9");
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid("company-9")).thenReturn(CompanyOid.of("company-9"));
        when(expenseMapper.toEntity(any(), eq("user-1"), any(), eq(CompanyOid.of("company-9"))))
                .thenReturn(mock(Expense.class));
        when(expenseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(expenseMapper.toResponse(any())).thenReturn(new ExpenseResponse());

        expenseService.createExpense(request);

        verify(expenseMapper).toEntity(any(), eq("user-1"), any(), eq(CompanyOid.of("company-9")));
    }

    @Test
    void createExpenseIgnoresTheRequestedCompanyWhenTheTokenHasOne() {
        CreateExpenseRequest request = request();
        request.setCompanyOid("company-9");
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid("company-9")).thenReturn(CompanyOid.of("company-1"));
        when(expenseMapper.toEntity(any(), eq("user-1"), any(), eq(CompanyOid.of("company-1"))))
                .thenReturn(mock(Expense.class));
        when(expenseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(expenseMapper.toResponse(any())).thenReturn(new ExpenseResponse());

        expenseService.createExpense(request);

        verify(expenseMapper).toEntity(any(), eq("user-1"), any(), eq(CompanyOid.of("company-1")));
    }

    @Test
    void createExpenseLeavesTheCompanyNullWhenTheTokenCarriesNoCompany() {
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        ArgumentCaptor<CompanyOid> captor = ArgumentCaptor.forClass(CompanyOid.class);
        when(expenseMapper.toEntity(any(), eq("user-1"), any(), captor.capture()))
                .thenReturn(mock(Expense.class));
        when(expenseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(expenseMapper.toResponse(any())).thenReturn(new ExpenseResponse());

        expenseService.createExpense(request());

        assertNull(captor.getValue());
    }
}
