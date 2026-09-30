package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.dto.response.LoginResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.application.mapper.CompanyMapper;
import com.contatodo.application.mapper.RoleMapper;
import com.contatodo.application.mapper.UserMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.port.ModulePermissionChecker;
import com.contatodo.application.validators.CompanyOidValidator;
import com.contatodo.application.port.TokenProvider;
import com.contatodo.application.validators.UserValidator;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.model.ModulePermissionAction;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.ModuleConstants;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.exceptions.AccessDeniedException;
import com.contatodo.shared.exceptions.AuthenticationException;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.shared.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserService} covering registration and login rules.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private CompanyContextProvider companyContextProvider;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private CompanyService companyService;

    @Mock
    private ModulePermissionChecker modulePermissionChecker;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository, roleRepository, companyRepository, new CompanyOidValidator(companyRepository), userValidator, userMapper, roleMapper, companyMapper, passwordEncoder, tokenProvider, companyContextProvider, authenticatedUserProvider, companyService, modulePermissionChecker);
    }

    private CreateUserRequest createRequest(String email) {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("david");
        request.setEmail(email);
        request.setPassword("secret123");
        request.setName("David");
        return request;
    }

    private User activeUser() {
        return User.builder()
                .id("user-1")
                .userName("david")
                .email("david@example.com")
                .password("hashed")
                .name("David")
                .build();
    }

    private User activeUserWithRole(String roleId) {
        return User.builder()
                .id("user-" + roleId)
                .userName("david-" + roleId)
                .email("david-" + roleId + "@example.com")
                .password("hashed")
                .name("David " + roleId)
                .roleId(roleId)
                .build();
    }

    private User activeUserWithRoleAndCompany(String roleId, String companyOid) {
        return User.builder()
                .id("user-" + roleId)
                .userName("david-" + roleId)
                .email("david-" + roleId + "@example.com")
                .password("hashed")
                .name("David " + roleId)
                .roleId(roleId)
                .companyOid(companyOid)
                .build();
    }

    private Role role(String id, String name) {
        return Role.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("david@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(createRequest("david@example.com"), Optional.empty())
        );
        assertEquals(UserConstants.USER_ALREADY_EXISTS, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserHashesPasswordBeforeSaving() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(createRequest("new@example.com"), Optional.empty());

        verify(userMapper).toEntity(any(), any(), any(), any(), anyBoolean());
        verify(userRepository).save(any());
    }

    @Test
    void createUserRecordsSessionUserAsCreatorAndKeepsTheRequestedRoleAndActiveFlag() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setRoleId("role-1");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyContextProvider.isAuthenticated()).thenReturn(true);
        when(userRepository.findActiveUserByEmail("david@example.com", false))
                .thenReturn(Optional.of(activeUser()));
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.of("david@example.com"));

        ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> creatorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(userMapper).toEntity(any(), any(), roleCaptor.capture(), creatorCaptor.capture(), activeCaptor.capture());
        assertEquals("role-1", roleCaptor.getValue());
        assertEquals("user-1", creatorCaptor.getValue());
        assertEquals(Boolean.TRUE, activeCaptor.getValue());
    }

    @Test
    void createUserWithoutSessionCreatesTheUserWithoutRoleAndInactive() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setRoleId("role-1");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.empty());

        ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> creatorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(userMapper).toEntity(any(), any(), roleCaptor.capture(), creatorCaptor.capture(), activeCaptor.capture());
        assertEquals(null, roleCaptor.getValue());
        assertEquals(null, creatorCaptor.getValue());
        assertEquals(Boolean.FALSE, activeCaptor.getValue());
    }

    @Test
    void createUserWithUnknownSessionEmailCreatesTheUserWithoutRoleAndInactive() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setRoleId("role-1");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyContextProvider.isAuthenticated()).thenReturn(true);
        when(userRepository.findActiveUserByEmail("unknown@example.com", false)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.of("unknown@example.com"));

        ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> creatorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(userMapper).toEntity(any(), any(), roleCaptor.capture(), creatorCaptor.capture(), activeCaptor.capture());
        assertEquals(null, roleCaptor.getValue());
        assertEquals(null, creatorCaptor.getValue());
        assertEquals(Boolean.FALSE, activeCaptor.getValue());
    }

    @Test
    void createUserRejectsAnInactiveCompany() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setCompanyOid("inactive-company");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyService.resolveCompanyOid("inactive-company"))
                .thenReturn(CompanyOid.of("inactive-company"));
        when(companyRepository.findById("inactive-company")).thenReturn(Optional.of(
                Company.builder().id("inactive-company").name("Dormant").isActive(false).isDeleted(false).build()
        ));

        assertThrows(ResourceNotFoundException.class,
                () -> userService.createUser(request, Optional.empty()));
    }

    @Test
    void updateUserRejectsAnInactiveCompany() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCompanyOid("inactive-company");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(activeUser()));
        when(companyService.resolveCompanyOid("inactive-company"))
                .thenReturn(CompanyOid.of("inactive-company"));
        when(companyRepository.findById("inactive-company")).thenReturn(Optional.of(
                Company.builder().id("inactive-company").name("Dormant").isActive(false).isDeleted(false).build()
        ));

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser("user-1", request));
    }

    @Test
    void createUserRejectsADeletedCompany() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setCompanyOid("deleted-company");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyService.resolveCompanyOid("deleted-company"))
                .thenReturn(CompanyOid.of("deleted-company"));
        when(companyRepository.findById("deleted-company")).thenReturn(Optional.of(
                Company.builder().id("deleted-company").name("Gone").isActive(true).isDeleted(true).build()
        ));

        assertThrows(ResourceNotFoundException.class,
                () -> userService.createUser(request, Optional.empty()));
    }

    @Test
    void createUserRejectsInvalidCompanyOid() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setCompanyOid("invalid-company");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyService.resolveCompanyOid("invalid-company"))
                .thenReturn(CompanyOid.of("invalid-company"));
        when(companyRepository.findById("invalid-company")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.createUser(request, Optional.empty()));
    }

    @Test
    void createUserAcceptsValidCompanyOid() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setCompanyOid("valid-company");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyService.resolveCompanyOid("valid-company"))
                .thenReturn(CompanyOid.of("valid-company"));
        when(companyRepository.findById("valid-company")).thenReturn(Optional.of(
                Company.builder().id("valid-company").name("Test Company").isActive(true).isDeleted(false).build()
        ));
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.empty());

        verify(userRepository).save(any());
    }

    @Test
    void createUserKeepsTheSessionCompanyEvenWhenTheRequestCarriesAnother() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setCompanyOid("other-company");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyContextProvider.isAuthenticated()).thenReturn(true);
        when(userRepository.findActiveUserByEmail("david@example.com", false))
                .thenReturn(Optional.of(activeUser()));
        when(companyService.resolveCompanyOid("other-company"))
                .thenReturn(CompanyOid.of("session-company"));
        when(companyRepository.findById("session-company")).thenReturn(Optional.of(
                Company.builder().id("session-company").name("Session Company").isActive(true).isDeleted(false).build()
        ));
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.of("david@example.com"));

        ArgumentCaptor<CreateUserRequest> requestCaptor = ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userMapper).toEntity(requestCaptor.capture(), any(), any(), any(), anyBoolean());
        assertEquals("session-company", requestCaptor.getValue().getCompanyOid());
        verify(companyService).resolveCompanyOid("other-company");
    }

    @Test
    void createUserFallsBackToSessionCompanyWhenNoneRequested() {
        CreateUserRequest request = createRequest("new@example.com");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyContextProvider.isAuthenticated()).thenReturn(true);
        when(userRepository.findActiveUserByEmail("david@example.com", false))
                .thenReturn(Optional.of(activeUser()));
        when(companyService.resolveCompanyOid(null)).thenReturn(CompanyOid.of("company-1"));
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(
                Company.builder().id("company-1").name("Test Company").isActive(true).isDeleted(false).build()
        ));
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.of("david@example.com"));

        ArgumentCaptor<CreateUserRequest> requestCaptor = ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userMapper).toEntity(requestCaptor.capture(), any(), any(), any(), anyBoolean());
        assertEquals("company-1", requestCaptor.getValue().getCompanyOid());
    }

    @Test
    void createUserAsRootKeepsRequestedCompany() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setCompanyOid("company-2");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyService.resolveCompanyOid("company-2")).thenReturn(CompanyOid.of("company-2"));
        when(companyRepository.findById("company-2")).thenReturn(Optional.of(
                Company.builder().id("company-2").name("Test Company").isActive(true).isDeleted(false).build()
        ));
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.empty());

        ArgumentCaptor<CreateUserRequest> requestCaptor = ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userMapper).toEntity(requestCaptor.capture(), any(), any(), any(), anyBoolean());
        assertEquals("company-2", requestCaptor.getValue().getCompanyOid());
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(activeUser()));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(tokenProvider.generateToken(eq("david@example.com"), any())).thenReturn("jwt-token");
        when(userMapper.toResponse(any(), any(), any())).thenReturn(new UserResponse());

        LoginResponse response = userService.login(request);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginRejectsUnknownEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(request)
        );
        assertEquals(UserConstants.USER_INVALID_CREDENTIALS, exception.getMessage());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("wrong");

        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(activeUser()));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(request)
        );
        assertEquals(UserConstants.USER_INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void loginRejectsDeletedUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("david@example.com"))
                .thenReturn(Optional.of(activeUser().markDeleted()));

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(request)
        );
        assertEquals(UserConstants.USER_INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void loginRejectsInactiveUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("secret123");

        User inactive = User.builder()
                .id("user-1")
                .userName("david")
                .email("david@example.com")
                .password("hashed")
                .name("David")
                .isActive(false)
                .build();
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(inactive));

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.login(request)
        );
        assertEquals(UserConstants.USER_INACTIVE, exception.getMessage());
    }

    @Test
    void getAllUsersScopesTheReadToTheResolvedCompany() {
        when(companyService.resolveReadCompanyOid("company-2")).thenReturn(CompanyOid.of("company-2"));
        when(userRepository.findAllActive(CompanyOid.of("company-2"))).thenReturn(List.of());
        when(userMapper.toResponseList(anyList(), anyMap(), anyMap())).thenReturn(List.of());

        List<UserResponse> response = userService.getAllUsers("company-2");

        assertEquals(0, response.size());
        verify(userRepository).findAllActive(CompanyOid.of("company-2"));
    }

    @Test
    void getAllUsersRejectsReadsWithoutCompanyContext() {
        when(companyService.resolveReadCompanyOid(null))
                .thenThrow(new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getAllUsers(null)
        );
        assertEquals(AuthConstants.COMPANY_CONTEXT_REQUIRED, exception.getMessage());
        verify(userRepository, never()).findAllActive(any());
    }

    @Test
    void getContactCandidatesIgnoresTheRequestedCompanyScope() {
        User user = activeUserWithRoleAndCompany("role-1", "company-1");

        when(userRepository.findAllActiveInSessionScope()).thenReturn(List.of(user));
        when(roleRepository.findById("role-1")).thenReturn(Optional.of(role("role-1", "Admin")));
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(
                Company.builder().id("company-1").name("VichoBox").isActive(true).isDeleted(false).build()
        ));
        when(userMapper.toResponseList(anyList(), anyMap(), anyMap())).thenReturn(List.of(new UserResponse()));

        List<UserResponse> response = userService.getContactCandidates();

        assertEquals(1, response.size());
        verify(userRepository).findAllActiveInSessionScope();
        verify(userRepository, never()).findAllActive(any());
    }

    @Test
    void getAllUsersResolvesRoleAndCompany() {
        User user = activeUserWithRoleAndCompany("role-1", "company-1");

        when(companyService.resolveReadCompanyOid("company-1")).thenReturn(CompanyOid.of("company-1"));
        when(userRepository.findAllActive(CompanyOid.of("company-1"))).thenReturn(List.of(user));
        when(roleRepository.findById("role-1")).thenReturn(Optional.of(role("role-1", "Admin")));
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(
                Company.builder().id("company-1").name("VichoBox").isActive(true).isDeleted(false).build()
        ));
        when(userMapper.toResponseList(anyList(), anyMap(), anyMap())).thenReturn(List.of(new UserResponse()));

        List<UserResponse> response = userService.getAllUsers("company-1");

        assertEquals(1, response.size());
        verify(userRepository).findAllActive(CompanyOid.of("company-1"));
        verify(roleRepository).findById("role-1");
        verify(companyRepository).findById("company-1");
        verify(userMapper).toResponseList(anyList(), anyMap(), anyMap());
    }

    @Test
    void getAllUsersReturnsUsersWhenRoleLookupFails() {
        User user = activeUserWithRoleAndCompany("role-1", "company-1");

        when(companyService.resolveReadCompanyOid("company-1")).thenReturn(CompanyOid.of("company-1"));
        when(userRepository.findAllActive(CompanyOid.of("company-1"))).thenReturn(List.of(user));
        when(roleRepository.findById("role-1")).thenThrow(new RuntimeException("boom"));
        when(userMapper.toResponseList(anyList(), anyMap(), anyMap())).thenReturn(List.of(new UserResponse()));

        List<UserResponse> response = userService.getAllUsers("company-1");

        assertEquals(1, response.size());
        verify(userMapper).toResponseList(anyList(), anyMap(), anyMap());
    }

    @Test
    void getAllUsersReturnsUsersWhenCompanyLookupFails() {
        User user = activeUserWithRoleAndCompany("role-1", "company-1");

        when(companyService.resolveReadCompanyOid("company-1")).thenReturn(CompanyOid.of("company-1"));
        when(userRepository.findAllActive(CompanyOid.of("company-1"))).thenReturn(List.of(user));
        when(companyRepository.findById("company-1")).thenThrow(new RuntimeException("boom"));
        when(userMapper.toResponseList(anyList(), anyMap(), anyMap())).thenReturn(List.of(new UserResponse()));

        List<UserResponse> response = userService.getAllUsers("company-1");

        assertEquals(1, response.size());
        verify(userMapper).toResponseList(anyList(), anyMap(), anyMap());
    }

    @Test
    void createUserWithoutTheCreatePermissionIsRejected() {
        CreateUserRequest request = createRequest("new@example.com");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyContextProvider.isAuthenticated()).thenReturn(true);
        org.mockito.Mockito.doThrow(new AccessDeniedException(AuthConstants.ACCESS_DENIED))
                .when(modulePermissionChecker)
                .requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.CREATE);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> userService.createUser(request, Optional.of("david@example.com"))
        );
        assertEquals(AuthConstants.ACCESS_DENIED, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserTreatsAnAnonymousAuthenticationAsAPublicRegistration() {
        CreateUserRequest request = createRequest("new@example.com");
        request.setRoleId("role-1");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyContextProvider.isAuthenticated()).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request, Optional.of("anonymousUser"));

        ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> creatorCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(userMapper).toEntity(any(), any(), roleCaptor.capture(), creatorCaptor.capture(), activeCaptor.capture());
        assertEquals(null, roleCaptor.getValue());
        assertEquals(null, creatorCaptor.getValue());
        assertEquals(Boolean.FALSE, activeCaptor.getValue());
        verify(modulePermissionChecker, never())
                .requirePermission(any(), any());
        verify(userRepository, never()).findActiveUserByEmail(any(), anyBoolean());
    }

    @Test
    void getAllUsersRequiresTheViewPermission() {
        doThrow(new AccessDeniedException(AuthConstants.ACCESS_DENIED))
                .when(modulePermissionChecker)
                .requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW);

        assertThrows(AccessDeniedException.class, () -> userService.getAllUsers("company-1"));
        verify(companyService, never()).resolveReadCompanyOid(any());
    }

    @Test
    void updateUserRequiresTheUpdatePermission() {
        UpdateUserRequest request = new UpdateUserRequest();
        doThrow(new AccessDeniedException(AuthConstants.ACCESS_DENIED))
                .when(modulePermissionChecker)
                .requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.UPDATE);

        assertThrows(AccessDeniedException.class, () -> userService.updateUser("user-1", request));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void deleteUserRequiresTheDeletePermission() {
        doThrow(new AccessDeniedException(AuthConstants.ACCESS_DENIED))
                .when(modulePermissionChecker)
                .requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.DELETE);

        assertThrows(AccessDeniedException.class, () -> userService.deleteUser("user-1"));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUserByEmailRequiresTheViewPermission() {
        doThrow(new AccessDeniedException(AuthConstants.ACCESS_DENIED))
                .when(modulePermissionChecker)
                .requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW);

        assertThrows(AccessDeniedException.class, () -> userService.getUserByEmail("david@example.com"));
        verify(userRepository, never()).findByEmail(any());
    }
}
