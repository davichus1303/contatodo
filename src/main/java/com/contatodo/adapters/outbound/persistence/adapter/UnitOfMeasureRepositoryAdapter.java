package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.UnitOfMeasureDocument;
import com.contatodo.adapters.outbound.persistence.mapper.UnitOfMeasurePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.UnitOfMeasureMongoRepository;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the unit of measure repository port.
 */
@Repository
public class UnitOfMeasureRepositoryAdapter implements UnitOfMeasureRepository {

    private final UnitOfMeasureMongoRepository unitOfMeasureMongoRepository;
    private final UnitOfMeasurePersistenceMapper persistenceMapper;

    /**
     * Creates a unit of measure repository adapter.
     *
     * @param unitOfMeasureMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public UnitOfMeasureRepositoryAdapter(
            UnitOfMeasureMongoRepository unitOfMeasureMongoRepository,
            UnitOfMeasurePersistenceMapper persistenceMapper
    ) {
        this.unitOfMeasureMongoRepository = unitOfMeasureMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnitOfMeasure save(UnitOfMeasure unitOfMeasure) {
        UnitOfMeasureDocument document = persistenceMapper.toDocument(unitOfMeasure);
        UnitOfMeasureDocument savedDocument = unitOfMeasureMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UnitOfMeasure> findActiveByCompany(CompanyOid companyOid) {
        String companyValue = companyOid != null ? companyOid.value() : null;
        return persistenceMapper.toEntityList(
                unitOfMeasureMongoRepository.findByIsActiveTrueAndIsDeletedFalseAndCompanyOidOrderByNameAsc(companyValue)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UnitOfMeasure> findActiveByCompanyAndName(CompanyOid companyOid, String name) {
        String companyValue = companyOid != null ? companyOid.value() : null;
        return unitOfMeasureMongoRepository
                .findByIsActiveTrueAndIsDeletedFalseAndCompanyOidAndName(companyValue, name)
                .map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UnitOfMeasure> findActiveByCompanyAndOid(CompanyOid companyOid, String unitOfMeasuresOid) {
        String companyValue = companyOid != null ? companyOid.value() : null;
        return unitOfMeasureMongoRepository
                .findByIsActiveTrueAndIsDeletedFalseAndCompanyOidAndId(companyValue, unitOfMeasuresOid)
                .map(persistenceMapper::toEntity);
    }
}
