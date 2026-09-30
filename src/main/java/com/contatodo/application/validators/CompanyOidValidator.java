package com.contatodo.application.validators;

import com.contatodo.domain.entities.Company;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.shared.constants.CompanyConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Validator for the company identifier a record is written against.
 */
@Component
public class CompanyOidValidator {

    private final CompanyRepository companyRepository;

    /**
     * Constructor.
     *
     * @param companyRepository Company repository port.
     */
    public CompanyOidValidator(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Resolves a company identifier to one that a record can be written against.
     *
     * <p>Three conditions make a company unusable for a new record: it does not
     * exist, it is inactive, or it has been soft-deleted.
     * {@link CompanyRepository#findById(String)} applies none of them and happily
     * returns a deleted company, so all three are checked here.</p>
     *
     * <p>A single message covers all three cases, so a caller cannot tell an id
     * that does not exist from one pointing at a deleted company.</p>
     *
     * @param companyOid Company identifier, possibly {@code null} or blank.
     * @return The identifier when it points at a usable company, {@code null} when
     *         no company was supplied.
     * @throws ResourceNotFoundException when the company is missing, inactive or deleted.
     */
    public String validate(String companyOid) {
        if (companyOid == null || companyOid.isBlank()) {
            return null;
        }
        Company company = companyRepository.findById(companyOid)
                .filter(CompanyOidValidator::isUsable)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CompanyConstants.COMPANY_NOT_AVAILABLE + " Id: " + companyOid
                ));
        return company.getId();
    }

    /**
     * Tells whether a company may receive new records.
     *
     * <p>Both flags are nullable {@link Boolean}s on documents written before
     * they were introduced, so both are compared against a known value rather
     * than unboxed.</p>
     *
     * @param company Company to inspect.
     * @return {@code true} when the company is active and not deleted.
     */
    private static boolean isUsable(Company company) {
        return Boolean.TRUE.equals(company.getIsActive())
                && !Boolean.TRUE.equals(company.getIsDeleted());
    }
}
