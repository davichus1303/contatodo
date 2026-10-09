package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.mapper.UnitOfMeasurePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.UnitOfMeasureMongoRepository;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<UnitOfMeasure> findActiveByCompany(CompanyOid companyOid) {
        String companyValue = companyOid != null ? companyOid.value() : null;
        return persistenceMapper.toEntityList(
                unitOfMeasureMongoRepository.findByIsActiveTrueAndIsDeletedFalseAndCompanyOidOrderByNameAsc(companyValue)
        );
    }
}
