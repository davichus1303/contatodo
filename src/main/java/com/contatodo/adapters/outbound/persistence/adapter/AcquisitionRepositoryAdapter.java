package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.adapters.outbound.persistence.mapper.AcquisitionPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.AcquisitionDocument;
import com.contatodo.adapters.outbound.persistence.repository.AcquisitionMongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the acquisition repository port.
 */
@Repository
public class AcquisitionRepositoryAdapter implements AcquisitionRepository {

    private final AcquisitionMongoRepository acquisitionMongoRepository;
    private final AcquisitionPersistenceMapper persistenceMapper;

    /**
     * Creates an acquisition repository adapter.
     *
     * @param acquisitionMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public AcquisitionRepositoryAdapter(
            AcquisitionMongoRepository acquisitionMongoRepository,
            AcquisitionPersistenceMapper persistenceMapper
    ) {
        this.acquisitionMongoRepository = acquisitionMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Acquisition save(Acquisition acquisition) {
        AcquisitionDocument document = persistenceMapper.toDocument(acquisition);
        AcquisitionDocument savedDocument = acquisitionMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByUserOidAndAcquisitionDateBetween(String userOid, LocalDateTime startDate, LocalDateTime endDate) {
        return persistenceMapper.toEntityList(
                acquisitionMongoRepository.findByUserOidAndAcquisitionDateBetweenAndIsDeletedFalse(userOid, startDate, endDate)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByUserOidOrderByAcquisitionDateDesc(String userOid) {
        return persistenceMapper.toEntityList(
                acquisitionMongoRepository.findByUserOidAndIsDeletedFalseOrderByAcquisitionDateDesc(userOid)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByProductOid(String productOid) {
        return persistenceMapper.toEntityList(
                acquisitionMongoRepository.findByProductOidAndIsDeletedFalse(productOid)
        );
    }
}
