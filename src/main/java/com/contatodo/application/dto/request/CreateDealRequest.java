package com.contatodo.application.dto.request;

/**
 * Request DTO for creating a deal.
 */
public class CreateDealRequest {

    private String companyOid;
    private String productOid;
    private String unitOfMeasureOid;
    private Double price;
    private Double quantity;

    public CreateDealRequest() {
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(String companyOid) {
        this.companyOid = companyOid;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
