package com.contatodo.domain.entities;

/**
 * Domain entity representing a role's permissions over a specific module.
 */
public class RolePermission {

    private String moduleOid;
    private PermissionDetails permissions;

    /**
     * Creates an empty role permission.
     */
    public RolePermission() {
    }

    /**
     * Creates a role permission with specified values.
     *
     * @param moduleOid Module identifier.
     * @param permissions Permission details.
     */
    public RolePermission(String moduleOid, PermissionDetails permissions) {
        this.moduleOid = moduleOid;
        this.permissions = permissions;
    }

    public String getModuleOid() {
        return moduleOid;
    }

    public void setModuleOid(String moduleOid) {
        this.moduleOid = moduleOid;
    }

    public PermissionDetails getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionDetails permissions) {
        this.permissions = permissions;
    }

    /**
     * Inner class representing permission details for a module.
     */
    public static class PermissionDetails {
        private Boolean create;
        private Boolean update;
        private Boolean delete;
        private Boolean view;

        /**
         * Creates empty permission details.
         */
        public PermissionDetails() {
        }

        /**
         * Creates permission details with specified values.
         *
         * @param create Create permission.
         * @param update Update permission.
         * @param delete Delete permission.
         * @param view View permission.
         */
        public PermissionDetails(Boolean create, Boolean update, Boolean delete, Boolean view) {
            this.create = create;
            this.update = update;
            this.delete = delete;
            this.view = view;
        }

        public Boolean getCreate() {
            return create;
        }

        public void setCreate(Boolean create) {
            this.create = create;
        }

        public Boolean getUpdate() {
            return update;
        }

        public void setUpdate(Boolean update) {
            this.update = update;
        }

        public Boolean getDelete() {
            return delete;
        }

        public void setDelete(Boolean delete) {
            this.delete = delete;
        }

        public Boolean getView() {
            return view;
        }

        public void setView(Boolean view) {
            this.view = view;
        }
    }
}
