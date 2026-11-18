package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.mapper.DealPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.DealMongoRepository;
import com.contatodo.domain.entities.Deal;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.DealRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the deal repository port.
 */
@Repository
public class DealRepositoryAdapter implements DealRepository {

    private final DealMongoRepository dealMongoRepository;
    private final DealPersistenceMapper persistenceMapper;

    public DealRepositoryAdapter(
            DealMongoRepository dealMongoRepository,
            DealPersistenceMapper persistenceMapper
    ) {
        this.dealMongoRepository = dealMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public Deal save(Deal deal) {
        return persistenceMapper.toEntity(
                dealMongoRepository.save(persistenceMapper.toDocument(deal))
        );
    }

    @Override
    public List<Deal> findByCompanyAndNotDeleted(CompanyOid companyOid) {
        return persistenceMapper.toEntityList(
                dealMongoRepository.findByCompanyOidAndIsDeleteFalse(companyOid.value())
        );
    }

    @Override
    public List<Deal> findActiveNotDeletedByCompany(CompanyOid companyOid) {
        return persistenceMapper.toEntityList(
                dealMongoRepository.findByCompanyOidAndIsActiveTrueAndIsDeleteFalse(companyOid.value())
        );
    }

    @Override
    public Optional<Deal> findActiveByCompanyAndProduct(CompanyOid companyOid, String productOid) {
        return dealMongoRepository
                .findByCompanyOidAndProductOidAndIsActiveTrueAndIsDeleteFalse(companyOid.value(), productOid)
                .map(persistenceMapper::toEntity);
    }

    @Override
    public Optional<Deal> findNotDeletedByCompanyAndProduct(CompanyOid companyOid, String productOid) {
        return dealMongoRepository
                .findByCompanyOidAndProductOidAndIsDeleteFalse(companyOid.value(), productOid)
                .map(persistenceMapper::toEntity);
    }

    @Override
    public Optional<Deal> findById(String id) {
        return dealMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }
}
