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
     * @return Product entity.
     */
    public Product toEntity(CreateProductRequest request, String code) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());
        product.setCode(code);
        product.setRealCost(request.getRealCost());
        product.setUrlPhoto(request.getUrlPhoto());
        product.setPublicCost(request.getPublicCost());
        product.setCreatedDate(LocalDateTime.now());
        product.setUpdatedDate(LocalDateTime.now());
        return product;
    }

    /**
     * Applies update request changes to an existing product.
     *
     * @param product Existing product.
     * @param request Update product request.
     */
    public void applyUpdate(Product product, UpdateProductRequest request) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getRealCost() != null) {
            product.setRealCost(request.getRealCost());
        }
        if (request.getUrlPhoto() != null) {
            product.setUrlPhoto(request.getUrlPhoto());
        }
        if (request.getPublicCost() != null) {
            product.setPublicCost(request.getPublicCost());
        }
        product.setUpdatedDate(LocalDateTime.now());
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
        response.setUrlPhoto(product.getUrlPhoto());
        response.setPublicCost(product.getPublicCost());
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
