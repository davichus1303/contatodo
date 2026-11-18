package com.contatodo.application.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for a deal.
 */
public class DealResponse {

    private String id;
    private String productOid;
    private String unitOfMeasureOid;
    private Double quantity;
    private Double price;
    private String companyOid;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private Boolean isActive;
    private Boolean isDelete;

    public DealResponse() {
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

    public String getUnitOfMeasureOid() {
        return unitOfMeasureOid;
    }

    public void setUnitOfMeasureOid(String unitOfMeasureOid) {
        this.unitOfMeasureOid = unitOfMeasureOid;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(String companyOid) {
        this.companyOid = companyOid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}
