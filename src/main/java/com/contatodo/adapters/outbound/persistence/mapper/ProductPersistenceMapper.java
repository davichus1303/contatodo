package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.ProductDocument;
import com.contatodo.domain.entities.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link Product} domain entities and MongoDB product documents.
 */
@Component
public class ProductPersistenceMapper {

    /**
     * Maps a product entity to a product document.
     *
     * @param product Product entity.
     * @return Product document.
     */
    public ProductDocument toDocument(Product product) {
        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setStock(product.getStock());
        document.setCode(product.getCode());
        document.setRealCost(product.getRealCost());
        document.setUnitRealCost(product.getUnitRealCost());
        document.setUnitPublicCost(product.getUnitPublicCost());
        document.setUrlPhoto(product.getUrlPhoto());
        document.setIsActive(product.getIsActive());
        document.setUserOid(product.getUserOid());
        document.setCreatedDate(product.getCreatedDate());
        document.setUpdatedDate(product.getUpdatedDate());
        return document;
    }

    /**
     * Maps a product document to a product entity.
     *
     * @param document Product document.
     * @return Product entity.
     */
    public Product toEntity(ProductDocument document) {
        return Product.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .stock(document.getStock())
                .code(document.getCode())
                .realCost(document.getRealCost())
                .unitRealCost(document.getUnitRealCost())
                .unitPublicCost(document.getUnitPublicCost())
                .urlPhoto(document.getUrlPhoto())
                .isActive(document.getIsActive())
                .userOid(document.getUserOid())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .build();
    }

    /**
     * Maps a list of product documents to product entities.
     *
     * @param documents Product documents.
     * @return Product entities.
     */
    public List<Product> toEntityList(List<ProductDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
