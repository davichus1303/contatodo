package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.shared.constants.ValidationConstants;

import java.time.LocalDateTime;

/**
 * Domain entity representing an application module.
 *
 * <p>Immutable; construction is only possible through {@link Builder#build()}.</p>
 */
public final class Module {

    private final String id;
    private final String name;
    private final String link;
    private final Boolean isActive;
    private final Boolean isDeleted;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;

    /**
     * Creates a module from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Module(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.link = builder.link;
        this.isActive = builder.isActive;
        this.isDeleted = builder.isDeleted;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Module builder.
     */
    public static Module.Builder builder() {
        return new Module.Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
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
     * Fluent builder for {@link Module} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String name;
        private String link;
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
         * Sets the module name.
         *
         * @param name Name.
         * @return This builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the module link.
         *
         * @param link Link.
         * @return This builder.
         */
        public Builder link(String link) {
            this.link = link;
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
         * Builds the module validating its invariants.
         *
         * @return Immutable module.
         * @throws InvalidEntityStateException if the name or link is missing.
         */
        public Module build() {
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            if (link == null || link.trim().isEmpty()) {
                throw new InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            return new Module(this);
        }
    }
}
