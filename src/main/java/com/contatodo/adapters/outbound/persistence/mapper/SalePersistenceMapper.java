package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.SaleDocument;
import com.contatodo.domain.entities.Sale;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link Sale} domain entities and MongoDB sale documents.
 */
@Component
public class SalePersistenceMapper {

    /**
     * Maps a sale entity to a sale document.
     *
     * @param sale Sale entity.
     * @return Sale document.
     */
    public SaleDocument toDocument(Sale sale) {
        SaleDocument document = new SaleDocument();
        document.setId(sale.getId());
        document.setSaleNumber(sale.getSaleNumber());
        document.setProductOid(sale.getProductOid());
        document.setProductName(sale.getProductName());
        document.setUserOid(sale.getUserOid());
        document.setQuantity(sale.getQuantity());
        document.setTotalCost(sale.getTotalCost());
        document.setOriginalTotalPrice(sale.getOriginalTotalPrice());
        document.setTotalSalePrice(sale.getTotalSalePrice());
        document.setSaleDate(sale.getSaleDate());
        document.setNotes(sale.getNotes());
        document.setCreatedDate(sale.getCreatedDate());
        document.setUpdatedDate(sale.getUpdatedDate());
        document.setIsDeleted(sale.getIsDeleted());
        return document;
    }

    /**
     * Maps a sale document to a sale entity.
     *
     * @param document Sale document.
     * @return Sale entity.
     */
    public Sale toEntity(SaleDocument document) {
        return Sale.builder()
                .id(document.getId())
                .saleNumber(document.getSaleNumber())
                .productOid(document.getProductOid())
                .productName(document.getProductName())
                .userOid(document.getUserOid())
                .quantity(document.getQuantity())
                .totalCost(document.getTotalCost())
                .originalTotalPrice(document.getOriginalTotalPrice())
                .totalSalePrice(document.getTotalSalePrice())
                .saleDate(document.getSaleDate())
                .notes(document.getNotes())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .isDeleted(document.getIsDeleted())
                .build();
    }

    /**
     * Maps a list of sale documents to sale entities.
     *
     * @param documents Sale documents.
     * @return Sale entities.
     */
    public List<Sale> toEntityList(List<SaleDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
