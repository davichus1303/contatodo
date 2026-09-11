package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.Module;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.adapters.outbound.persistence.mapper.ModulePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.ModuleDocument;
import com.contatodo.adapters.outbound.persistence.repository.ModuleMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the module repository port.
 */
@Repository
public class ModuleRepositoryAdapter implements ModuleRepository {

    private final ModuleMongoRepository moduleMongoRepository;
    private final ModulePersistenceMapper persistenceMapper;

    /**
     * Creates a module repository adapter.
     *
     * @param moduleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ModuleRepositoryAdapter(
            ModuleMongoRepository moduleMongoRepository,
            ModulePersistenceMapper persistenceMapper
    ) {
        this.moduleMongoRepository = moduleMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module save(Module module) {
        ModuleDocument document = persistenceMapper.toDocument(module);
        ModuleDocument savedDocument = moduleMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Module> findById(String id) {
        return moduleMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> findAllActive() {
        return persistenceMapper.toEntityList(moduleMongoRepository.findActiveModules());
    }
}
