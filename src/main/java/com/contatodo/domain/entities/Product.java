package com.contatodo.domain.entities;

import java.time.LocalDateTime;

/**
 * Domain entity representing a product.
 */
public class Product {

    private String id;
    private String name;
    private String description;
    private Integer stock;
    private String code;
    private Double realCost;
    private Double unitRealCost;
    private Double unitPublicCost;
    private String urlPhoto;
    private Boolean isActive;
    private String userOid;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Creates an empty product.
     */
    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }
}
