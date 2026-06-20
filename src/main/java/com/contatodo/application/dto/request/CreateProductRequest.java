package com.contatodo.application.dto.request;

import java.math.BigDecimal;

/**
 * Request DTO for creating a product.
 */
public class CreateProductRequest {

    private String name;
    private String description;
    private Integer stock;
    private BigDecimal realCost;
    private String urlPhoto;
    private BigDecimal publicCost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getRealCost() {
        return realCost;
    }

    public void setRealCost(BigDecimal realCost) {
        this.realCost = realCost;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public BigDecimal getPublicCost() {
        return publicCost;
    }

    public void setPublicCost(BigDecimal publicCost) {
        this.publicCost = publicCost;
    }
}
