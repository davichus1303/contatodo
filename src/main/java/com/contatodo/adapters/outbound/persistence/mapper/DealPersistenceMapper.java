package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.DealDocument;
import com.contatodo.domain.entities.Deal;
import org.springframework.stereotype.Component;

/**
 * Mapper between {@link Deal} domain entities and MongoDB deal documents.
 */
@Component
public class DealPersistenceMapper implements PersistenceMapper<DealDocument, Deal> {

    @Override
    public DealDocument toDocument(Deal deal) {
        DealDocument document = new DealDocument();
        document.setId(deal.getId());
        document.setProductOid(deal.getProductOid());
        document.setUnitOfMeasureOid(deal.getUnitOfMeasureOid());
        document.setQuantity(deal.getQuantity());
        document.setPrice(deal.getPrice());
        document.setCompanyOid(deal.getCompanyOid());
        document.setCreatedDate(deal.getCreatedDate());
        document.setUpdateDate(deal.getUpdateDate());
        document.setIsActive(deal.getIsActive());
        document.setIsDelete(deal.getIsDelete());
        return document;
    }

    @Override
    public Deal toEntity(DealDocument document) {
        return Deal.builder()
                .id(document.getId())
                .productOid(document.getProductOid())
                .unitOfMeasureOid(document.getUnitOfMeasureOid())
                .quantity(document.getQuantity())
                .price(document.getPrice())
                .companyOid(document.getCompanyOid())
                .createdDate(document.getCreatedDate())
                .updateDate(document.getUpdateDate())
                .isActive(document.getIsActive())
                .isDelete(document.getIsDelete())
                .build();
    }
}
