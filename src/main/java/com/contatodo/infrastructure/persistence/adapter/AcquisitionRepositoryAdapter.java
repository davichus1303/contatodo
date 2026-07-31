package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.AcquisitionDocument;
import com.contatodo.infrastructure.persistence.repository.AcquisitionMongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the acquisition repository port.
 */
@Repository
public class AcquisitionRepositoryAdapter implements AcquisitionRepository {

    private final AcquisitionMongoRepository acquisitionMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates an acquisition repository adapter.
     *
     * @param acquisitionMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public AcquisitionRepositoryAdapter(
            AcquisitionMongoRepository acquisitionMongoRepository,
            PersistenceMapper persistenceMapper
    ) {
        this.acquisitionMongoRepository = acquisitionMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Acquisition save(Acquisition acquisition) {
        AcquisitionDocument document = persistenceMapper.toAcquisitionDocument(acquisition);
        AcquisitionDocument savedDocument = acquisitionMongoRepository.save(document);
        return persistenceMapper.toAcquisitionEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByUserOidAndAcquisitionDateBetween(String userOid, LocalDateTime startDate, LocalDateTime endDate) {
        return persistenceMapper.toAcquisitionEntityList(
                acquisitionMongoRepository.findByUserOidAndAcquisitionDateBetweenAndIsDeletedFalse(userOid, startDate, endDate)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByUserOidOrderByAcquisitionDateDesc(String userOid) {
        return persistenceMapper.toAcquisitionEntityList(
                acquisitionMongoRepository.findByUserOidAndIsDeletedFalseOrderByAcquisitionDateDesc(userOid)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Acquisition> findByProductOid(String productOid) {
        return persistenceMapper.toAcquisitionEntityList(
                acquisitionMongoRepository.findByProductOidAndIsDeletedFalse(productOid)
        );
    }
}
