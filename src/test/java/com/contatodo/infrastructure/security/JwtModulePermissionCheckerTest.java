package com.contatodo.infrastructure.security;

import com.contatodo.application.dto.response.RolePermissionResponse;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.domain.model.ModulePermissionAction;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.ModuleConstants;
import com.contatodo.shared.exceptions.AccessDeniedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JwtModulePermissionChecker}.
 */
@ExtendWith(MockitoExtension.class)
class JwtModulePermissionCheckerTest {

    private static final String USERS_OID = "users-oid";
    private static final String SALES_OID = "sales-oid";

    @Mock
    private JwtService jwtService;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CompanyContextProvider companyContextProvider;

    private JwtModulePermissionChecker checker;

    @BeforeEach
    void setUp() {
        checker = new JwtModulePermissionChecker(jwtService, moduleRepository, companyContextProvider, new ObjectMapper());
    }

    private Module module(String id, String link) {
        return Module.builder().id(id).link(link).name("Module").isActive(true).build();
    }

    private void activeModules() {
        when(moduleRepository.findAllActive()).thenReturn(List.of(
                module(USERS_OID, ModuleConstants.USERS_LINK),
                module(SALES_OID, "/sales")
        ));
    }

    private RolePermissionResponse.Permissions flags(Boolean view, Boolean create, Boolean update, Boolean delete) {
        return new RolePermissionResponse.Permissions(create, update, delete, view);
    }

    private void claimOf(Object... permissions) {
        when(jwtService.getClaimValue(AuthConstants.JWT_CLAIM_PERMISSION_OF_ROLE))
                .thenReturn(Arrays.asList(permissions));
    }

    private Map<String, Object> entry(String moduleOid, RolePermissionResponse.Permissions permissions) {
        return Map.of(
                "moduleOid", moduleOid,
                "moduleName", "Usuarios",
                "permissions", Map.of(
                        "view", permissions.getView(),
                        "create", permissions.getCreate(),
                        "update", permissions.getUpdate(),
                        "delete", permissions.getDelete()
                )
        );
    }

    @Test
    void rootIsAllowedEveryActionWithoutConsultingTheClaim() {
        when(companyContextProvider.isRoot()).thenReturn(true);

        assertTrue(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.DELETE));
        verify(moduleRepository, never()).findAllActive();
    }

    @Test
    void anActionGrantedByTheClaimIsAllowed() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        claimOf(entry(USERS_OID, flags(true, true, false, false)));

        assertTrue(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW));
        assertTrue(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.CREATE));
        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.UPDATE));
        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.DELETE));
    }

    @Test
    void aPermissionOfAnotherModuleDoesNotGrantAccess() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        claimOf(entry(SALES_OID, flags(true, true, true, true)));

        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW));
    }

    @Test
    void aMissingFlagCountsAsNotGranted() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        claimOf(Map.of(
                "moduleOid", USERS_OID,
                "permissions", Map.of("view", true)
        ));

        assertTrue(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW));
        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.CREATE));
    }

    @Test
    void aMissingClaimGrantsNothing() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        when(jwtService.getClaimValue(AuthConstants.JWT_CLAIM_PERMISSION_OF_ROLE)).thenReturn(null);

        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW));
    }

    @Test
    void aMalformedClaimGrantsNothing() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        when(jwtService.getClaimValue(AuthConstants.JWT_CLAIM_PERMISSION_OF_ROLE))
                .thenReturn("not-a-list");

        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW));
    }

    @Test
    void anUnregisteredModuleLinkGrantsNothing() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        when(moduleRepository.findAllActive()).thenReturn(List.of(module(SALES_OID, "/sales")));

        assertFalse(checker.hasPermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW));
    }

    @Test
    void requirePermissionThrowsWhenTheActionIsNotGranted() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        claimOf(entry(USERS_OID, flags(true, false, false, false)));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> checker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.DELETE)
        );
        assertTrue(exception.getMessage().contains(AuthConstants.ACCESS_DENIED));
    }

    @Test
    void requirePermissionReturnsWithoutThrowingWhenTheActionIsGranted() {
        when(companyContextProvider.isRoot()).thenReturn(false);
        activeModules();
        claimOf(entry(USERS_OID, flags(true, false, false, true)));

        checker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.DELETE);
    }

    @Test
    void aBlankLinkIsRejectedWithoutQueryingTheModules() {
        when(companyContextProvider.isRoot()).thenReturn(false);

        assertFalse(checker.hasPermission("  ", ModulePermissionAction.VIEW));
        verify(moduleRepository, never()).findAllActive();
    }
}
