package com.contatodo.application.dto.request;

import com.contatodo.domain.entities.RolePermission;

import java.util.List;

/**
 * Request DTO for updating a role.
 */
public class UpdateRoleRequest {

    private String name;

    private List<RolePermission> permissions;

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
    public UpdateRoleRequest(String name, List<RolePermission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RolePermission> permissions) {
        this.permissions = permissions;
    }
}
