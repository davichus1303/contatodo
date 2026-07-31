package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.AcquisitionTypeDocument;
import com.contatodo.infrastructure.persistence.repository.AcquisitionTypeMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the acquisition type repository port.
 */
@Repository
public class AcquisitionTypeRepositoryAdapter implements AcquisitionTypeRepository {

    private final AcquisitionTypeMongoRepository acquisitionTypeMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates an acquisition type repository adapter.
     *
     * @param acquisitionTypeMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public AcquisitionTypeRepositoryAdapter(
            AcquisitionTypeMongoRepository acquisitionTypeMongoRepository,
            PersistenceMapper persistenceMapper
    ) {
        this.acquisitionTypeMongoRepository = acquisitionTypeMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AcquisitionType save(AcquisitionType acquisitionType) {
        AcquisitionTypeDocument document = persistenceMapper.toAcquisitionTypeDocument(acquisitionType);
        AcquisitionTypeDocument savedDocument = acquisitionTypeMongoRepository.save(document);
        return persistenceMapper.toAcquisitionTypeEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AcquisitionType> findById(String id) {
        return acquisitionTypeMongoRepository.findById(id).map(persistenceMapper::toAcquisitionTypeEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AcquisitionType> findActiveAndNotDeleted() {
        return persistenceMapper.toAcquisitionTypeEntityList(
                acquisitionTypeMongoRepository.findByIsActiveTrueAndIsDeletedFalse()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AcquisitionType> findByName(String name) {
        return acquisitionTypeMongoRepository.findByName(name).map(persistenceMapper::toAcquisitionTypeEntity);
    }
}
