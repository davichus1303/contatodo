package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.CompanyDocument;
import com.contatodo.adapters.outbound.persistence.mapper.CompanyPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.CompanyMongoRepository;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.repositories.CompanyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the company repository port.
 */
@Repository
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyMongoRepository companyMongoRepository;
    private final CompanyPersistenceMapper persistenceMapper;

    /**
     * Creates a company repository adapter.
     *
     * @param companyMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public CompanyRepositoryAdapter(
            CompanyMongoRepository companyMongoRepository,
            CompanyPersistenceMapper persistenceMapper
    ) {
        this.companyMongoRepository = companyMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Company save(Company company) {
        CompanyDocument savedDocument = companyMongoRepository.save(persistenceMapper.toDocument(company));
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> findById(String id) {
        return companyMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> saveAll(List<Company> companies) {
        List<CompanyDocument> documents = companies.stream()
                .map(persistenceMapper::toDocument)
                .toList();
        return persistenceMapper.toEntityList(companyMongoRepository.saveAll(documents));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> findAllNotDeleted() {
        return persistenceMapper.toEntityList(
                companyMongoRepository.findByIsDeletedFalseOrderByNameAsc()
        );
    }
}
