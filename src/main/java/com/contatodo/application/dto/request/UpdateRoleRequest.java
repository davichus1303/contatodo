package com.contatodo.application.dto.request;

import com.contatodo.application.dto.request.RolePermissionRequest;

import java.util.List;

/**
 * Request DTO for updating a role.
 */
public class UpdateRoleRequest {

    private String name;

    private List<RolePermissionRequest> permissions;

    private Boolean isActive;

    /**
     * Creates an empty update role request.
     */
    public UpdateRoleRequest() {
    }

    /**
     * Creates an update role request with specified values.
     *
     * @param name Role name.
     * @param permissions List of role permissions.
     * @param isActive Role active flag.
     */
    public UpdateRoleRequest(String name, List<RolePermissionRequest> permissions, Boolean isActive) {
        this.name = name;
        this.permissions = permissions;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RolePermissionRequest> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RolePermissionRequest> permissions) {
        this.permissions = permissions;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
