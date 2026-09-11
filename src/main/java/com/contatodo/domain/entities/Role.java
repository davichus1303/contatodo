package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.shared.constants.ValidationConstants;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain entity representing a role in the application.
 *
 * <p>A role groups permissions over several modules. Immutable; logical
 * deletion and edits are expressed as transitions returning new instances.
 * Construction is only possible through {@link Builder#build()}.</p>
 */
public final class Role {

    private final String id;
    private final String name;
    private final List<RolePermission> permissions;
    private final Boolean isDeleted;
    private final Boolean isActive;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final String createdBy;

    /**
     * Creates a role from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.permissions = builder.permissions == null ? List.of() : List.copyOf(builder.permissions);
        this.isDeleted = builder.isDeleted;
        this.isActive = builder.isActive;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
        this.createdBy = builder.createdBy;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Role builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Performs the logical deletion of the role.
     *
     * @return New instance flagged as deleted with an updated timestamp.
     */
    public Role markDeleted() {
        return copyBase()
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

    /**
     * Gets the module permissions granted to this role.
     *
     * @return Unmodifiable permission list.
     */
    public List<RolePermission> getPermissions() {
        return permissions;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
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
                .permissions(permissions)
                .isDeleted(isDeleted)
                .isActive(isActive)
                .createdDate(createdDate)
                .updatedDate(updatedDate)
                .createdBy(createdBy);
    }

    /**
     * Fluent builder for {@link Role} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String name;
        private List<RolePermission> permissions;
        private Boolean isDeleted;
        private Boolean isActive;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private String createdBy;

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
         * Sets the role name.
         *
         * @param name Name.
         * @return This builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the module permissions.
         *
         * @param permissions Permission list.
         * @return This builder.
         */
        public Builder permissions(List<RolePermission> permissions) {
            this.permissions = permissions;
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
         * Sets the creator identifier.
         *
         * @param createdBy Creator identifier.
         * @return This builder.
         */
        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        /**
         * Builds the role validating its invariants.
         *
         * @return Immutable role.
         * @throws InvalidEntityStateException if the name is missing.
         */
        public Role build() {
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidEntityStateException(ValidationConstants.FIELD_REQUIRED);
            }
            return new Role(this);
        }
    }
}
