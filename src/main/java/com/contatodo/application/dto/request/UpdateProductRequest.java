package com.contatodo.application.dto.request;


/**
 * Request DTO for updating a product.
 */
public class UpdateProductRequest {

    private String name;
    private String description;
    private Integer stock;
    private Double realCost;
    private Double unitRealCost;
    private Double unitPublicCost;
    private String urlPhoto;
    private Boolean isActive;

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

    public Double getRealCost() {
        return realCost;
    }

    public void setRealCost(Double realCost) {
        this.realCost = realCost;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public Double getUnitRealCost() {
        return unitRealCost;
    }

    public void setUnitRealCost(Double unitRealCost) {
        this.unitRealCost = unitRealCost;
    }

    public Double getUnitPublicCost() {
        return unitPublicCost;
    }

    public void setUnitPublicCost(Double unitPublicCost) {
        this.unitPublicCost = unitPublicCost;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
