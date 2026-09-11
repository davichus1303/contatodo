package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.domain.model.Money;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InsufficientStockException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a product.
 *
 * <p>Instances are immutable; every state change is expressed as an
 * intentional domain transition that returns a new instance. Construction
 * is only possible through {@link Builder#build()}, which guarantees the
 * entity never exists in an invalid state.</p>
 */
public final class Product {

    private final String id;
    private final String name;
    private final String description;
    private final Integer stock;
    private final String code;
    private final Double realCost;
    private final Double unitRealCost;
    private final Double unitPublicCost;
    private final String urlPhoto;
    private final Boolean isActive;
    private final String userOid;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;

    /**
     * Creates a product from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.stock = builder.stock;
        this.code = builder.code;
        this.realCost = builder.realCost;
        this.unitRealCost = builder.unitRealCost;
        this.unitPublicCost = builder.unitPublicCost;
        this.urlPhoto = builder.urlPhoto;
        this.isActive = builder.isActive;
        this.userOid = builder.userOid;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Product builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStock() {
        return stock;
    }

    public String getCode() {
        return code;
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

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public String getUserOid() {
        return userOid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Decreases stock by the sold quantity, enforcing availability rules.
     *
     * @param quantity Quantity to subtract.
     * @return New product instance with updated stock and timestamp.
     * @throws InsufficientStockException if the product has no stock or it is lower than requested.
     */
    public Product decreaseStock(Integer quantity) {
        if (stock == null || stock <= 0) {
            throw new InsufficientStockException(SaleConstants.PRODUCT_OUT_OF_STOCK);
        }
        if (stock < quantity) {
            throw new InsufficientStockException(SaleConstants.INSUFFICIENT_STOCK);
        }
        return copyWith(stock - quantity, LocalDateTime.now());
    }

    /**
     * Increases stock by the acquired quantity.
     *
     * @param quantity Quantity to add.
     * @return New product instance with updated stock and timestamp.
     */
    public Product replenish(Integer quantity) {
        int current = stock != null ? stock : 0;
        return copyWith(current + quantity, LocalDateTime.now());
    }

    /**
     * Updates the public sale price of the product.
     *
     * @param unitPublicCost New unit public cost.
     * @return New product instance with updated cost and timestamp.
     */
    public Product withUnitPublicCost(Double unitPublicCost) {
        Builder builder = copyBase();
        builder.unitPublicCost = unitPublicCost;
        builder.updatedDate = LocalDateTime.now();
        return new Product(builder);
    }

    /**
     * Recalculates costs using a new weighted average real unit cost.
     *
     * <p>The total real cost becomes the average multiplied by current
     * stock, keeping both figures consistent.</p>
     *
     * @param averageUnitRealCost Weighted average unit real cost.
     * @return New product instance with recalculated costs.
     */
    public Product withAverageUnitRealCost(Double averageUnitRealCost) {
        int effectiveStock = stock != null ? stock : 0;
        double totalRealCost = Money.of(averageUnitRealCost).multiply(effectiveStock).toDouble();
        Builder builder = copyBase();
        builder.unitRealCost = averageUnitRealCost;
        builder.realCost = totalRealCost;
        builder.updatedDate = LocalDateTime.now();
        return new Product(builder);
    }

    /**
     * Copies all fields applying a new stock value.
     *
     * @param newStock Updated stock.
     * @param now Update timestamp.
     * @return New product instance.
     */
    private Product copyWith(int newStock, LocalDateTime now) {
        Builder builder = copyBase();
        builder.stock = newStock;
        builder.updatedDate = now;
        return new Product(builder);
    }

    /**
     * Creates a builder preloaded with the current state.
     *
     * @return Preloaded builder.
     */
    private Builder copyBase() {
        return builder()
                .id(id)
                .name(name)
                .description(description)
                .stock(stock)
                .code(code)
                .realCost(realCost)
                .unitRealCost(unitRealCost)
                .unitPublicCost(unitPublicCost)
                .urlPhoto(urlPhoto)
                .isActive(isActive)
                .userOid(userOid)
                .createdDate(createdDate)
                .updatedDate(updatedDate);
    }

    /**
     * Fluent builder for {@link Product} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String name;
        private String description;
        private Integer stock;
        private String code;
        private Double realCost;
        private Double unitRealCost;
        private Double unitPublicCost;
        private String urlPhoto;
        private Boolean isActive;
        private String userOid;
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
         * Sets the name.
         *
         * @param name Name.
         * @return This builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the description.
         *
         * @param description Description.
         * @return This builder.
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the stock quantity.
         *
         * @param stock Stock quantity.
         * @return This builder.
         */
        public Builder stock(Integer stock) {
            this.stock = stock;
            return this;
        }

        /**
         * Sets the product code.
         *
         * @param code Code.
         * @return This builder.
         */
        public Builder code(String code) {
            this.code = code;
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
         * Sets the unit real cost.
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
         * Sets the photo URL.
         *
         * @param urlPhoto Photo URL.
         * @return This builder.
         */
        public Builder urlPhoto(String urlPhoto) {
            this.urlPhoto = urlPhoto;
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
         * Builds the product validating its invariants.
         *
         * @return Immutable product.
         * @throws InvalidEntityStateException if mandatory fields are missing or amounts are negative.
         */
        public Product build() {
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidEntityStateException(ProductConstants.PRODUCT_NAME_REQUIRED);
            }
            if (stock != null && stock < 0) {
                throw new InvalidEntityStateException(ProductConstants.PRODUCT_STOCK_INVALID);
            }
            validateNonNegative(realCost, "total");
            validateNonNegative(unitRealCost, "unit real");
            validateNonNegative(unitPublicCost, "public");
            return new Product(this);
        }

        /**
         * Validates that an optional amount is not negative.
         *
         * @param amount Amount to check.
         * @param label Label used in the error message.
         */
        private void validateNonNegative(Double amount, String label) {
            if (amount != null && amount < 0) {
                throw new InvalidEntityStateException(ProductConstants.PRODUCT_COST_INVALID + " (" + label + ")");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product)) {
            return false;
        }
        Product product = (Product) other;
        return Objects.equals(id, product.id) && Objects.equals(code, product.code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}
