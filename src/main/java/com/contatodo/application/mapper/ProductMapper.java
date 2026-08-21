package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.domain.entities.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for product entities and DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create product request.
     * @param code Generated product code.
     * @param userOid Owning user identifier.
     * @return Product entity.
     */
    public Product toEntity(CreateProductRequest request, String code, String userOid) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .stock(request.getStock())
                .code(code)
                .realCost(request.getRealCost())
                .unitRealCost(request.getUnitRealCost())
                .unitPublicCost(request.getUnitPublicCost())
                .urlPhoto(request.getUrlPhoto())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .userOid(userOid)
                .createdDate(now)
                .updatedDate(now)
                .build();
    }

    /**
     * Applies update request changes to an existing product.
     *
     * @param product Existing product.
     * @param request Update product request.
     */
    public Product applyUpdate(Product existing, UpdateProductRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .id(existing.getId())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .description(request.getDescription() != null ? request.getDescription() : existing.getDescription())
                .stock(request.getStock() != null ? request.getStock() : existing.getStock())
                .code(existing.getCode())
                .realCost(request.getRealCost() != null ? request.getRealCost() : existing.getRealCost())
                .unitRealCost(request.getUnitRealCost() != null ? request.getUnitRealCost() : existing.getUnitRealCost())
                .unitPublicCost(request.getUnitPublicCost() != null ? request.getUnitPublicCost() : existing.getUnitPublicCost())
                .urlPhoto(request.getUrlPhoto() != null ? request.getUrlPhoto() : existing.getUrlPhoto())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive())
                .userOid(existing.getUserOid())
                .createdDate(existing.getCreatedDate())
                .updatedDate(now)
                .build();
    }

    /**
     * Maps a product entity to a response DTO.
     *
     * @param product Product entity.
     * @return Product response.
     */
    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setStock(product.getStock());
        response.setCode(product.getCode());
        response.setRealCost(product.getRealCost());
        response.setUnitRealCost(product.getUnitRealCost());
        response.setUnitPublicCost(product.getUnitPublicCost());
        response.setUrlPhoto(product.getUrlPhoto());
        response.setIsActive(product.getIsActive());
        response.setCreatedDate(product.getCreatedDate());
        response.setUpdatedDate(product.getUpdatedDate());
        return response;
    }

    /**
     * Maps a list of products to response DTOs.
     *
     * @param products Product entities.
     * @return Product responses.
     */
    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream().map(this::toResponse).toList();
    }
}
