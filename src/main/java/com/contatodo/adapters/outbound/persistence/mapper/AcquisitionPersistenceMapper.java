package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.AcquisitionDocument;
import com.contatodo.domain.entities.Acquisition;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link Acquisition} domain entities and MongoDB acquisition
 * documents.
 */
@Component
public class AcquisitionPersistenceMapper {

    /**
     * Maps an acquisition entity to a document.
     *
     * @param acquisition Acquisition entity.
     * @return Acquisition document.
     */
    public AcquisitionDocument toDocument(Acquisition acquisition) {
        AcquisitionDocument document = new AcquisitionDocument();
        document.setId(acquisition.getId());
        document.setAcquisitionTypeOid(acquisition.getAcquisitionTypeOid());
        document.setProductOid(acquisition.getProductOid());
        document.setProductName(acquisition.getProductName());
        document.setQuantity(acquisition.getQuantity());
        document.setRealCost(acquisition.getRealCost());
        document.setUnitRealCost(acquisition.getUnitRealCost());
        document.setUnitPublicCost(acquisition.getUnitPublicCost());
        document.setSupplierOid(acquisition.getSupplierOid());
        document.setSupplierName(acquisition.getSupplierName());
        document.setInvoiceNumber(acquisition.getInvoiceNumber());
        document.setAcquisitionDate(acquisition.getAcquisitionDate());
        document.setObservations(acquisition.getObservations());
        document.setUserOid(acquisition.getUserOid());
        document.setIsDeleted(acquisition.getIsDeleted());
        document.setCreatedDate(acquisition.getCreatedDate());
        document.setUpdatedDate(acquisition.getUpdatedDate());
        return document;
    }

    /**
     * Maps an acquisition document to an entity.
     *
     * @param document Acquisition document.
     * @return Acquisition entity.
     */
    public Acquisition toEntity(AcquisitionDocument document) {
        return Acquisition.builder()
                .id(document.getId())
                .acquisitionTypeOid(document.getAcquisitionTypeOid())
                .productOid(document.getProductOid())
                .productName(document.getProductName())
                .quantity(document.getQuantity())
                .realCost(document.getRealCost())
                .unitRealCost(document.getUnitRealCost())
                .unitPublicCost(document.getUnitPublicCost())
                .supplierOid(document.getSupplierOid())
                .supplierName(document.getSupplierName())
                .invoiceNumber(document.getInvoiceNumber())
                .acquisitionDate(document.getAcquisitionDate())
                .observations(document.getObservations())
                .userOid(document.getUserOid())
                .isDeleted(document.getIsDeleted())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .build();
    }

    /**
     * Maps a list of acquisition documents to entities.
     *
     * @param documents Acquisition documents.
     * @return Acquisition entities.
     */
    public List<Acquisition> toEntityList(List<AcquisitionDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
