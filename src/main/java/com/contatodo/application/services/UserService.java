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
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.port.TokenProvider;
import com.contatodo.application.validators.UserValidator;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.AuthConstants;
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
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates a user service.
     *
     * @param userRepository User repository port.
     * @param roleRepository Role repository port.
     * @param companyRepository Company repository port.
     * @param userValidator User validator.
     * @param userMapper User mapper.
     * @param roleMapper Role mapper.
     * @param companyMapper Company mapper.
     * @param passwordEncoder Password encoder.
     * @param tokenProvider Security token provider.
     * @param companyContextProvider Company context provider.
     */
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CompanyRepository companyRepository,
            UserValidator userValidator,
            UserMapper userMapper,
            RoleMapper roleMapper,
            CompanyMapper companyMapper,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            CompanyContextProvider companyContextProvider
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.companyMapper = companyMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.companyContextProvider = companyContextProvider;
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
        String effectiveCompanyOid = resolveWritableCompanyOid(request.getCompanyOid());
        request.setCompanyOid(effectiveCompanyOid);
        if (effectiveCompanyOid != null && !effectiveCompanyOid.isEmpty()) {
            if (!companyRepository.findById(effectiveCompanyOid).isPresent()) {
                throw new ResourceNotFoundException("Company not found with id: " + effectiveCompanyOid);
            }
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        String roleId = request.getRoleId();
        String createdByUserOid = null;
        boolean isActive = true;

        if (sessionEmail.isPresent()) {
            Optional<User> sessionUser = userRepository.findActiveUserByEmail(sessionEmail.get(), false);
            if (sessionUser.isPresent()) {
                createdByUserOid = sessionUser.get().getId();
            } else {
                roleId = null;
                isActive = false;
            }
        } else {
            roleId = null;
            isActive = false;
        }

        User user = userMapper.toEntity(request, hashedPassword, roleId, createdByUserOid, isActive);
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

        User user = userRepository.findById(id)
                .filter(existingUser -> !existingUser.isDelete())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException(UserConstants.USER_ALREADY_EXISTS);
            }
        }

        // Validate companyOid if provided
        String effectiveCompanyOid = resolveWritableCompanyOid(request.getCompanyOid());
        request.setCompanyOid(effectiveCompanyOid);
        if (effectiveCompanyOid != null && !effectiveCompanyOid.isEmpty()) {
            if (!companyRepository.findById(effectiveCompanyOid).isPresent()) {
                throw new ResourceNotFoundException("Company not found with id: " + effectiveCompanyOid);
            }
        }

        String hashedPassword = request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null;

        User updatedUser = userRepository.save(userMapper.applyUpdate(user, request, hashedPassword));
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Performs logical delete on a user.
     *
     * @param id User identifier.
     */
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .filter(existingUser -> !existingUser.isDelete())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));

        userRepository.save(user.markDeleted());
    }

    /**
     * Retrieves all active users with their role and company resolved.
     *
     * @return List of user responses.
     */
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllActive();
        Map<String, RoleResponse> roles = resolveRoles(users);
        Map<String, CompanyResponse> companies = resolveCompanies(users);
        return userMapper.toResponseList(users, roles, companies);
    }

    /**
     * Resolves the company to persist for a user write.
     *
     * <p>The company selected in the request is respected so an operator can
     * assign any company. When the request carries no company, the company of
     * the current session is used as a sensible default; without an
     * authenticated company context (public registration) the requested value
     * is kept.</p>
     *
     * @param requestedCompanyOid Company requested in the payload.
     * @return Company identifier to persist.
     */
    private String resolveWritableCompanyOid(String requestedCompanyOid) {
        if (requestedCompanyOid != null && !requestedCompanyOid.isBlank()) {
            return requestedCompanyOid;
        }
        return companyContextProvider.currentCompanyOid()
                .map(CompanyOid::value)
                .orElse(requestedCompanyOid);
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
        if (roleResponse != null) {
            claims.put("roleId", roleResponse.getId());
            claims.put("roleName", roleResponse.getName());
            claims.put(AuthConstants.JWT_CLAIM_ROLE,
                    AuthConstants.ROOT_ROLE_NAME.equalsIgnoreCase(roleResponse.getName())
                            ? AuthConstants.ROOT_ROLE_CLAIM
                            : roleResponse.getName());

            if (roleResponse.getPermissions() != null) {
                claims.put("permissionOfRole", roleResponse.getPermissions());
            }
        }
        if (user.getCompanyOid() != null) {
            claims.put(AuthConstants.JWT_CLAIM_COMPANY_OID, user.getCompanyOid());
        }

        Map<String, CompanyResponse> companies = resolveCompanies(List.of(user));

        LoginResponse response = new LoginResponse();
        response.setToken(tokenProvider.generateToken(user.getEmail(), claims));
        response.setUser(userMapper.toResponse(user, roleResponse, companies.get(user.getCompanyOid())));
        return response;
    }
}