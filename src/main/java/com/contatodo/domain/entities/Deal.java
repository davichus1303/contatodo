package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;

/**
 * Domain entity representing a deal associated with a product and company.
 *
 * <p>Immutable; state changes are expressed through builder-based copies.
 * Construction is only possible through {@link Builder#build()}.</p>
 */
public final class Deal {

    private final String id;
    private final String productOid;
    private final String unitOfMeasureOid;
    private final Double quantity;
    private final Double price;
    private final String companyOid;
    private final LocalDateTime createdDate;
    private final LocalDateTime updateDate;
    private final Boolean isActive;
    private final Boolean isDelete;

    /**
     * Creates a deal from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Deal(Builder builder) {
        this.id = builder.id;
        this.productOid = builder.productOid;
        this.unitOfMeasureOid = builder.unitOfMeasureOid;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.companyOid = builder.companyOid;
        this.createdDate = builder.createdDate;
        this.updateDate = builder.updateDate;
        this.isActive = builder.isActive;
        this.isDelete = builder.isDelete;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Deal builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Performs the logical deletion of the deal.
     *
     * @param updateDate Timestamp of the deletion update.
     * @return New instance flagged as deleted and inactive.
     */
    public Deal markDeleted(LocalDateTime updateDate) {
        return copyBase()
                .isDelete(true)
                .isActive(false)
                .updateDate(updateDate)
                .build();
    }

    /**
     * Returns the deal identifier.
     *
     * @return Deal identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the product identifier.
     *
     * @return Product identifier.
     */
    public String getProductOid() {
        return productOid;
    }

    /**
     * Returns the unit of measure identifier.
     *
     * @return Unit of measure identifier.
     */
    public String getUnitOfMeasureOid() {
        return unitOfMeasureOid;
    }

    /**
     * Returns the deal quantity.
     *
     * @return Deal quantity.
     */
    public Double getQuantity() {
        return quantity;
    }

    /**
     * Returns the deal price.
     *
     * @return Deal price.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Returns the owning company identifier.
     *
     * @return Company identifier.
     */
    public String getCompanyOid() {
        return companyOid;
    }

    /**
     * Returns the creation date.
     *
     * @return Creation date.
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Returns the last update date.
     *
     * @return Last update date.
     */
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    /**
     * Returns the active flag.
     *
     * @return Active flag.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Returns the logical delete flag.
     *
     * @return Logical delete flag.
     */
    public Boolean getIsDelete() {
        return isDelete;
    }

    /**
     * Creates a builder preloaded with the current state.
     *
     * @return Preloaded builder.
     */
    private Builder copyBase() {
        return builder()
                .id(id)
                .productOid(productOid)
                .unitOfMeasureOid(unitOfMeasureOid)
                .quantity(quantity)
                .price(price)
                .companyOid(companyOid)
                .createdDate(createdDate)
                .updateDate(updateDate)
                .isActive(isActive)
                .isDelete(isDelete);
    }

    /**
     * Fluent builder for {@link Deal} with invariant validation.
     */
    public static class Builder {
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

        /**
         * Sets the deal identifier.
         *
         * @param id Deal identifier.
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
         * Sets the unit of measure identifier.
         *
         * @param unitOfMeasureOid Unit of measure identifier.
         * @return This builder.
         */
        public Builder unitOfMeasureOid(String unitOfMeasureOid) {
            this.unitOfMeasureOid = unitOfMeasureOid;
            return this;
        }

        /**
         * Sets the deal quantity.
         *
         * @param quantity Deal quantity.
         * @return This builder.
         */
        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the deal price.
         *
         * @param price Deal price.
         * @return This builder.
         */
        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        /**
         * Sets the owning company identifier.
         *
         * @param companyOid Company identifier.
         * @return This builder.
         */
        public Builder companyOid(String companyOid) {
            this.companyOid = companyOid;
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
         * @param updateDate Last update date.
         * @return This builder.
         */
        public Builder updateDate(LocalDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        /**
         * Sets the active flag.
         *
         * @param isActive Active flag.
         * @return This builder.
         */
        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * Sets the logical delete flag.
         *
         * @param isDelete Logical delete flag.
         * @return This builder.
         */
        public Builder isDelete(Boolean isDelete) {
            this.isDelete = isDelete;
            return this;
        }

        /**
         * Builds the deal validating its invariants.
         *
         * @return Immutable deal.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if required fields are missing.
         */
        public Deal build() {
            EntityValidation.requireNotBlank(productOid, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requireNotBlank(unitOfMeasureOid, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requireNotBlank(companyOid, ValidationConstants.FIELD_REQUIRED);
            if (quantity == null) {
                throw new com.contatodo.domain.exception.InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            if (price == null) {
                throw new com.contatodo.domain.exception.InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            return new Deal(this);
        }
    }
}
