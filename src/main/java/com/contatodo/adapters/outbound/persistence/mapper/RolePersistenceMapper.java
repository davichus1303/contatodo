package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.RoleDocument;
import com.contatodo.adapters.outbound.persistence.document.RolePermissionDocument;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.entities.RolePermission;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link Role} domain entities and MongoDB role documents.
 */
@Component
public class RolePersistenceMapper {

    /**
     * Maps a role entity to a document.
     *
     * @param role Role entity.
     * @return Role document.
     */
    public RoleDocument toDocument(Role role) {
        RoleDocument document = new RoleDocument();
        document.setId(role.getId());
        document.setName(role.getName());
        document.setIsDeleted(role.getIsDeleted());
        document.setIsActive(role.getIsActive());
        document.setCreatedDate(role.getCreatedDate());
        document.setUpdatedDate(role.getUpdatedDate());
        document.setCreatedBy(role.getCreatedBy());

        List<RolePermissionDocument> permissionDocuments = role.getPermissions().stream()
                .map(RolePersistenceMapper::toPermissionDocument)
                .toList();
        document.setPermissions(permissionDocuments);

        return document;
    }

    /**
     * Maps a role document to an entity.
     *
     * @param document Role document.
     * @return Role entity.
     */
    public Role toEntity(RoleDocument document) {
        List<RolePermission> permissions = document.getPermissions().stream()
                .map(RolePersistenceMapper::toPermission)
                .toList();

        return Role.builder()
                .id(document.getId())
                .name(document.getName())
                .permissions(permissions)
                .isDeleted(document.getIsDeleted())
                .isActive(document.getIsActive())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .createdBy(document.getCreatedBy())
                .build();
    }

    /**
     * Maps a list of role documents to entities.
     *
     * @param documents Role documents.
     * @return Role entities.
     */
    public List<Role> toEntityList(List<RoleDocument> documents) {
        return documents.stream().map(this::toEntity).toList();
    }

    /**
     * Maps a permission entity to its document form.
     *
     * @param permission Permission entity.
     * @return Permission document.
     */
    private static RolePermissionDocument toPermissionDocument(RolePermission permission) {
        RolePermissionDocument document = new RolePermissionDocument();
        document.setModuleOid(permission.getModuleOid());

        RolePermissionDocument.PermissionDetailsDocument details =
                new RolePermissionDocument.PermissionDetailsDocument();
        details.setCreate(permission.getPermissions().getCreate());
        details.setUpdate(permission.getPermissions().getUpdate());
        details.setDelete(permission.getPermissions().getDelete());
        details.setView(permission.getPermissions().getView());
        document.setPermissions(details);

        return document;
    }

    /**
     * Maps a permission document to its entity form.
     *
     * @param document Permission document.
     * @return Permission entity.
     */
    private static RolePermission toPermission(RolePermissionDocument document) {
        RolePermission.PermissionDetails details = RolePermission.PermissionDetails.builder()
                .create(document.getPermissions().getCreate())
                .update(document.getPermissions().getUpdate())
                .delete(document.getPermissions().getDelete())
                .view(document.getPermissions().getView())
                .build();

        return RolePermission.builder()
                .moduleOid(document.getModuleOid())
                .permissions(details)
                .build();
    }
}
