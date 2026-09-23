package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateCompanyRequest;
import com.contatodo.application.dto.request.UpdateCompanyRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Mapper for company entities and DTOs.
 */
@Component
public class CompanyMapper implements ResponseMapper<Company, CompanyResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create company request.
     * @param createdBy Identifier of the user that creates it.
     * @return Company entity.
     */
    public Company toEntity(CreateCompanyRequest request, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return Company.builder()
                .name(request.getName())
                .rfc(request.getRfc())
                .webSite(request.getWebSite())
                .ubication(request.getUbication())
                .contactUserOId(request.getContactUserOId())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isDeleted(false)
                .createdDate(now)
                .updatedDate(now)
                .createdBy(createdBy)
                .build();
    }

    /**
     * Maps a list of create requests to domain entities.
     *
     * @param requests Create company requests.
     * @param createdBy Identifier of the user that creates them.
     * @return Company entities.
     */
    public List<Company> toEntityList(List<CreateCompanyRequest> requests, String createdBy) {
        return requests.stream()
                .map(request -> toEntity(request, createdBy))
                .toList();
    }

    /**
     * Produces an updated copy of an existing company applying request changes.
     *
     * <p>Only the fields present in the request are applied; a null field keeps
     * the current value. The identifier, the logical delete flag, the creation
     * date and the creator are never changed.</p>
     *
     * @param existing Current persisted company.
     * @param request Update company request.
     * @return New immutable company instance with the changes applied.
     */
    public Company applyUpdate(Company existing, UpdateCompanyRequest request) {
        return Company.builder()
                .id(existing.getId())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .rfc(request.getRfc() != null ? request.getRfc() : existing.getRfc())
                .webSite(request.getWebSite() != null ? request.getWebSite() : existing.getWebSite())
                .ubication(request.getUbication() != null ? request.getUbication() : existing.getUbication())
                .contactUserOId(request.getContactUserOId() != null ? request.getContactUserOId() : existing.getContactUserOId())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive())
                .isDeleted(existing.getIsDeleted())
                .createdDate(existing.getCreatedDate())
                .updatedDate(LocalDateTime.now())
                .createdBy(existing.getCreatedBy())
                .build();
    }

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
