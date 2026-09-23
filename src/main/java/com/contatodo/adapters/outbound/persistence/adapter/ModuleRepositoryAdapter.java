package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.Module;
import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.adapters.outbound.persistence.mapper.ModulePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.ModuleDocument;
import com.contatodo.adapters.outbound.persistence.repository.ModuleMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter implementing the module repository port.
 */
@Repository
public class ModuleRepositoryAdapter implements ModuleRepository {

    private static final Logger log = LoggerFactory.getLogger(ModuleRepositoryAdapter.class);

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
        return moduleMongoRepository.findActiveModules().stream()
                .map(this::mapSkippingInvalid)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private Optional<Module> mapSkippingInvalid(ModuleDocument document) {
        try {
            return Optional.of(persistenceMapper.toEntity(document));
        } catch (InvalidEntityStateException exception) {
            log.warn("Skipping invalid module document with id='{}': {}", document.getId(), exception.getMessage());
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> findAllById(Iterable<String> ids) {
        return StreamSupport.stream(moduleMongoRepository.findAllById(ids).spliterator(), false)
                .map(persistenceMapper::toEntity)
                .collect(Collectors.toList());
    }
}
