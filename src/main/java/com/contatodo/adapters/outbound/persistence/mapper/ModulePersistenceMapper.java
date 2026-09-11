package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.ModuleDocument;
import com.contatodo.domain.entities.Module;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between {@link Module} domain entities and MongoDB module documents.
 */
@Component
public class ModulePersistenceMapper implements PersistenceMapper<ModuleDocument, Module> {

    /**
     * Maps a module entity to a module document.
     *
     * @param module Module entity.
     * @return Module document.
     */
    public ModuleDocument toDocument(Module module) {
        ModuleDocument document = new ModuleDocument();
        document.setId(module.getId());
        document.setName(module.getName());
        document.setLink(module.getLink());
        document.setIsActive(module.getIsActive());
        document.setIsDelete(module.getIsDeleted());
        document.setCreatedDate(module.getCreatedDate());
        document.setUpdatedDate(module.getUpdatedDate());
        return document;
    }

    /**
     * Maps a module document to a module entity.
     *
     * @param document Module document.
     * @return Module entity.
     */
    public Module toEntity(ModuleDocument document) {
        return Module.builder()
                .id(document.getId())
                .name(document.getName())
                .link(document.getLink())
                .isActive(document.getIsActive())
                .isDeleted(document.getIsDelete())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .build();
    }
}
