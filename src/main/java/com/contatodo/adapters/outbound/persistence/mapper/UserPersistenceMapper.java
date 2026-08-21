package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.UserDocument;
import com.contatodo.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link User} domain entities and MongoDB user documents.
 */
@Component
public class UserPersistenceMapper {

    /**
     * Maps a user entity to a user document.
     *
     * @param user User entity.
     * @return User document.
     */
    public UserDocument toDocument(User user) {
        UserDocument document = new UserDocument();
        document.setId(user.getId());
        document.setUserName(user.getUserName());
        document.setEmail(user.getEmail());
        document.setPassword(user.getPassword());
        document.setName(user.getName());
        document.setActive(user.isActive());
        document.setDelete(user.isDelete());
        document.setCreatedDate(user.getCreatedDate());
        document.setUpdatedDate(user.getUpdatedDate());
        document.setCreatedBy(user.getCreatedBy());
        document.setUpdatedBy(user.getUpdatedBy());
        return document;
    }

    /**
     * Maps a user document to a user entity.
     *
     * @param document User document.
     * @return User entity.
     */
    public User toEntity(UserDocument document) {
        return User.builder()
                .id(document.getId())
                .userName(document.getUserName())
                .email(document.getEmail())
                .password(document.getPassword())
                .name(document.getName())
                .isActive(document.isActive())
                .isDelete(document.isDelete())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .createdBy(document.getCreatedBy())
                .updatedBy(document.getUpdatedBy())
                .build();
    }

    /**
     * Maps a list of user documents to user entities.
     *
     * @param documents User documents.
     * @return User entities.
     */
    public List<User> toEntityList(List<UserDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }
}
