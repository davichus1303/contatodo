package com.contatodo.application.dto.request;

/**
 * Request DTO for creating a sale.
 */
public class CreateSaleRequest {

    private String productOid;
    private Integer quantity;
    private String unitOfMeasureOid;
    private Boolean isPack;
    private Integer packSize;
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

    /**
     * Gets the unit of measure in which the sale is registered.
     *
     * @return Unit of measure identifier.
     */
    public String getUnitOfMeasureOid() {
        return unitOfMeasureOid;
    }

    /**
     * Sets the unit of measure in which the sale is registered.
     *
     * @param unitOfMeasureOid Unit of measure identifier.
     */
    public void setUnitOfMeasureOid(String unitOfMeasureOid) {
        this.unitOfMeasureOid = unitOfMeasureOid;
    }

    /**
     * Gets whether the selected unit of measure represents a pack.
     *
     * @return Pack flag, or null when omitted.
     */
    public Boolean getIsPack() {
        return isPack;
    }

    /**
     * Sets whether the selected unit of measure represents a pack.
     *
     * @param isPack Pack flag.
     */
    public void setIsPack(Boolean isPack) {
        this.isPack = isPack;
    }

    /**
     * Gets the number of base units contained in one sold pack.
     *
     * @return Pack size, or null when the product pack size must be used.
     */
    public Integer getPackSize() {
        return packSize;
    }

    /**
     * Sets the number of base units contained in one sold pack.
     *
     * @param packSize Pack size.
     */
    public void setPackSize(Integer packSize) {
        this.packSize = packSize;
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
