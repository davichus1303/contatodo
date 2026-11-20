package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.utils.EntityValidation;

/**
 * Domain entity representing a unit of measure.
 *
 * <p>Immutable; logical deletion is expressed through {@link #markDeleted()}.
 * Construction is only possible through {@link Builder#build()}.</p>
 */
public final class UnitOfMeasure {

    private final String id;
    private final String name;
    private final String abrev;
    private final String companyOid;
    private final Boolean isActive;
    private final Boolean isDeleted;
    private final Boolean isDivisible;
    private final Boolean isPack;

    /**
     * Creates a unit of measure from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private UnitOfMeasure(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.abrev = builder.abrev;
        this.companyOid = builder.companyOid;
        this.isActive = builder.isActive;
        this.isDeleted = builder.isDeleted;
        this.isDivisible = builder.isDivisible;
        this.isPack = builder.isPack;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Unit of measure builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Resolves whether the unit of measure can be sold in fractions,
     * treating a missing flag as false.
     *
     * @return True when the unit is divisible.
     */
    public boolean isDivisible() {
        return Boolean.TRUE.equals(isDivisible);
    }

    /**
     * Resolves whether the unit of measure represents a pack or presentation
     * that contains several base units, treating a missing flag as false.
     *
     * @return True when the unit is a pack.
     */
    public boolean isPack() {
        return Boolean.TRUE.equals(isPack);
    }

    /**
     * Performs the logical deletion of the unit of measure.
     *
     * @return New instance flagged as deleted and inactive.
     */
    public UnitOfMeasure markDeleted() {
        return copyBase()
                .isActive(false)
                .isDeleted(true)
                .build();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbrev() {
        return abrev;
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public Boolean getIsDivisible() {
        return isDivisible;
    }

    public Boolean getIsPack() {
        return isPack;
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
                .abrev(abrev)
                .companyOid(companyOid)
                .isActive(isActive)
                .isDeleted(isDeleted)
                .isDivisible(isDivisible)
                .isPack(isPack);
    }

    /**
     * Fluent builder for {@link UnitOfMeasure} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String name;
        private String abrev;
        private String companyOid;
        private Boolean isActive;
        private Boolean isDeleted;
        private Boolean isDivisible;
        private Boolean isPack;

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
         * Sets the abbreviation.
         *
         * @param abrev Abbreviation.
         * @return This builder.
         */
        public Builder abrev(String abrev) {
            this.abrev = abrev;
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
         * Sets the divisible flag.
         *
         * @param isDivisible Divisible flag.
         * @return This builder.
         */
        public Builder isDivisible(Boolean isDivisible) {
            this.isDivisible = isDivisible;
            return this;
        }

        /**
         * Sets the pack flag.
         *
         * @param isPack Pack flag.
         * @return This builder.
         */
        public Builder isPack(Boolean isPack) {
            this.isPack = isPack;
            return this;
        }

        /**
         * Builds the unit of measure validating its invariants.
         *
         * @return Immutable unit of measure.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if the name or abbreviation is missing.
         */
        public UnitOfMeasure build() {
            EntityValidation.requireNotBlank(name, ValidationConstants.FIELD_REQUIRED);
            EntityValidation.requireNotBlank(abrev, ValidationConstants.FIELD_REQUIRED);
            return new UnitOfMeasure(this);
        }
    }
}
