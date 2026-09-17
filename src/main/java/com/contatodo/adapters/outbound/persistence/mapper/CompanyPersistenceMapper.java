package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.CompanyDocument;
import com.contatodo.domain.entities.Company;
import org.springframework.stereotype.Component;

/**
 * Mapper between {@link Company} domain entities and MongoDB company documents.
 */
@Component
public class CompanyPersistenceMapper implements PersistenceMapper<CompanyDocument, Company> {

    /**
     * Maps a company entity to a document.
     *
     * @param company Company entity.
     * @return Company document.
     */
    public CompanyDocument toDocument(Company company) {
        CompanyDocument document = new CompanyDocument();
        document.setId(company.getId());
        document.setName(company.getName());
        document.setRfc(company.getRfc());
        document.setWebSite(company.getWebSite());
        document.setUbication(company.getUbication());
        document.setContactUserOId(company.getContactUserOId());
        document.setIsActive(company.getIsActive());
        document.setIsDeleted(company.getIsDeleted());
        document.setCreatedDate(company.getCreatedDate());
        document.setUpdatedDate(company.getUpdatedDate());
        document.setCreatedBy(company.getCreatedBy());
        return document;
    }

    /**
     * Maps a company document to an entity.
     *
     * @param document Company document.
     * @return Company entity.
     */
    public Company toEntity(CompanyDocument document) {
        return Company.builder()
                .id(document.getId())
                .name(document.getName())
                .rfc(document.getRfc())
                .webSite(document.getWebSite())
                .ubication(document.getUbication())
                .contactUserOId(document.getContactUserOId())
                .isActive(document.getIsActive())
                .isDeleted(document.getIsDeleted())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .createdBy(document.getCreatedBy())
                .build();
    }
}
