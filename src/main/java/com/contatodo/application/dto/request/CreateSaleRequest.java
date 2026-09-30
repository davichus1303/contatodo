package com.contatodo.application.dto.request;

/**
 * Request DTO for creating a sale.
 */
public class CreateSaleRequest {

    private String productOid;
    private Integer quantity;
    private Double totalSalePrice;
    private String notes;

    private String companyOid;

    public String getProductOid() {
        return productOid;
    }

    public void setProductOid(String productOid) {
        this.productOid = productOid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalSalePrice() {
        return totalSalePrice;
    }

    public void setTotalSalePrice(Double totalSalePrice) {
        this.totalSalePrice = totalSalePrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the company the record is being created for.
     *
     * <p>Optional. When omitted, the company is taken from the session token. When the
     * token carries no company, this value is used instead, and is left null when neither
     * is available.</p>
     *
     * @return Requested company identifier, or null.
     */
    public String getCompanyOid() {
        return companyOid;
    }

    /**
     * Sets the company the record is being created for.
     *
     * @param companyOid Requested company identifier.
     */
    public void setCompanyOid(String companyOid) {
        this.companyOid = companyOid;
    }
}
