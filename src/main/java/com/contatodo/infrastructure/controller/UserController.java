package com.contatodo.infrastructure.controller;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.LoginResponse;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.application.services.UserService;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user endpoints.
 */
@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    /**
     * Creates a user controller.
     *
     * @param userService User service.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param request Create user request.
     * @return Created user response.
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(UserConstants.USER_CREATED, user));
    }

    /**
     * Updates an existing user.
     *
     * @param id User identifier.
     * @param request Update user request.
     * @return Updated user response.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request
    ) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(UserConstants.USER_UPDATED, user));
    }

    /**
     * Performs logical delete on a user.
     *
     * @param id User identifier.
     * @return Success response without data.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<List<Object>>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.successWithoutData(UserConstants.USER_DELETED));
    }

    /**
     * Retrieves all active users.
     *
     * @return List of users.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, users));
    }

    /**
     * Retrieves a user by email.
     *
     * @param email User email.
     * @return User response.
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, user));
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request Login request.
     * @return Login response with token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(AuthConstants.LOGIN_SUCCESS, loginResponse));
    }
}
