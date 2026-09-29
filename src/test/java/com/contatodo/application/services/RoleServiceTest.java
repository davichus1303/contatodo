package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.mapper.RoleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RoleService} audit attribution rules.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleService(roleRepository, roleMapper, authenticatedUserProvider);
    }

    private Role existingRole() {
        return Role.builder()
                .id("role-1")
                .name("Administrator")
                .byUserOid("creator-1")
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void createRoleAssignsTheAuthenticatedUserAsCreator() {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("Administrator");
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("creator-1");
        when(roleMapper.toEntity(request, "creator-1")).thenReturn(existingRole());
        when(roleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleMapper.toResponse(any())).thenReturn(new RoleResponse());

        roleService.createRole(request);

        verify(roleMapper).toEntity(request, "creator-1");
    }

    @Test
    void updateRolePassesTheAuthenticatedUserAsEditor() {
        when(roleRepository.findById("role-1")).thenReturn(Optional.of(existingRole()));
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("editor-1");
        when(roleMapper.updateEntity(any(), any(), eq("editor-1"))).thenReturn(existingRole());
        when(roleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleMapper.toResponse(any())).thenReturn(new RoleResponse());

        roleService.updateRole("role-1", new UpdateRoleRequest());

        verify(roleMapper).updateEntity(any(), any(), eq("editor-1"));
    }

    @Test
    void updateRoleRejectsAnUnknownRole() {
        when(roleRepository.findById("role-404")).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> roleService.updateRole("role-404", new UpdateRoleRequest())
        );

        assertEquals("Role not found", exception.getMessage());
    }
}
