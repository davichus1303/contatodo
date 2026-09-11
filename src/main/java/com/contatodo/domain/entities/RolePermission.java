package com.contatodo.domain.entities;

import java.util.List;

/**
 * Domain entity representing a role's permissions over a specific module.
 *
 * <p>Immutable value object; construction is only possible through
 * {@link Builder#build()}.</p>
 */
public final class RolePermission {

    private final String moduleOid;
    private final PermissionDetails permissions;

    /**
     * Creates a role permission from its builder.
     *
     * @param builder Source builder with all values set.
     */
    private RolePermission(Builder builder) {
        this.moduleOid = builder.moduleOid;
        this.permissions = builder.permissions;
    }

    /**
     * Creates a new empty builder.
     *
     * @return Role permission builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the target module identifier.
     *
     * @return Module identifier.
     */
    public String getModuleOid() {
        return moduleOid;
    }

    /**
     * Gets the permission details for the module.
     *
     * @return Permission details.
     */
    public PermissionDetails getPermissions() {
        return permissions;
    }

    /**
     * Fluent builder for {@link RolePermission}.
     */
    public static class Builder {

        private String moduleOid;
        private PermissionDetails permissions;

        /**
         * Sets the target module identifier.
         *
         * @param moduleOid Module identifier.
         * @return This builder.
         */
        public Builder moduleOid(String moduleOid) {
            this.moduleOid = moduleOid;
            return this;
        }

        /**
         * Sets the permission details.
         *
         * @param permissions Permission details.
         * @return This builder.
         */
        public Builder permissions(PermissionDetails permissions) {
            this.permissions = permissions;
            return this;
        }

        /**
         * Builds the role permission.
         *
         * @return Immutable role permission.
         */
        public RolePermission build() {
            return new RolePermission(this);
        }
    }

    /**
     * Immutable details of the permissions granted over a module.
     */
    public static final class PermissionDetails {

        private final Boolean create;
        private final Boolean update;
        private final Boolean delete;
        private final Boolean view;

        /**
         * Creates permission details.
         *
         * @param builder Source builder with all values set.
         */
        private PermissionDetails(Builder builder) {
            this.create = builder.create;
            this.update = builder.update;
            this.delete = builder.delete;
            this.view = builder.view;
        }

        /**
         * Creates a new empty builder.
         *
         * @return Permission details builder.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Gets the create flag.
         *
         * @return Create flag.
         */
        public Boolean getCreate() {
            return create;
        }

        /**
         * Gets the update flag.
         *
         * @return Update flag.
         */
        public Boolean getUpdate() {
            return update;
        }

        /**
         * Gets the delete flag.
         *
         * @return Delete flag.
         */
        public Boolean getDelete() {
            return delete;
        }

        /**
         * Gets the view flag.
         *
         * @return View flag.
         */
        public Boolean getView() {
            return view;
        }

        /**
         * Fluent builder for {@link PermissionDetails}.
         */
        public static class Builder {

            private Boolean create;
            private Boolean update;
            private Boolean delete;
            private Boolean view;

            /**
             * Sets the create flag.
             *
             * @param create Create flag.
             * @return This builder.
             */
            public Builder create(Boolean create) {
                this.create = create;
                return this;
            }

            /**
             * Sets the update flag.
             *
             * @param update Update flag.
             * @return This builder.
             */
            public Builder update(Boolean update) {
                this.update = update;
                return this;
            }

            /**
             * Sets the delete flag.
             *
             * @param delete Delete flag.
             * @return This builder.
             */
            public Builder delete(Boolean delete) {
                this.delete = delete;
                return this;
            }

            /**
             * Sets the view flag.
             *
             * @param view View flag.
             * @return This builder.
             */
            public Builder view(Boolean view) {
                this.view = view;
                return this;
            }

            /**
             * Builds the permission details.
             *
             * @return Immutable permission details.
             */
            public PermissionDetails build() {
                return new PermissionDetails(this);
            }
        }
    }
}
