package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.response.LoginResponse;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.application.mapper.UserMapper;
import com.contatodo.application.port.TokenProvider;
import com.contatodo.application.validators.UserValidator;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.exceptions.AuthenticationException;
import com.contatodo.shared.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private UserValidator userValidator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userValidator, userMapper, passwordEncoder, tokenProvider);
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

    @Test
    void createUserRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("david@example.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(createRequest("david@example.com"))
        );
        assertEquals(UserConstants.USER_ALREADY_EXISTS, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserHashesPasswordBeforeSaving() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toEntity(any(), any())).thenReturn(activeUser());
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

        userService.createUser(createRequest("new@example.com"));

        verify(userMapper).toEntity(any(), any());
        verify(userRepository).save(any());
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(activeUser()));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(tokenProvider.generateToken("david@example.com")).thenReturn("jwt-token");
        when(userMapper.toResponse(any())).thenReturn(new UserResponse());

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
}
