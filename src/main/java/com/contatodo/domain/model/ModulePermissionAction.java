package com.contatodo.domain.model;

import com.contatodo.application.dto.response.RolePermissionResponse;

import java.util.function.Function;

/**
 * Actions a role can be granted over a module.
 *
 * <p>Each action reads its flag from {@link RolePermissionResponse.Permissions},
 * the payload that already travels in the {@code permissionOfRole} claim, so
 * the authorization check and the login response cannot drift apart.</p>
 */
public enum ModulePermissionAction {

    VIEW(RolePermissionResponse.Permissions::getView),
    CREATE(RolePermissionResponse.Permissions::getCreate),
    UPDATE(RolePermissionResponse.Permissions::getUpdate),
    DELETE(RolePermissionResponse.Permissions::getDelete);

    private final Function<RolePermissionResponse.Permissions, Boolean> flag;

    ModulePermissionAction(Function<RolePermissionResponse.Permissions, Boolean> flag) {
        this.flag = flag;
    }

    /**
     * Checks whether the given permissions grant this action.
     *
     * <p>Only an explicit {@code true} counts as granted, because documents
     * written before a flag existed leave it {@code null}.</p>
     *
     * @param permissions Permissions granted over a module, possibly {@code null}.
     * @return True when the action is granted.
     */
    public boolean isGranted(RolePermissionResponse.Permissions permissions) {
        return permissions != null && Boolean.TRUE.equals(flag.apply(permissions));
    }
}
