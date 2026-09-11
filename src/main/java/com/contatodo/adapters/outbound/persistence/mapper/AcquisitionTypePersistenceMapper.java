package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.AcquisitionTypeDocument;
import com.contatodo.domain.entities.AcquisitionType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link AcquisitionType} domain entities and MongoDB
 * acquisition type documents.
 */
@Component
public class AcquisitionTypePersistenceMapper {

    /**
     * Maps an acquisition type entity to a document.
     *
     * @param acquisitionType Acquisition type entity.
     * @return Acquisition type document.
     */
    public AcquisitionTypeDocument toDocument(AcquisitionType acquisitionType) {
        AcquisitionTypeDocument document = new AcquisitionTypeDocument();
        document.setId(acquisitionType.getId());
        document.setName(acquisitionType.getName());
        document.setDescription(acquisitionType.getDescription());
        document.setUserOid(acquisitionType.getUserOid());
        document.setIsActive(acquisitionType.getIsActive());
        document.setIsDeleted(acquisitionType.getIsDeleted());
        document.setAffectsInventory(acquisitionType.getAffectsInventory());
        document.setCreatedDate(acquisitionType.getCreatedDate());
        document.setUpdatedDate(acquisitionType.getUpdatedDate());
        return document;
    }

    /**
     * Maps an acquisition type document to an entity.
     *
     * @param document Acquisition type document.
     * @return Acquisition type entity.
     */
    public AcquisitionType toEntity(AcquisitionTypeDocument document) {
        return AcquisitionType.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .userOid(document.getUserOid())
                .isActive(document.getIsActive())
                .isDeleted(document.getIsDeleted())
                .affectsInventory(document.getAffectsInventory())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .build();
    }

    /**
     * Maps a list of acquisition type documents to entities.
     *
     * @param documents Acquisition type documents.
     * @return Acquisition type entities.
     */
    public List<AcquisitionType> toEntityList(List<AcquisitionTypeDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
