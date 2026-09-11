package com.contatodo.domain.entities;

import com.contatodo.domain.model.Money;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.SaleWithoutProfitException;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a sale.
 *
 * <p>Instances are immutable. New sales can only be created through
 * {@link #place(Long, String, String, String, Integer, Double, Money, Money, String)},
 * which enforces the business rule that every sale must generate profit.
 * Hydration from persistence uses {@link Builder#build()}, which only checks
 * structural integrity so legacy data can still be read.</p>
 */
public final class Sale {

    private final String id;
    private final Long saleNumber;
    private final String productOid;
    private final String productName;
    private final String userOid;
    private final Integer quantity;
    private final Double totalCost;
    private final Double originalTotalPrice;
    private final Double totalSalePrice;
    private final LocalDateTime saleDate;
    private final String notes;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final Boolean isDeleted;

    /**
     * Creates a sale from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Sale(Builder builder) {
        this.id = builder.id;
        this.saleNumber = builder.saleNumber;
        this.productOid = builder.productOid;
        this.productName = builder.productName;
        this.userOid = builder.userOid;
        this.quantity = builder.quantity;
        this.totalCost = builder.totalCost;
        this.originalTotalPrice = builder.originalTotalPrice;
        this.totalSalePrice = builder.totalSalePrice;
        this.saleDate = builder.saleDate;
        this.notes = builder.notes;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
        this.isDeleted = builder.isDeleted;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Sale builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Places a new sale enforcing the profit invariant.
     *
     * @param saleNumber Sequential daily sale number.
     * @param productOid Product identifier.
     * @param productName Product name at the time of the sale.
     * @param userOid Selling user identifier.
     * @param quantity Sold quantity.
     * @param totalSalePrice Agreed total sale price.
     * @param totalCost Computed total cost of the sold units.
     * @param originalTotalPrice List-price total for the sold units.
     * @param notes Optional sale notes.
     * @return Immutable sale ready to persist.
     * @throws SaleWithoutProfitException if the sale price does not exceed the total cost.
     * @throws InvalidEntityStateException if mandatory fields are missing.
     */
    public static Sale place(
            Long saleNumber,
            String productOid,
            String productName,
            String userOid,
            Integer quantity,
            Double totalSalePrice,
            Money totalCost,
            Money originalTotalPrice,
            String notes
    ) {
        Objects.requireNonNull(totalCost, "Total cost must not be null");
        Objects.requireNonNull(originalTotalPrice, "Original total price must not be null");
        if (!Money.of(totalSalePrice).isGreaterThan(totalCost)) {
            throw new SaleWithoutProfitException(SaleConstants.SALE_WITHOUT_PROFIT);
        }
        LocalDateTime now = LocalDateTime.now();
        return builder()
                .saleNumber(saleNumber)
                .productOid(productOid)
                .productName(productName)
                .userOid(userOid)
                .quantity(quantity)
                .totalCost(totalCost.toDouble())
                .originalTotalPrice(originalTotalPrice.toDouble())
                .totalSalePrice(totalSalePrice)
                .saleDate(now)
                .notes(notes)
                .createdDate(now)
                .updatedDate(now)
                .isDeleted(false)
                .build();
    }

    public String getId() {
        return id;
    }

    public Long getSaleNumber() {
        return saleNumber;
    }

    public String getProductOid() {
        return productOid;
    }

    public String getProductName() {
        return productName;
    }

    public String getUserOid() {
        return userOid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public Double getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public Double getTotalSalePrice() {
        return totalSalePrice;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * Fluent builder for {@link Sale} with structural validation.
     */
    public static class Builder {

        private String id;
        private Long saleNumber;
        private String productOid;
        private String productName;
        private String userOid;
        private Integer quantity;
        private Double totalCost;
        private Double originalTotalPrice;
        private Double totalSalePrice;
        private LocalDateTime saleDate;
        private String notes;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private Boolean isDeleted;

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
         * Sets the sequential daily sale number.
         *
         * @param saleNumber Sale number.
         * @return This builder.
         */
        public Builder saleNumber(Long saleNumber) {
            this.saleNumber = saleNumber;
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
         * Sets the product name snapshot.
         *
         * @param productName Product name.
         * @return This builder.
         */
        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        /**
         * Sets the selling user identifier.
         *
         * @param userOid User identifier.
         * @return This builder.
         */
        public Builder userOid(String userOid) {
            this.userOid = userOid;
            return this;
        }

        /**
         * Sets the sold quantity.
         *
         * @param quantity Quantity.
         * @return This builder.
         */
        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the total cost.
         *
         * @param totalCost Total cost.
         * @return This builder.
         */
        public Builder totalCost(Double totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        /**
         * Sets the list-price total.
         *
         * @param originalTotalPrice Original total price.
         * @return This builder.
         */
        public Builder originalTotalPrice(Double originalTotalPrice) {
            this.originalTotalPrice = originalTotalPrice;
            return this;
        }

        /**
         * Sets the agreed total sale price.
         *
         * @param totalSalePrice Total sale price.
         * @return This builder.
         */
        public Builder totalSalePrice(Double totalSalePrice) {
            this.totalSalePrice = totalSalePrice;
            return this;
        }

        /**
         * Sets the sale date.
         *
         * @param saleDate Sale date.
         * @return This builder.
         */
        public Builder saleDate(LocalDateTime saleDate) {
            this.saleDate = saleDate;
            return this;
        }

        /**
         * Sets optional notes.
         *
         * @param notes Notes.
         * @return This builder.
         */
        public Builder notes(String notes) {
            this.notes = notes;
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
         * Builds the sale validating its structural invariants.
         *
         * @return Immutable sale.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if mandatory fields are missing or quantity is not positive.
         */
        public Sale build() {
            EntityValidation.requireNotBlank(productOid, SaleConstants.SALE_PRODUCT_OID_REQUIRED);
            EntityValidation.requirePositive(quantity, SaleConstants.SALE_QUANTITY_REQUIRED);
            EntityValidation.requireNotBlank(userOid, SaleConstants.USER_NOT_FOUND);
            return new Sale(this);
        }
    }
}
