package com.contatodo.domain.entities;

import java.time.LocalDateTime;

/**
 * Domain entity representing product cost history.
 */
public class ProductCostHistory {

    private String id;
    private String productOid;
    private String acquisitionOid;
    private Integer quantity;
    private Integer remainingQuantity;
    private Double realCost;
    private Double unitRealCost;
    private Double unitPublicCostAtPurchase;
    private LocalDateTime acquisitionDate;
    private String userOid;
    private LocalDateTime createdDate;

    /**
     * Creates an empty product cost history.
     */
    public ProductCostHistory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductOid() {
        return productOid;
    }

    public void setProductOid(String productOid) {
        this.productOid = productOid;
    }

    public String getAcquisitionOid() {
        return acquisitionOid;
    }

    public void setAcquisitionOid(String acquisitionOid) {
        this.acquisitionOid = acquisitionOid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public Double getRealCost() {
        return realCost;
    }

    public void setRealCost(Double realCost) {
        this.realCost = realCost;
    }

    public Double getUnitRealCost() {
        return unitRealCost;
    }

    public void setUnitRealCost(Double unitRealCost) {
        this.unitRealCost = unitRealCost;
    }

    public Double getUnitPublicCostAtPurchase() {
        return unitPublicCostAtPurchase;
    }

    public void setUnitPublicCostAtPurchase(Double unitPublicCostAtPurchase) {
        this.unitPublicCostAtPurchase = unitPublicCostAtPurchase;
    }

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDateTime acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
