package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * @param roleId Role assigned to the new user.
     * @param createdByUserOid Identifier of the user that creates it.
     * @param isActive Active flag.
     * @return User entity.
     */
    public User toEntity(CreateUserRequest request, String hashedPassword, String roleId, String createdByUserOid, boolean isActive) {
        return User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(hashedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .roleId(request.getRoleId())
                .companyOid(request.getCompanyOid())
                .createdByUserOid(request.getCreatedByUserOid())
                .isActive(isActive)
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
                .phoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : existing.getPhoneNumber())
                .roleId(request.getRoleId() != null ? request.getRoleId() : existing.getRoleId())
                .companyOid(request.getCompanyOid() != null ? request.getCompanyOid() : existing.getCompanyOid())
                .createdByUserOid(existing.getCreatedByUserOid())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.isActive())
                .isDelete(existing.isDelete())
                .createdDate(existing.getCreatedDate())
                .updatedDate(LocalDateTime.now())
                .createdBy(existing.getCreatedBy())
                .updatedBy(existing.getUpdatedBy())
                .build();
    }

    /**
     * Maps a user entity to a response DTO (full, used for login/single user).
     *
     * @param user User entity.
     * @param role Resolved role, or null when the user has no role or it could not be resolved.
     * @param company Resolved company, or null when the user has no company or it could not be resolved.
     * @return User response.
     */
    public UserResponse toResponse(User user, RoleResponse role, CompanyResponse company) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(role);
        response.setCompany(company);
        response.setCompanyOid(user.getCompanyOid());
        response.setCreatedByUserOid(user.getCreatedByUserOid());
        response.setActive(user.isActive());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());
        return response;
    }

    /**
     * Maps a user entity to a list response DTO (minimal, used for user lists).
     *
     * @param user User entity.
     * @return User response with only roleId/companyOid, no nested objects.
     */
    public UserResponse toListResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCompanyOid(user.getCompanyOid());
        response.setCreatedByUserOid(user.getCreatedByUserOid());
        response.setActive(user.isActive());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());
        return response;
    }

    /**
     * Maps a list of user entities to response DTOs (minimal, for listings).
     *
     * @param users User entities.
     * @return User responses without nested role/company objects.
     */
    public List<UserResponse> toListResponseList(List<User> users) {
        return users.stream()
                .map(this::toListResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     * Returns minimal user response for interface compatibility.
     */
    @Override
    public UserResponse toResponse(User user) {
        return toListResponse(user);
    }

    /**
     * Maps a list of user entities to response DTOs resolving each role and company from maps.
     * Used for single-user responses (login, get by email).
     *
     * <p>A user whose role or company is missing from the map is still mapped, leaving its
     * role/company empty, so a single unresolved reference never drops the
     * user from the list.</p>
     *
     * @param users User entities.
     * @param roles Map of role identifiers to resolved roles.
     * @param companies Map of company identifiers to resolved companies.
     * @return User responses.
     */
    public List<UserResponse> toResponseList(List<User> users, Map<String, RoleResponse> roles, Map<String, CompanyResponse> companies) {
        return users.stream()
                .map(user -> toResponse(
                        user,
                        user.getRoleId() == null ? null : roles.get(user.getRoleId()),
                        user.getCompanyOid() == null ? null : companies.get(user.getCompanyOid())
                ))
                .toList();
    }
}