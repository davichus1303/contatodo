package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.adapters.outbound.persistence.mapper.ProductCostHistoryPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.ProductCostHistoryDocument;
import com.contatodo.adapters.outbound.persistence.repository.ProductCostHistoryMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter implementing the product cost history repository port.
 */
@Repository
public class ProductCostHistoryRepositoryAdapter implements ProductCostHistoryRepository {

    private final ProductCostHistoryMongoRepository productCostHistoryMongoRepository;
    private final ProductCostHistoryPersistenceMapper persistenceMapper;

    /**
     * Creates a product cost history repository adapter.
     *
     * @param productCostHistoryMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ProductCostHistoryRepositoryAdapter(
            ProductCostHistoryMongoRepository productCostHistoryMongoRepository,
            ProductCostHistoryPersistenceMapper persistenceMapper
    ) {
        this.productCostHistoryMongoRepository = productCostHistoryMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductCostHistory save(ProductCostHistory productCostHistory) {
        ProductCostHistoryDocument document = persistenceMapper.toDocument(productCostHistory);
        ProductCostHistoryDocument savedDocument = productCostHistoryMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductCostHistory> findByProductOid(String productOid) {
        return persistenceMapper.toEntityList(
                productCostHistoryMongoRepository.findByProductOid(productOid)
        );
    }
}
