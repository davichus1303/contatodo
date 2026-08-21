package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.adapters.outbound.persistence.mapper.AcquisitionTypePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.AcquisitionTypeDocument;
import com.contatodo.adapters.outbound.persistence.repository.AcquisitionTypeMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the acquisition type repository port.
 */
@Repository
public class AcquisitionTypeRepositoryAdapter implements AcquisitionTypeRepository {

    private final AcquisitionTypeMongoRepository acquisitionTypeMongoRepository;
    private final AcquisitionTypePersistenceMapper persistenceMapper;

    /**
     * Creates an acquisition type repository adapter.
     *
     * @param acquisitionTypeMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public AcquisitionTypeRepositoryAdapter(
            AcquisitionTypeMongoRepository acquisitionTypeMongoRepository,
            AcquisitionTypePersistenceMapper persistenceMapper
    ) {
        this.acquisitionTypeMongoRepository = acquisitionTypeMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AcquisitionType save(AcquisitionType acquisitionType) {
        AcquisitionTypeDocument document = persistenceMapper.toDocument(acquisitionType);
        AcquisitionTypeDocument savedDocument = acquisitionTypeMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AcquisitionType> findById(String id) {
        return acquisitionTypeMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AcquisitionType> findActiveAndNotDeleted() {
        return persistenceMapper.toEntityList(
                acquisitionTypeMongoRepository.findByIsActiveTrueAndIsDeletedFalse()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AcquisitionType> findByName(String name) {
        return acquisitionTypeMongoRepository.findByName(name).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AcquisitionType> findAllNotDeleted() {
        return persistenceMapper.toEntityList(
                acquisitionTypeMongoRepository.findByIsDeletedFalseOrderByNameAsc()
        );
    }
}
