package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Module;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.ModuleDocument;
import com.contatodo.infrastructure.persistence.repository.ModuleMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the module repository port.
 */
@Repository
public class ModuleRepositoryAdapter implements ModuleRepository {

    private final ModuleMongoRepository moduleMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates a module repository adapter.
     *
     * @param moduleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ModuleRepositoryAdapter(
            ModuleMongoRepository moduleMongoRepository,
            PersistenceMapper persistenceMapper
    ) {
        this.moduleMongoRepository = moduleMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module save(Module module) {
        ModuleDocument document = persistenceMapper.toModuleDocument(module);
        ModuleDocument savedDocument = moduleMongoRepository.save(document);
        return persistenceMapper.toModuleEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Module> findById(String id) {
        return moduleMongoRepository.findById(id).map(persistenceMapper::toModuleEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> findActiveModulesByUserOid(String userOid) {
        return persistenceMapper.toModuleEntityList(moduleMongoRepository.findActiveModulesByUserOid(userOid));
    }
}
