package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.LoginResponse;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.application.mapper.UserMapper;
import com.contatodo.application.validators.UserValidator;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.infrastructure.security.JwtService;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.exceptions.AuthenticationException;
import com.contatodo.shared.exceptions.UserAlreadyExistsException;
import com.contatodo.shared.exceptions.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service containing user business logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Creates a user service.
     *
     * @param userRepository User repository port.
     * @param userValidator User validator.
     * @param userMapper User mapper.
     * @param passwordEncoder Password encoder.
     * @param jwtService JWT service.
     */
    public UserService(
            UserRepository userRepository,
            UserValidator userValidator,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Creates a new user.
     *
     * @param request Create user request.
     * @return Created user response.
     */
    public UserResponse createUser(CreateUserRequest request) {
        userValidator.validateCreateRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(UserConstants.USER_ALREADY_EXISTS);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, hashedPassword);
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
                .orElseThrow(() -> new UserNotFoundException(UserConstants.USER_NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException(UserConstants.USER_ALREADY_EXISTS);
            }
        }

        String hashedPassword = request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null;

        userMapper.applyUpdate(user, request, hashedPassword);
        User updatedUser = userRepository.save(user);
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
                .orElseThrow(() -> new UserNotFoundException(UserConstants.USER_NOT_FOUND));

        user.setDelete(true);
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Retrieves all active users.
     *
     * @return List of user responses.
     */
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAllActive());
    }

    /**
     * Retrieves a user by email.
     *
     * @param email User email.
     * @return User response.
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.isDelete())
                .orElseThrow(() -> new UserNotFoundException(UserConstants.USER_NOT_FOUND));
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
        response.setToken(jwtService.generateToken(user.getEmail()));
        response.setUser(userMapper.toResponse(user));
        return response;
    }
}
