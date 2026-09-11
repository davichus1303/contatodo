package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;

/**
 * Domain entity representing an expense.
 *
 * <p>Immutable; construction is only possible through {@link Builder#build()}.</p>
 */
public final class Expense {

    private final String id;
    private final String acquisitionOid;
    private final String acquisitionTypeOid;
    private final String name;
    private final Integer quantity;
    private final Double amount;
    private final String currency;
    private final LocalDateTime expenseDate;
    private final String userOid;
    private final Boolean isActive;
    private final Boolean isDeleted;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;

    /**
     * Creates an expense from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Expense(Builder builder) {
        this.id = builder.id;
        this.acquisitionOid = builder.acquisitionOid;
        this.acquisitionTypeOid = builder.acquisitionTypeOid;
        this.name = builder.name;
        this.quantity = builder.quantity;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.expenseDate = builder.expenseDate;
        this.userOid = builder.userOid;
        this.isActive = builder.isActive;
        this.isDeleted = builder.isDeleted;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Expense builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getAcquisitionOid() {
        return acquisitionOid;
    }

    public String getAcquisitionTypeOid() {
        return acquisitionTypeOid;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }

    public String getUserOid() {
        return userOid;
    }

    public Boolean getIsActive() {
        return isActive;
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
     * Fluent builder for {@link Expense} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String acquisitionOid;
        private String acquisitionTypeOid;
        private String name;
        private Integer quantity;
        private Double amount;
        private String currency;
        private LocalDateTime expenseDate;
        private String userOid;
        private Boolean isActive;
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
         * Sets the optional linked acquisition identifier.
         *
         * @param acquisitionOid Acquisition identifier.
         * @return This builder.
         */
        public Builder acquisitionOid(String acquisitionOid) {
            this.acquisitionOid = acquisitionOid;
            return this;
        }

        /**
         * Sets the optional linked acquisition type identifier.
         *
         * @param acquisitionTypeOid Acquisition type identifier.
         * @return This builder.
         */
        public Builder acquisitionTypeOid(String acquisitionTypeOid) {
            this.acquisitionTypeOid = acquisitionTypeOid;
            return this;
        }

        /**
         * Sets the expense name.
         *
         * @param name Name.
         * @return This builder.
         */
        public Builder name(String name) {
            this.name = name;
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
         * Sets the amount.
         *
         * @param amount Amount.
         * @return This builder.
         */
        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Sets the currency code.
         *
         * @param currency Currency code.
         * @return This builder.
         */
        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Sets the expense date.
         *
         * @param expenseDate Expense date.
         * @return This builder.
         */
        public Builder expenseDate(LocalDateTime expenseDate) {
            this.expenseDate = expenseDate;
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
         * Builds the expense validating its invariants.
         *
         * @return Immutable expense.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if name, owner or date are missing, or the amount is negative.
         */
        public Expense build() {
            EntityValidation.requireNotBlank(name, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requireNotBlank(userOid, ValidationConstants.FIELD_REQUIRED);
            if (expenseDate == null) {
                throw new com.contatodo.domain.exception.InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            EntityValidation.requireNonNegative(amount, ValidationConstants.FIELD_INVALID_RANGE);
            return new Expense(this);
        }
    }
}
