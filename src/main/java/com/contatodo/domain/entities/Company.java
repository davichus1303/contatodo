package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;

/**
 * Domain entity representing a company.
 *
 * <p>Instances are immutable; logical deletion is expressed through the
 * {@link #markDeleted()} transition which returns a new instance.
 * Construction is only possible through {@link Builder#build()}.</p>
 */
public final class Company {

    private final String id;
    private final String name;
    private final String rfc;
    private final String webSite;
    private final String ubication;
    private final String contactUserOId;
    private final Boolean isActive;
    private final Boolean isDeleted;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final String createdBy;

    /**
     * Creates a company from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private Company(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.rfc = builder.rfc;
        this.webSite = builder.webSite;
        this.ubication = builder.ubication;
        this.contactUserOId = builder.contactUserOId;
        this.isActive = builder.isActive;
        this.isDeleted = builder.isDeleted;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
        this.createdBy = builder.createdBy;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Company builder.
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

    public String getRfc() {
        return rfc;
    }

    public String getWebSite() {
        return webSite;
    }

    public String getUbication() {
        return ubication;
    }

    public String getContactUserOId() {
        return contactUserOId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Performs the logical deletion of the company.
     *
     * @return New company instance flagged as deleted and inactive.
     */
    public Company markDeleted() {
        return copyBase()
                .isActive(false)
                .isDeleted(true)
                .updatedDate(LocalDateTime.now())
                .build();
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
                .rfc(rfc)
                .webSite(webSite)
                .ubication(ubication)
                .contactUserOId(contactUserOId)
                .isActive(isActive)
                .isDeleted(isDeleted)
                .createdDate(createdDate)
                .updatedDate(updatedDate)
                .createdBy(createdBy);
    }

    /**
     * Fluent builder for {@link Company} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String name;
        private String rfc;
        private String webSite;
        private String ubication;
        private String contactUserOId;
        private Boolean isActive;
        private Boolean isDeleted;
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
         * Sets the tax identifier.
         *
         * @param rfc RFC.
         * @return This builder.
         */
        public Builder rfc(String rfc) {
            this.rfc = rfc;
            return this;
        }

        /**
         * Sets the website.
         *
         * @param webSite Website.
         * @return This builder.
         */
        public Builder webSite(String webSite) {
            this.webSite = webSite;
            return this;
        }

        /**
         * Sets the location.
         *
         * @param ubication Location.
         * @return This builder.
         */
        public Builder ubication(String ubication) {
            this.ubication = ubication;
            return this;
        }

        /**
         * Sets the identifier of the user that acts as contact.
         *
         * @param contactUserOId Contact user identifier.
         * @return This builder.
         */
        public Builder contactUserOId(String contactUserOId) {
            this.contactUserOId = contactUserOId;
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
         * Builds the company validating its invariants.
         *
         * @return Immutable company.
         * @throws com.contatodo.domain.exception.InvalidEntityStateException if the name is missing.
         */
        public Company build() {
            EntityValidation.requireNotBlank(name, ValidationConstants.FIELD_REQUIRED);
            return new Company(this);
        }
    }
}
