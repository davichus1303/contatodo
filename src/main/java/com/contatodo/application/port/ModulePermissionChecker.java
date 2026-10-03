package com.contatodo.application.port;

import com.contatodo.domain.model.ModulePermissionAction;

/**
 * Outbound port to verify whether the current session may act on a module.
 *
 * <p>Modules are addressed by their link (for example {@code /users}) because
 * the identifier is generated per database. Implementations resolve the link to
 * the stored module and compare it against the permissions granted to the role
 * carried by the session.</p>
 */
public interface ModulePermissionChecker {

    /**
     * Checks whether the current session may perform an action over a module.
     *
     * @param moduleLink Module link.
     * @param action Action to verify.
     * @return True when the action is granted.
     */
    boolean hasPermission(String moduleLink, ModulePermissionAction action);

    /**
     * Fails when the current session cannot perform an action over a module.
     *
     * @param moduleLink Module link.
     * @param action Action to verify.
     * @throws com.contatodo.shared.exceptions.AccessDeniedException when the
     *         module is not granted or the action is not allowed on it.
     */
    void requirePermission(String moduleLink, ModulePermissionAction action);
}
