package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for user entities and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create user request.
     * @param hashedPassword Hashed password.
     * @return User entity.
     */
    public User toEntity(CreateUserRequest request, String hashedPassword) {
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setName(request.getName());
        user.setActive(true);
        user.setDelete(false);
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        return user;
    }

    /**
     * Applies update request changes to an existing user.
     *
     * @param user Existing user.
     * @param request Update user request.
     * @param hashedPassword Hashed password when provided.
     */
    public void applyUpdate(User user, UpdateUserRequest request, String hashedPassword) {
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (hashedPassword != null) {
            user.setPassword(hashedPassword);
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }
        user.setUpdatedDate(LocalDateTime.now());
    }

    /**
     * Maps a user entity to a response DTO.
     *
     * @param user User entity.
     * @return User response.
     */
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setActive(user.isActive());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());
        return response;
    }

    /**
     * Maps a list of users to response DTOs.
     *
     * @param users User entities.
     * @return User responses.
     */
    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }
}
