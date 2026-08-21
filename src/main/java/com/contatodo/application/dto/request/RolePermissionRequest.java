package com.contatodo.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload describing a role permission over a module.
 */
public class RolePermissionRequest {

    @NotBlank(message = "Module identifier is required")
    private String moduleOid;

    @NotNull(message = "Permissions are required")
    private Permissions permissions;

    /**
     * Creates an empty request.
     */
    public RolePermissionRequest() {
    }

    /**
     * Creates a request with values.
     *
     * @param moduleOid Module identifier.
     * @param permissions Permission flags.
     */
    public RolePermissionRequest(String moduleOid, Permissions permissions) {
        this.moduleOid = moduleOid;
        this.permissions = permissions;
    }

    public String getModuleOid() {
        return moduleOid;
    }

    public void setModuleOid(String moduleOid) {
        this.moduleOid = moduleOid;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    /**
     * Permission flags over a module.
     */
    public static class Permissions {

        private Boolean create;
        private Boolean update;
        private Boolean delete;
        private Boolean view;

        /**
         * Creates empty permission flags.
         */
        public Permissions() {
        }

        /**
         * Creates permission flags with values.
         *
         * @param create Create flag.
         * @param update Update flag.
         * @param delete Delete flag.
         * @param view View flag.
         */
        public Permissions(Boolean create, Boolean update, Boolean delete, Boolean view) {
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
