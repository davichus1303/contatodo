package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.request.TotalExpensesRequest;
import com.contatodo.application.dto.response.ExpenseResponse;
import com.contatodo.application.dto.response.TotalExpensesResponse;
import com.contatodo.application.mapper.ExpenseMapper;
import com.contatodo.application.validators.ExpenseValidator;
import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.ExpenseRepository;
import com.contatodo.shared.constants.CompanyConstants;
import com.contatodo.shared.constants.ExpenseConstants;
import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.domain.model.Money;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.shared.exceptions.InvalidDateRangeException;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.domain.model.CompanyOid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service containing expense business logic.
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseValidator expenseValidator;
    private final ExpenseMapper expenseMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CompanyContextProvider companyContextProvider;
    private final CompanyRepository companyRepository;

    /**
     * Creates an expense service.
     *
     * @param expenseRepository Expense repository port.
     * @param expenseValidator Expense validator.
     * @param expenseMapper Expense mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyContextProvider Company context provider.
     */
    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseValidator expenseValidator,
            ExpenseMapper expenseMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyContextProvider companyContextProvider,
            CompanyRepository companyRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseValidator = expenseValidator;
        this.expenseMapper = expenseMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyContextProvider = companyContextProvider;
        this.companyRepository = companyRepository;
    }

    /**
     * Creates a new expense.
     *
     * @param request Create expense request.
     * @return Created expense response.
     */
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        expenseValidator.validateCreateRequest(request);

        String userOid = authenticatedUserProvider.getCurrentUserOid();
        LocalDateTime expenseDate = resolveExpenseDate(request.getExpenseDate());

        Expense savedExpense = expenseRepository.save(
                expenseMapper.toEntity(request, userOid, expenseDate, resolveCompanyOid(request.getCompanyOid())));
        return expenseMapper.toResponse(savedExpense);
    }

    /**
     * Resolves the owning company for a write.
     *
     * <p>The company of the session always wins, so a caller can never move a record
     * out of the company its own token points at. Only when the token carries no
     * company, which is the case for the root user and for any session without a
     * company, the optional value supplied in the request is used. The identifier is
     * left {@code null} when neither is available, instead of being rejected.</p>
     *
     * <p>The value supplied in the request is validated against the stored companies
     * before it is used, so a record can never be attached to a company that does
     * not exist. The value is only validated when it is actually the one applied,
     * which is when the session carries no company.</p>
     *
     * @param requestedCompanyOid Optional company identifier supplied in the request.
     * @return Company identifier, or {@code null} when neither source provides one.
     * @throws ResourceNotFoundException when the requested company does not exist.
     */
    private CompanyOid resolveCompanyOid(String requestedCompanyOid) {
        Optional<CompanyOid> sessionCompany = companyContextProvider.currentCompanyOid();
        if (sessionCompany.isPresent()) {
            return sessionCompany.get();
        }
        if (requestedCompanyOid != null && !requestedCompanyOid.isBlank()) {
            CompanyOid requested = CompanyOid.of(requestedCompanyOid);
            if (!companyRepository.findById(requested.value()).isPresent()) {
                throw new ResourceNotFoundException(
                        CompanyConstants.COMPANY_NOT_FOUND + " Id: " + requested.value()
                );
            }
            return requested;
        }
        return null;
    }

    /**
     * Resolves the effective expense date, defaulting to now when absent.
     *
     * @param rawExpenseDate Raw ISO-8601 date text; may be null or empty.
     * @return Parsed expense date or the current timestamp.
     */
    private LocalDateTime resolveExpenseDate(String rawExpenseDate) {
        if (rawExpenseDate == null || rawExpenseDate.isEmpty()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(rawExpenseDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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

    /**
     * Calculates the total expenses within a specified date range.
     *
     * @param request Total expenses request with start and end dates.
     * @return Total expenses response with the calculated sum.
     * @throws InvalidDateRangeException If the date range is invalid (startDate > endDate).
     */
    public TotalExpensesResponse getTotalExpensesByDateRange(TotalExpensesRequest request) {
        // Normalize dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ExpenseConstants.DATE_TIME_FORMAT);
        
        LocalDateTime startDate = LocalDateTime.parse(
            request.getStartDate() + " " + ExpenseConstants.START_OF_DAY,
            formatter
        );
        
        LocalDateTime endDate = LocalDateTime.parse(
            request.getEndDate() + " " + ExpenseConstants.END_OF_DAY,
            formatter
        );

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(ValidationConstants.FIELD_INVALID_RANGE);
        }

        // Get expenses within date range
        List<Expense> expenses = expenseRepository.findActiveAndNotDeletedByDateRange(startDate, endDate);

        Money total = expenses.stream()
            .map(expense -> Money.of(expense.getAmount()))
            .reduce(Money.zero(), Money::add);

        return new TotalExpensesResponse(total.toDouble());
    }
}
