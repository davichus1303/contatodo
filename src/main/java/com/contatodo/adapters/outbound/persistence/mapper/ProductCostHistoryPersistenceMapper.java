package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.ProductCostHistoryDocument;
import com.contatodo.domain.entities.ProductCostHistory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link ProductCostHistory} domain entities and MongoDB
 * product cost history documents.
 */
@Component
public class ProductCostHistoryPersistenceMapper {

    /**
     * Maps a product cost history entity to a document.
     *
     * @param productCostHistory History entity.
     * @return History document.
     */
    public ProductCostHistoryDocument toDocument(ProductCostHistory productCostHistory) {
        ProductCostHistoryDocument document = new ProductCostHistoryDocument();
        document.setId(productCostHistory.getId());
        document.setProductOid(productCostHistory.getProductOid());
        document.setAcquisitionOid(productCostHistory.getAcquisitionOid());
        document.setQuantity(productCostHistory.getQuantity());
        document.setRemainingQuantity(productCostHistory.getRemainingQuantity());
        document.setRealCost(productCostHistory.getRealCost());
        document.setUnitRealCost(productCostHistory.getUnitRealCost());
        document.setUnitPublicCostAtPurchase(productCostHistory.getUnitPublicCostAtPurchase());
        document.setAcquisitionDate(productCostHistory.getAcquisitionDate());
        document.setUserOid(productCostHistory.getUserOid());
        document.setCreatedDate(productCostHistory.getCreatedDate());
        return document;
    }

    /**
     * Maps a product cost history document to an entity.
     *
     * @param document History document.
     * @return History entity.
     */
    public ProductCostHistory toEntity(ProductCostHistoryDocument document) {
        return ProductCostHistory.builder()
                .id(document.getId())
                .productOid(document.getProductOid())
                .acquisitionOid(document.getAcquisitionOid())
                .quantity(document.getQuantity())
                .remainingQuantity(document.getRemainingQuantity())
                .realCost(document.getRealCost())
                .unitRealCost(document.getUnitRealCost())
                .unitPublicCostAtPurchase(document.getUnitPublicCostAtPurchase())
                .acquisitionDate(document.getAcquisitionDate())
                .userOid(document.getUserOid())
                .createdDate(document.getCreatedDate())
                .build();
    }

    /**
     * Maps a list of history documents to entities.
     *
     * @param documents History documents.
     * @return History entities.
     */
    public List<ProductCostHistory> toEntityList(List<ProductCostHistoryDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
