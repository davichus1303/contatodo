package com.contatodo.shared.constants;

/**
 * Constants for module module messages and identifiers.
 *
 * <p>The link is the stable identifier of a module: the module identifier itself
 * is generated per database, so it can never be hardcoded in source.</p>
 *
 * @see com.contatodo.infrastructure.security.JwtModulePermissionChecker
 */
public final class ModuleConstants {

    public static final String MODULE_CREATED = "Module created successfully.";
    public static final String MODULE_NOT_FOUND = "Module not found.";
    public static final String MODULE_NAME_REQUIRED = "Module name is required.";
    public static final String MODULE_LINK_REQUIRED = "Module link is required.";

    public static final String USERS_LINK = "/users";

    private ModuleConstants() {
    }
}
