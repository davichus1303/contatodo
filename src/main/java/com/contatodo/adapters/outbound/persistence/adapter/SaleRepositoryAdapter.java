package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.SaleDocument;
import com.contatodo.adapters.outbound.persistence.mapper.SalePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.SaleMongoRepository;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.repositories.SaleRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the sale repository port on top of MongoDB.
 */
@Repository
public class SaleRepositoryAdapter implements SaleRepository {

    private final SaleMongoRepository saleMongoRepository;
    private final SalePersistenceMapper persistenceMapper;

    /**
     * Creates a sale repository adapter.
     *
     * @param saleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public SaleRepositoryAdapter(
            SaleMongoRepository saleMongoRepository,
            SalePersistenceMapper persistenceMapper
    ) {
        this.saleMongoRepository = saleMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sale save(Sale sale) {
        SaleDocument document = persistenceMapper.toDocument(sale);
        SaleDocument savedDocument = saleMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findByUserOidAndSaleDate(String userOid, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return persistenceMapper.toEntityList(
                saleMongoRepository.findByUserOidAndSaleDateBetween(userOid, startOfDay, endOfDay)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findByUserOidAndSaleDateBetween(String userOid, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return persistenceMapper.toEntityList(
                saleMongoRepository.findByUserOidAndSaleDateBetween(userOid, startOfDay, endOfDay)
        );
    }
}
