package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateCompaniesRequest;
import com.contatodo.application.dto.request.UpdateCompanyRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.mapper.CompanyMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.CompanyValidator;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.CompanyConstants;
import com.contatodo.shared.exceptions.CompanyNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service containing company business logic.
 */
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final CompanyValidator companyValidator;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    /**
     * Creates a company service.
     *
     * @param companyRepository Company repository port.
     * @param userRepository User repository port used to resolve the contact data.
     * @param companyMapper Company mapper.
     * @param companyValidator Company validator.
     * @param authenticatedUserProvider Authenticated user provider.
     */
    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            CompanyMapper companyMapper,
            CompanyValidator companyValidator,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.companyMapper = companyMapper;
        this.companyValidator = companyValidator;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Creates one or more companies in a single call.
     *
     * <p>The whole batch is validated before anything is persisted, and the
     * authenticated user is recorded as the creator of every company.</p>
     *
     * @param request Create companies request.
     * @return Created company responses with their resolved contact data.
     */
    public List<CompanyResponse> createCompanies(CreateCompaniesRequest request) {
        companyValidator.validateCreateRequest(request);

        String createdBy = authenticatedUserProvider.getCurrentUserOid();
        List<Company> companies = companyMapper.toEntityList(request.getCompanies(), createdBy);
        List<Company> savedCompanies = companyRepository.saveAll(companies);
        return companyMapper.toResponseList(savedCompanies, resolveContacts(savedCompanies));
    }

    /**
     * Updates a single existing company.
     *
     * <p>The original company is loaded from the database; when it does not
     * exist (or is logically deleted) the process stops with a not found error.
     * Only the fields present in the request are changed.</p>
     *
     * @param id Company identifier.
     * @param request Update company request.
     * @return Updated company response with its resolved contact data.
     */
    public CompanyResponse updateCompany(String id, UpdateCompanyRequest request) {
        companyValidator.validateUpdateRequest(request);

        Company company = companyRepository.findById(id)
                .filter(existingCompany -> !Boolean.TRUE.equals(existingCompany.getIsDeleted()))
                .orElseThrow(() -> new CompanyNotFoundException(CompanyConstants.COMPANY_NOT_FOUND));

        Company updatedCompany = companyRepository.save(companyMapper.applyUpdate(company, request));
        return companyMapper.toResponse(updatedCompany, resolveContact(updatedCompany.getContactUserOId()));
    }

    /**
     * Performs the logical deletion of a single company.
     *
     * <p>The company is loaded and, when found, persisted again as an updated
     * copy with its delete flag set to true and its active flag set to false.</p>
     *
     * @param id Company identifier.
     */
    public void deleteCompany(String id) {
        Company company = companyRepository.findById(id)
                .filter(existingCompany -> !Boolean.TRUE.equals(existingCompany.getIsDeleted()))
                .orElseThrow(() -> new CompanyNotFoundException(CompanyConstants.COMPANY_NOT_FOUND));

        companyRepository.save(company.markDeleted());
    }

    /**
     * Retrieves all non-deleted companies, resolving each related contact user.
     *
     * <p>Each contact is looked up at most once. A contact that cannot be
     * resolved (missing identifier, not found or lookup error) is left empty
     * for that company without failing the whole query.</p>
     *
     * @return List of company responses.
     */
    public List<CompanyResponse> getCompanies() {
        List<Company> companies = companyRepository.findAllNotDeleted();
        return companyMapper.toResponseList(companies, resolveContacts(companies));
    }

    /**
     * Resolves the contact users referenced by the given companies, keyed by user identifier.
     *
     * @param companies Companies whose contacts must be resolved.
     * @return Map of user identifiers to resolved users.
     */
    private Map<String, User> resolveContacts(List<Company> companies) {
        Map<String, User> contacts = new HashMap<>();
        for (Company company : companies) {
            String contactUserOId = company.getContactUserOId();
            if (contactUserOId == null || contacts.containsKey(contactUserOId)) {
                continue;
            }
            User contact = resolveContact(contactUserOId);
            if (contact != null) {
                contacts.put(contactUserOId, contact);
            }
        }
        return contacts;
    }

    /**
     * Looks up a single contact user, tolerating a missing or failing lookup.
     *
     * @param contactUserOId Contact user identifier.
     * @return Resolved user, or null when it cannot be resolved.
     */
    private User resolveContact(String contactUserOId) {
        if (contactUserOId == null) {
            return null;
        }
        try {
            return userRepository.findById(contactUserOId).orElse(null);
        } catch (RuntimeException exception) {
            // A broken contact must never fail the company operation.
            return null;
        }
    }
}
