package com.contatodo.domain.entities;

import java.time.LocalDateTime;

/**
 * Domain entity representing an acquisition.
 */
public class Acquisition {

    private String id;
    private String acquisitionTypeOid;
    private String productOid;
    private Integer quantity;
    private Double realCost;
    private Double unitRealCost;
    private Double unitPublicCost;
    private String supplierOid;
    private String supplierName;
    private String invoiceNumber;
    private LocalDateTime acquisitionDate;
    private String observations;
    private String userOid;
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Creates an empty acquisition.
     */
    public Acquisition() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcquisitionTypeOid() {
        return acquisitionTypeOid;
    }

    public void setAcquisitionTypeOid(String acquisitionTypeOid) {
        this.acquisitionTypeOid = acquisitionTypeOid;
    }

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

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDateTime acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
}
