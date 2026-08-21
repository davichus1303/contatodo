package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.shared.constants.ValidationConstants;

import java.time.LocalDateTime;

/**
 * Domain entity representing an acquisition type.
 *
 * <p>Immutable; logical deletion is expressed through {@link #markDeleted()}.
 * Construction is only possible through {@link Builder#build()}.</p>
 */
public final class AcquisitionType {

    private final String id;
    private final String name;
    private final String description;
    private final String userOid;
    private final Boolean isActive;
    private final Boolean isDeleted;
    private final Boolean affectsInventory;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;

    /**
     * Creates an acquisition type from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private AcquisitionType(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.userOid = builder.userOid;
        this.isActive = builder.isActive;
        this.isDeleted = builder.isDeleted;
        this.affectsInventory = builder.affectsInventory;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Acquisition type builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Resolves whether acquisitions of this type affect product inventory,
     * treating a missing flag as false.
     *
     * @return True when inventory is affected.
     */
    public boolean affectsInventory() {
        return Boolean.TRUE.equals(affectsInventory);
    }

    /**
     * Performs the logical deletion of the acquisition type.
     *
     * @return New instance flagged as deleted and inactive.
     */
    public AcquisitionType markDeleted() {
        return copyBase()
                .isActive(false)
                .isDeleted(true)
                .updatedDate(LocalDateTime.now())
                .build();
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

    public String getUserOid() {
        return userOid;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public Boolean getAffectsInventory() {
        return affectsInventory;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
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
                .userOid(userOid)
                .isActive(isActive)
                .isDeleted(isDeleted)
                .affectsInventory(affectsInventory)
                .createdDate(createdDate)
                .updatedDate(updatedDate);
    }

    /**
     * Fluent builder for {@link AcquisitionType} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String name;
        private String description;
        private String userOid;
        private Boolean isActive;
        private Boolean isDeleted;
        private Boolean affectsInventory;
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
         * Sets the inventory impact flag.
         *
         * @param affectsInventory Inventory impact flag.
         * @return This builder.
         */
        public Builder affectsInventory(Boolean affectsInventory) {
            this.affectsInventory = affectsInventory;
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
         * Builds the acquisition type validating its invariants.
         *
         * @return Immutable acquisition type.
         * @throws InvalidEntityStateException if the name or owner is missing.
         */
        public AcquisitionType build() {
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            if (userOid == null || userOid.trim().isEmpty()) {
                throw new InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            return new AcquisitionType(this);
        }
    }
}
