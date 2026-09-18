package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.LoginResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.application.mapper.RoleMapper;
import com.contatodo.application.mapper.UserMapper;
import com.contatodo.application.port.TokenProvider;
import com.contatodo.application.validators.UserValidator;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.domain.repositories.UserRepository;
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
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * Creates a user service.
     *
     * @param userRepository User repository port.
     * @param roleRepository Role repository port.
     * @param userValidator User validator.
     * @param userMapper User mapper.
     * @param roleMapper Role mapper.
     * @param passwordEncoder Password encoder.
     * @param tokenProvider Security token provider.
     */
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserValidator userValidator,
            UserMapper userMapper,
            RoleMapper roleMapper,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
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
     * Retrieves all active users, resolving each related role.
     *
     * <p>Each role is looked up at most once. A role that cannot be resolved
     * (missing identifier, not found or lookup error) is left empty for that
     * user without failing the whole query.</p>
     *
     * @return List of user responses.
     */
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllActive();
        return userMapper.toResponseList(users, resolveRoles(users));
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
     * Retrieves a user by email.
     *
     * @param email User email.
     * @return User response.
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findActiveUserByEmail(email, false)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstants.USER_NOT_FOUND));
        return userMapper.toResponse(user);
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

        LoginResponse response = new LoginResponse();
        response.setToken(tokenProvider.generateToken(user.getEmail()));
        response.setUser(userMapper.toResponse(user));
        return response;
    }
}
