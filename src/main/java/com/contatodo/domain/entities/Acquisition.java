package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;

/**
 * Domain entity representing an acquisition of goods.
 *
 * <p>Immutable; construction is only possible through {@link Builder#build()}.</p>
 */
public final class Acquisition {

    private final String id;
    private final String acquisitionTypeOid;
    private final String productOid;
    private final String productName;
    private final Integer quantity;
    private final Double realCost;
    private final Double unitRealCost;
    private final Double unitPublicCost;
    private final String supplierOid;
    private final String supplierName;
    private final String invoiceNumber;
    private final LocalDateTime acquisitionDate;
    private final String observations;
    private final String userOid;
    private final Boolean isDeleted;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;

    /**
     * Creates an acquisition from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Acquisition(Builder builder) {
        this.id = builder.id;
        this.acquisitionTypeOid = builder.acquisitionTypeOid;
        this.productOid = builder.productOid;
        this.productName = builder.productName;
        this.quantity = builder.quantity;
        this.realCost = builder.realCost;
        this.unitRealCost = builder.unitRealCost;
        this.unitPublicCost = builder.unitPublicCost;
        this.supplierOid = builder.supplierOid;
        this.supplierName = builder.supplierName;
        this.invoiceNumber = builder.invoiceNumber;
        this.acquisitionDate = builder.acquisitionDate;
        this.observations = builder.observations;
        this.userOid = builder.userOid;
        this.isDeleted = builder.isDeleted;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Acquisition builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getAcquisitionTypeOid() {
        return acquisitionTypeOid;
    }

    public String getProductOid() {
        return productOid;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getRealCost() {
        return realCost;
    }

    public Double getUnitRealCost() {
        return unitRealCost;
    }

    public Double getUnitPublicCost() {
        return unitPublicCost;
    }

    public String getSupplierOid() {
        return supplierOid;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public String getObservations() {
        return observations;
    }

    public String getUserOid() {
        return userOid;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Fluent builder for {@link Acquisition} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String acquisitionTypeOid;
        private String productOid;
        private String productName;
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
         * Sets the identifier.
         *
         * @param id Identifier.
         * @return This builder.
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the acquisition type identifier.
         *
         * @param acquisitionTypeOid Acquisition type identifier.
         * @return This builder.
         */
        public Builder acquisitionTypeOid(String acquisitionTypeOid) {
            this.acquisitionTypeOid = acquisitionTypeOid;
            return this;
        }

        /**
         * Sets the optional product identifier.
         *
         * @param productOid Product identifier.
         * @return This builder.
         */
        public Builder productOid(String productOid) {
            this.productOid = productOid;
            return this;
        }

        /**
         * Sets the product name.
         *
         * @param productName Product name.
         * @return This builder.
         */
        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        /**
         * Sets the quantity.
         *
         * @param quantity Quantity.
         * @return This builder.
         */
        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the total real cost.
         *
         * @param realCost Total real cost.
         * @return This builder.
         */
        public Builder realCost(Double realCost) {
            this.realCost = realCost;
            return this;
        }

        /**
         * Sets the computed unit real cost.
         *
         * @param unitRealCost Unit real cost.
         * @return This builder.
         */
        public Builder unitRealCost(Double unitRealCost) {
            this.unitRealCost = unitRealCost;
            return this;
        }

        /**
         * Sets the unit public cost.
         *
         * @param unitPublicCost Unit public cost.
         * @return This builder.
         */
        public Builder unitPublicCost(Double unitPublicCost) {
            this.unitPublicCost = unitPublicCost;
            return this;
        }

        /**
         * Sets the supplier identifier.
         *
         * @param supplierOid Supplier identifier.
         * @return This builder.
         */
        public Builder supplierOid(String supplierOid) {
            this.supplierOid = supplierOid;
            return this;
        }

        /**
         * Sets the supplier name.
         *
         * @param supplierName Supplier name.
         * @return This builder.
         */
        public Builder supplierName(String supplierName) {
            this.supplierName = supplierName;
            return this;
        }

        /**
         * Sets the invoice number.
         *
         * @param invoiceNumber Invoice number.
         * @return This builder.
         */
        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        /**
         * Sets the acquisition date.
         *
         * @param acquisitionDate Acquisition date.
         * @return This builder.
         */
        public Builder acquisitionDate(LocalDateTime acquisitionDate) {
            this.acquisitionDate = acquisitionDate;
            return this;
        }

        /**
         * Sets optional observations.
         *
         * @param observations Observations.
         * @return This builder.
         */
        public Builder observations(String observations) {
            this.observations = observations;
            return this;
        }

        /**
         * Sets the owning user identifier.
         *
         * @param userOid User identifier.
         * @return This builder.
         */
        public Builder userOid(String userOid) {
            this.userOid = userOid;
            return this;
        }

        /**
         * Sets the logical delete flag.
         *
         * @param isDeleted Delete flag.
         * @return This builder.
         */
        public Builder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        /**
         * Sets the creation date.
         *
         * @param createdDate Creation date.
         * @return This builder.
         */
        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        /**
         * Sets the last update date.
         *
         * @param updatedDate Update date.
         * @return This builder.
         */
        public Builder updatedDate(LocalDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        /**
         * Builds the acquisition validating its invariants.
         *
         * @return Immutable acquisition.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if type, owner, quantity or real cost are missing or invalid.
         */
        public Acquisition build() {
            EntityValidation.requireNotBlank(acquisitionTypeOid, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requireNotBlank(userOid, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requirePositive(quantity, ValidationConstants.FIELD_INVALID_TYPE);
            EntityValidation.requireNonNegative(realCost, ValidationConstants.FIELD_INVALID_RANGE);
            return new Acquisition(this);
        }
    }
}
