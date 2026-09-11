package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.domain.model.Email;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.utils.EntityValidation;

import java.time.LocalDateTime;

/**
 * Domain entity representing a user.
 *
 * <p>Instances are immutable; logical deletion is expressed through the
 * {@link #markDeleted()} transition which returns a new instance.
 * Construction is only possible through {@link Builder#build()}, which
 * validates mandatory fields and email format.</p>
 */
public final class User {

    private final String id;
    private final String userName;
    private final String email;
    private final String password;
    private final String name;
    private final boolean isActive;
    private final boolean isDelete;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final String createdBy;
    private final String updatedBy;

    /**
     * Creates a user from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private User(Builder builder) {
        this.id = builder.id;
        this.userName = builder.userName;
        this.email = builder.email;
        this.password = builder.password;
        this.name = builder.name;
        this.isActive = builder.isActive;
        this.isDelete = builder.isDelete;
        this.createdDate = builder.createdDate;
        this.updatedDate = builder.updatedDate;
        this.createdBy = builder.createdBy;
        this.updatedBy = builder.updatedBy;
    }

    /**
     * Creates a new empty builder.
     *
     * @return User builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDelete() {
        return isDelete;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Performs the logical deletion of the user.
     *
     * @return New user instance flagged as deleted and inactive.
     */
    public User markDeleted() {
        Builder builder = copyBase();
        builder.isDelete = true;
        builder.isActive = false;
        builder.updatedDate = LocalDateTime.now();
        return new User(builder);
    }

    /**
     * Creates a builder preloaded with the current state.
     *
     * @return Preloaded builder.
     */
    private Builder copyBase() {
        return builder()
                .id(id)
                .userName(userName)
                .email(email)
                .password(password)
                .name(name)
                .isActive(isActive)
                .isDelete(isDelete)
                .createdDate(createdDate)
                .updatedDate(updatedDate)
                .createdBy(createdBy)
                .updatedBy(updatedBy);
    }

    /**
     * Fluent builder for {@link User} with invariant validation.
     */
    public static class Builder {

        private String id;
        private String userName;
        private String email;
        private String password;
        private String name;
        private boolean isActive = true;
        private boolean isDelete;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private String createdBy;
        private String updatedBy;

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
         * Sets the username.
         *
         * @param userName Username.
         * @return This builder.
         */
        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Sets and validates the email format.
         *
         * @param email Email text.
         * @return This builder.
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the hashed password.
         *
         * @param password Hashed password.
         * @return This builder.
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the display name.
         *
         * @param name Display name.
         * @return This builder.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the active flag.
         *
         * @param isActive Active flag.
         * @return This builder.
         */
        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * Sets the logical delete flag.
         *
         * @param isDelete Delete flag.
         * @return This builder.
         */
        public Builder isDelete(boolean isDelete) {
            this.isDelete = isDelete;
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
         * Sets the last updater identifier.
         *
         * @param updatedBy Updater identifier.
         * @return This builder.
         */
        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        /**
         * Builds the user validating its invariants.
         *
         * @return Immutable user.
         * @throws InvalidEntityStateException if mandatory fields are missing or the email format is invalid.
         */
        public User build() {
            EntityValidation.requireNotBlank(userName, UserConstants.USER_USERNAME_REQUIRED);
            EntityValidation.requireNotBlank(email, UserConstants.USER_EMAIL_REQUIRED);
            Email.of(email);
            if (password == null || password.isEmpty()) {
                throw new InvalidEntityStateException(UserConstants.USER_PASSWORD_REQUIRED);
            }
            EntityValidation.requireNotBlank(name, UserConstants.USER_NAME_REQUIRED);
            return new User(this);
        }
    }
}
