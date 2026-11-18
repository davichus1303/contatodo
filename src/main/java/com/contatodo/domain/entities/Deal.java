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

    public static Builder builder() {
        return new Builder();
    }

    public Deal markDeleted(LocalDateTime updateDate) {
        return copyBase()
                .isDelete(true)
                .isActive(false)
                .updateDate(updateDate)
                .build();
    }

    public String getId() {
        return id;
    }

    public String getProductOid() {
        return productOid;
    }

    public String getUnitOfMeasureOid() {
        return unitOfMeasureOid;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

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

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder productOid(String productOid) {
            this.productOid = productOid;
            return this;
        }

        public Builder unitOfMeasureOid(String unitOfMeasureOid) {
            this.unitOfMeasureOid = unitOfMeasureOid;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder companyOid(String companyOid) {
            this.companyOid = companyOid;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder updateDate(LocalDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder isDelete(Boolean isDelete) {
            this.isDelete = isDelete;
            return this;
        }

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
