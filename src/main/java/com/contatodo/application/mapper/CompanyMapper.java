package com.contatodo.application.mapper;

import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Mapper for company entities and DTOs.
 */
@Component
public class CompanyMapper implements ResponseMapper<Company, CompanyResponse> {

    /**
     * Maps a company entity to a response DTO without contact data.
     *
     * @param company Company entity.
     * @return Company response.
     */
    public CompanyResponse toResponse(Company company) {
        return toResponse(company, null);
    }

    /**
     * Maps a company entity to a response DTO including its resolved contact.
     *
     * @param company Company entity.
     * @param contact Resolved contact user, or null when the company has no contact or it could not be resolved.
     * @return Company response.
     */
    public CompanyResponse toResponse(Company company, User contact) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setRfc(company.getRfc());
        response.setWebSite(company.getWebSite());
        response.setUbication(company.getUbication());
        response.setContactUserOId(company.getContactUserOId());
        response.setIsActive(company.getIsActive());
        response.setIsDeleted(company.getIsDeleted());
        if (contact != null) {
            response.setContactName(contact.getName());
            response.setContactPhone(contact.getPhoneNumber());
        }
        return response;
    }

    /**
     * Maps a list of company entities to response DTOs resolving each contact from a map.
     *
     * <p>A company whose contact is missing from the map is still mapped, leaving
     * its contact data empty, so a single unresolved contact never drops the
     * company from the list.</p>
     *
     * @param companies Company entities.
     * @param contacts Map of contact user identifiers to resolved users.
     * @return Company responses.
     */
    public List<CompanyResponse> toResponseList(List<Company> companies, Map<String, User> contacts) {
        return companies.stream()
                .map(company -> toResponse(
                        company,
                        company.getContactUserOId() == null ? null : contacts.get(company.getContactUserOId())
                ))
                .toList();
    }
}
