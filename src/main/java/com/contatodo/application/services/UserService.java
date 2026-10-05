package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.dto.response.LoginResponse;
import com.contatodo.application.dto.response.RolePermissionResponse;
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
import com.contatodo.shared.exceptions.AuthenticationException;
import com.contatodo.shared.exceptions.UserAlreadyExistsException;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service containing user business logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final CompanyOidValidator companyOidValidator;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CompanyContextProvider companyContextProvider;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CompanyService companyService;
    private final ModulePermissionChecker modulePermissionChecker;

    /**
     * Creates a user service.
     *
     * @param userRepository User repository port.
     * @param roleRepository Role repository port.
     * @param companyRepository Company repository port.
     * @param companyOidValidator Company identifier validator.
     * @param userValidator User validator.
     * @param userMapper User mapper.
     * @param roleMapper Role mapper.
     * @param companyMapper Company mapper.
     * @param passwordEncoder Password encoder.
     * @param tokenProvider Security token provider.
     * @param companyContextProvider Company context provider.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyService Company service.
     * @param modulePermissionChecker Module permission checker.
     */
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CompanyRepository companyRepository,
            CompanyOidValidator companyOidValidator,
            UserValidator userValidator,
            UserMapper userMapper,
            RoleMapper roleMapper,
            CompanyMapper companyMapper,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            CompanyContextProvider companyContextProvider,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyService companyService,
            ModulePermissionChecker modulePermissionChecker
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
        this.companyOidValidator = companyOidValidator;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.companyMapper = companyMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.companyContextProvider = companyContextProvider;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyService = companyService;
        this.modulePermissionChecker = modulePermissionChecker;
    }

    /**
     * Creates a new user.
     *
     * <p>When the optional session email is present, the creating user is
     * resolved from it and recorded as the creator of the new user. When the
     * session cannot be resolved, the user is created without a role and
     * inactive so a non-authenticated registration cannot grant access.</p>
     *
     * @param request Create user request.
     * @param sessionEmail Email of the user in session, or empty when not authenticated.
     * @return Created user response.
     */
    public UserResponse createUser(CreateUserRequest request, Optional<String> sessionEmail) {
        userValidator.validateCreateRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(UserConstants.USER_ALREADY_EXISTS);
        }

        // Validate companyOid if provided
        String effectiveCompanyOid = companyOidValidator.validate(resolveWritableCompanyOid(request.getCompanyOid()));
        request.setCompanyOid(effectiveCompanyOid);

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        String roleId = request.getRoleId();
        String byUserOid = null;
        boolean isActive = true;

        // An anonymous request reaches this endpoint because POST /users is
        // public; it must not be mistaken for a session with permissions.
        boolean hasSession = companyContextProvider.isAuthenticated() && sessionEmail.isPresent();

        if (hasSession) {
            modulePermissionChecker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.CREATE);

            Optional<User> sessionUser = userRepository.findActiveUserByEmail(sessionEmail.orElseThrow(), false);
            if (sessionUser.isPresent()) {
                byUserOid = sessionUser.get().getId();
            } else {
                roleId = null;
                isActive = false;
            }
        } else {
            // Public self-registration: no role and inactive, so the account
            // cannot sign in until a role with the create permission enables it.
            roleId = null;
            isActive = false;
        }

        User user = userMapper.toEntity(request, hashedPassword, roleId, byUserOid, isActive);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Updates an existing user.
     *
     * @param id User identifier.
     * @param request Update user request.
     * @return Updated user response.
     */
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        userValidator.validateUpdateRequest(request);
        modulePermissionChecker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.UPDATE);

        User user = userRepository.findById(id)
                .filter(existingUser -> !existingUser.isDelete())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException(UserConstants.USER_ALREADY_EXISTS);
            }
        }

        // Validate companyOid if provided
        String effectiveCompanyOid = companyOidValidator.validate(resolveWritableCompanyOid(request.getCompanyOid()));
        request.setCompanyOid(effectiveCompanyOid);

        String hashedPassword = request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null;

        String updatedByUserOid = resolveSessionUserOid();
        User updatedUser = userRepository.save(
                userMapper.applyUpdate(user, request, hashedPassword, updatedByUserOid)
        );
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Performs logical delete on a user.
     *
     * @param id User identifier.
     */
    public void deleteUser(String id) {
        modulePermissionChecker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.DELETE);

        User user = userRepository.findById(id)
                .filter(existingUser -> !existingUser.isDelete())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));

        userRepository.save(user.markDeleted());
    }

    /**
     * Retrieves all active users of a company with their role and company resolved.
     *
     * <p>The company of the session scopes the read. A root session falls back
     * to the requested company so an operator can list the users of any
     * company.</p>
     *
     * @param requestedCompanyOid Company requested by the caller.
     * @return List of user responses.
     */
    public List<UserResponse> getAllUsers(String requestedCompanyOid) {
        modulePermissionChecker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW);

        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);
        List<User> users = userRepository.findAllActive(companyOid);
        Map<String, RoleResponse> roles = resolveRoles(users);
        Map<String, CompanyResponse> companies = resolveCompanies(users);
        return userMapper.toResponseList(users, roles, companies);
    }

    /**
     * Retrieves the users that can be picked as the contact of a company.
     *
     * <p>The read is not company scoped on purpose: a company contact may belong
     * to any company and a company created now has no users yet, so a root
     * session must see every active user. A company session keeps seeing only
     * its own users.</p>
     *
     * @return List of user responses.
     */
    public List<UserResponse> getContactCandidates() {
        modulePermissionChecker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW);

        List<User> users = userRepository.findAllActiveInSessionScope();
        Map<String, RoleResponse> roles = resolveRoles(users);
        Map<String, CompanyResponse> companies = resolveCompanies(users);
        return userMapper.toResponseList(users, roles, companies);
    }

    /**
     * Resolves the identifier of the user in session to record as the last updater.
     *
     * <p>Returns {@code null} when there is no resolvable session user so an
     * update never fails solely because of the missing audit attribution.</p>
     *
     * @return Session user identifier, or null when it cannot be resolved.
     */
    private String resolveSessionUserOid() {
        try {
            return authenticatedUserProvider.getCurrentUserOid();
        } catch (ResourceNotFoundException exception) {
            return null;
        }
    }

    /**
     * Resolves the company to persist for a user write.
     *
     * <p>The company of the session wins, then the company carried by the
     * payload. The company is optional: when neither is present the user is
     * persisted without company, exactly like the product writes.</p>
     *
     * @param requestedCompanyOid Company requested in the payload.
     * @return Company identifier to persist, or {@code null} when absent.
     */
    private String resolveWritableCompanyOid(String requestedCompanyOid) {
        CompanyOid companyOid = companyService.resolveCompanyOid(requestedCompanyOid);
        return companyOid != null ? companyOid.value() : null;
    }

    /**
     * Resolves the roles referenced by the given users, keyed by role identifier.
     *
     * @param users Users whose roles must be resolved.
     * @return Map of role identifiers to resolved role responses.
     */
    private Map<String, RoleResponse> resolveRoles(List<User> users) {
        Map<String, RoleResponse> roles = new HashMap<>();
        for (User user : users) {
            String roleId = user.getRoleId();
            if (roleId == null || roles.containsKey(roleId)) {
                continue;
            }
            try {
                roleRepository.findById(roleId)
                        .ifPresent(role -> roles.put(roleId, roleMapper.toResponse(role)));
            } catch (RuntimeException exception) {
                // A single broken role must not prevent the remaining users from loading.
            }
        }
        return roles;
    }

    /**
     * Resolves the companies referenced by the given users, keyed by company identifier.
     *
     * @param users Users whose companies must be resolved.
     * @return Map of company identifiers to resolved company responses.
     */
    private Map<String, CompanyResponse> resolveCompanies(List<User> users) {
        Map<String, CompanyResponse> companies = new HashMap<>();
        for (User user : users) {
            String companyOid = user.getCompanyOid();
            if (companyOid == null || companies.containsKey(companyOid)) {
                continue;
            }
            try {
                companyRepository.findById(companyOid)
                        .ifPresent(company -> companies.put(companyOid, companyMapper.toResponse(company)));
            } catch (RuntimeException exception) {
                // A single broken company must not prevent the remaining users from loading.
            }
        }
        return companies;
    }

    /**
     * Retrieves a user by email.
     *
     * @param email User email.
     * @return User response.
     */
    public UserResponse getUserByEmail(String email) {
        modulePermissionChecker.requirePermission(ModuleConstants.USERS_LINK, ModulePermissionAction.VIEW);

        User user = userRepository.findActiveUserByEmail(email, false)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));
        Map<String, RoleResponse> roles = resolveRoles(List.of(user));
        Map<String, CompanyResponse> companies = resolveCompanies(List.of(user));
        return userMapper.toResponse(user, roles.get(user.getRoleId()), companies.get(user.getCompanyOid()));
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request Login request.
     * @return Login response with token.
     */
    public LoginResponse login(LoginRequest request) {
        userValidator.validateLoginRequest(request);

        User user = userRepository.findByEmail(request.getEmail())
                .filter(existingUser -> !existingUser.isDelete())
                .orElseThrow(() -> new AuthenticationException(UserConstants.USER_INVALID_CREDENTIALS));

        if (!user.isActive()) {
            throw new AuthenticationException(UserConstants.USER_INACTIVE);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException(UserConstants.USER_INVALID_CREDENTIALS);
        }

        // Fetch user's role with permissions
        Map<String, RoleResponse> roles = resolveRoles(List.of(user));
        RoleResponse roleResponse = user.getRoleId() != null ? roles.get(user.getRoleId()) : null;

        // Build token claims with role info and permissions
        Map<String, Object> claims = new HashMap<>();
        boolean rootSession = false;
        if (roleResponse != null) {
            rootSession = AuthConstants.ROOT_ROLE_NAME.equalsIgnoreCase(roleResponse.getName());
            claims.put("roleId", roleResponse.getId());
            claims.put("roleName", roleResponse.getName());
            claims.put(AuthConstants.JWT_CLAIM_ROLE,
                    rootSession ? AuthConstants.ROOT_ROLE_CLAIM : roleResponse.getName());

            if (roleResponse.getPermissions() != null) {
                claims.put(AuthConstants.JWT_CLAIM_PERMISSION_OF_ROLE, roleResponse.getPermissions());
            }
        }

        // A root session is not scoped to the company stored on its user
        // document: it picks the company to work on, and every company scoped
        // query resolves it from the request when the claim is absent. Emitting
        // the claim here would win over that choice and pin the session to the
        // home company, leaving the company selector without any products to
        // show. Every other session stays scoped to its own company.
        if (user.getCompanyOid() != null && !rootSession) {
            claims.put(AuthConstants.JWT_CLAIM_COMPANY_OID, user.getCompanyOid());
        }

        Map<String, CompanyResponse> companies = resolveCompanies(List.of(user));

        LoginResponse response = new LoginResponse();
        response.setToken(tokenProvider.generateToken(user.getEmail(), claims));
        response.setUser(userMapper.toResponse(user, roleResponse, companies.get(user.getCompanyOid())));
        return response;
    }
}