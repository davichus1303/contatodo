package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;

/**
 * Domain entity representing a snapshot of product costs for a given
 * acquisition.
 *
 * <p>Immutable; construction is only possible through {@link Builder#build()}.</p>
 */
public final class ProductCostHistory {

    private final String id;
    private final String productOid;
    private final String acquisitionOid;
    private final Integer quantity;
    private final Integer remainingQuantity;
    private final Double realCost;
    private final Double unitRealCost;
    private final Double unitPublicCostAtPurchase;
    private final LocalDateTime acquisitionDate;
    private final String userOid;
    private final LocalDateTime createdDate;

    /**
     * Creates a product cost history entry from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private ProductCostHistory(Builder builder) {
        this.id = builder.id;
        this.productOid = builder.productOid;
        this.acquisitionOid = builder.acquisitionOid;
        this.quantity = builder.quantity;
        this.remainingQuantity = builder.remainingQuantity;
        this.realCost = builder.realCost;
        this.unitRealCost = builder.unitRealCost;
        this.unitPublicCostAtPurchase = builder.unitPublicCostAtPurchase;
        this.acquisitionDate = builder.acquisitionDate;
        this.userOid = builder.userOid;
        this.createdDate = builder.createdDate;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Product cost history builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getProductOid() {
        return productOid;
    }

    public String getAcquisitionOid() {
        return acquisitionOid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public Double getRealCost() {
        return realCost;
    }

    public Double getUnitRealCost() {
        return unitRealCost;
    }

    public Double getUnitPublicCostAtPurchase() {
        return unitPublicCostAtPurchase;
    }

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public String getUserOid() {
        return userOid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Fluent builder for {@link ProductCostHistory} with invariant validation.
     */
    public static class Builder {

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
         * Sets the product identifier.
         *
         * @param productOid Product identifier.
         * @return This builder.
         */
        public Builder productOid(String productOid) {
            this.productOid = productOid;
            return this;
        }

        /**
         * Sets the linked acquisition identifier.
         *
         * @param acquisitionOid Acquisition identifier.
         * @return This builder.
         */
        public Builder acquisitionOid(String acquisitionOid) {
            this.acquisitionOid = acquisitionOid;
            return this;
        }

        /**
         * Sets the acquired quantity.
         *
         * @param quantity Quantity.
         * @return This builder.
         */
        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the remaining (not yet consumed) quantity.
         *
         * @param remainingQuantity Remaining quantity.
         * @return This builder.
         */
        public Builder remainingQuantity(Integer remainingQuantity) {
            this.remainingQuantity = remainingQuantity;
            return this;
        }

        /**
         * Sets the total real cost of the acquisition.
         *
         * @param realCost Total real cost.
         * @return This builder.
         */
        public Builder realCost(Double realCost) {
            this.realCost = realCost;
            return this;
        }

        /**
         * Sets the average unit real cost at purchase time.
         *
         * @param unitRealCost Unit real cost.
         * @return This builder.
         */
        public Builder unitRealCost(Double unitRealCost) {
            this.unitRealCost = unitRealCost;
            return this;
        }

        /**
         * Sets the unit public cost at purchase time.
         *
         * @param unitPublicCostAtPurchase Unit public cost.
         * @return This builder.
         */
        public Builder unitPublicCostAtPurchase(Double unitPublicCostAtPurchase) {
            this.unitPublicCostAtPurchase = unitPublicCostAtPurchase;
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
         * Builds the history entry validating its invariants.
         *
         * @return Immutable history entry.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if product, owner or dates are missing, or quantities are invalid.
         */
        public ProductCostHistory build() {
            EntityValidation.requireNotBlank(productOid, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requireNotBlank(userOid, ValidationConstants.FIELD_REQUIRED);
            if (acquisitionDate == null || createdDate == null) {
                throw new com.contatodo.domain.exception.InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            if (quantity != null && remainingQuantity != null && remainingQuantity > quantity) {
                throw new com.contatodo.domain.exception.InvalidEntityStateException(ValidationConstants.FIELD_INVALID_RANGE);
            }
            return new ProductCostHistory(this);
        }
    }
}
