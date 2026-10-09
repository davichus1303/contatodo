package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.UnitOfMeasureDocument;
import com.contatodo.domain.entities.UnitOfMeasure;
import org.springframework.stereotype.Component;

/**
 * Mapper between {@link UnitOfMeasure} domain entities and MongoDB unit of
 * measure documents.
 */
@Component
public class UnitOfMeasurePersistenceMapper implements PersistenceMapper<UnitOfMeasureDocument, UnitOfMeasure> {

    /**
     * Maps a unit of measure entity to a document.
     *
     * @param unitOfMeasure Unit of measure entity.
     * @return Unit of measure document.
     */
    public UnitOfMeasureDocument toDocument(UnitOfMeasure unitOfMeasure) {
        UnitOfMeasureDocument document = new UnitOfMeasureDocument();
        document.setId(unitOfMeasure.getId());
        document.setName(unitOfMeasure.getName());
        document.setAbrev(unitOfMeasure.getAbrev());
        document.setCompanyOid(unitOfMeasure.getCompanyOid());
        document.setIsActive(unitOfMeasure.getIsActive());
        document.setIsDeleted(unitOfMeasure.getIsDeleted());
        return document;
    }

    /**
     * Maps a unit of measure document to an entity.
     *
     * @param document Unit of measure document.
     * @return Unit of measure entity.
     */
    public UnitOfMeasure toEntity(UnitOfMeasureDocument document) {
        return UnitOfMeasure.builder()
                .id(document.getId())
                .name(document.getName())
                .abrev(document.getAbrev())
                .companyOid(document.getCompanyOid())
                .isActive(document.getIsActive())
                .isDeleted(document.getIsDeleted())
                .build();
    }
}
