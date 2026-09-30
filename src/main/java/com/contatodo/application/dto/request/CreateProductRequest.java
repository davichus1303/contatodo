package com.contatodo.application.dto.request;


/**
 * Request DTO for creating a product.
 */
public class CreateProductRequest {

    private String name;
    private String description;
    private Integer stock;
    private Double realCost;
    private Double unitRealCost;
    private Double unitPublicCost;
    private String urlPhoto;
    private Boolean isActive;

    private String companyOid;

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
