package com.contatodo.application.services;

import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.mapper.CompanyMapper;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.UserRepository;
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

    /**
     * Creates a company service.
     *
     * @param companyRepository Company repository port.
     * @param userRepository User repository port used to resolve the contact data.
     * @param companyMapper Company mapper.
     */
    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            CompanyMapper companyMapper
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.companyMapper = companyMapper;
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
            try {
                userRepository.findById(contactUserOId)
                        .ifPresent(user -> contacts.put(contactUserOId, user));
            } catch (RuntimeException exception) {
                // A single broken contact must not prevent the remaining companies from loading.
            }
        }
        return contacts;
    }
}
