package com.contatodo.application.dto.request;

/**
 * Request DTO for creating an acquisition.
 */
public class CreateAcquisitionRequest {

    private String acquisitionTypeOid;
    private String productName;
    private String description;
    private Integer quantity;
    private Double realCost;
    private Double unitPublicCost;
    private String supplierOid;
    private String supplierName;
    private String invoiceNumber;
    private String observations;

    public CreateAcquisitionRequest() {
    }

    public String getAcquisitionTypeOid() {
        return acquisitionTypeOid;
    }

    public void setAcquisitionTypeOid(String acquisitionTypeOid) {
        this.acquisitionTypeOid = acquisitionTypeOid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getRealCost() {
        return realCost;
    }

    public void setRealCost(Double realCost) {
        this.realCost = realCost;
    }

    public Double getUnitPublicCost() {
        return unitPublicCost;
    }

    public void setUnitPublicCost(Double unitPublicCost) {
        this.unitPublicCost = unitPublicCost;
    }

    public String getSupplierOid() {
        return supplierOid;
    }

    public void setSupplierOid(String supplierOid) {
        this.supplierOid = supplierOid;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
