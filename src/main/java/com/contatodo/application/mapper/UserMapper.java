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
public class UserMapper implements ResponseMapper<User, UserResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create user request.
     * @param hashedPassword Hashed password.
     * @return User entity.
     */
    public User toEntity(CreateUserRequest request, String hashedPassword) {
        return User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(hashedPassword)
                .name(request.getName())
                .isActive(true)
                .isDelete(false)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    /**
     * Produces an updated copy of an existing user applying request changes.
     *
     * @param existing Current persisted user.
     * @param request Update user request.
     * @param hashedPassword Newly hashed password when provided; null keeps the current one.
     * @return New immutable user instance with the changes applied.
     */
    public User applyUpdate(User existing, UpdateUserRequest request, String hashedPassword) {
        return User.builder()
                .id(existing.getId())
                .userName(request.getUserName() != null ? request.getUserName() : existing.getUserName())
                .email(request.getEmail() != null ? request.getEmail() : existing.getEmail())
                .password(hashedPassword != null ? hashedPassword : existing.getPassword())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.isActive())
                .isDelete(existing.isDelete())
                .createdDate(existing.getCreatedDate())
                .updatedDate(LocalDateTime.now())
                .createdBy(existing.getCreatedBy())
                .updatedBy(existing.getUpdatedBy())
                .build();
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
}
