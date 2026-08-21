package com.contatodo.application.dto.request;

import com.contatodo.application.dto.request.RolePermissionRequest;

import java.util.List;

/**
 * Request DTO for updating a role.
 */
public class UpdateRoleRequest {

    private String name;

    private List<RolePermissionRequest> permissions;

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
     */
    public UpdateRoleRequest(String name, List<RolePermissionRequest> permissions) {
        this.name = name;
        this.permissions = permissions;
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
}
