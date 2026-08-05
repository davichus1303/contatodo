package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.ProductCostHistoryDocument;
import com.contatodo.infrastructure.persistence.repository.ProductCostHistoryMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter implementing the product cost history repository port.
 */
@Repository
public class ProductCostHistoryRepositoryAdapter implements ProductCostHistoryRepository {

    private final ProductCostHistoryMongoRepository productCostHistoryMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates a product cost history repository adapter.
     *
     * @param productCostHistoryMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public ProductCostHistoryRepositoryAdapter(
            ProductCostHistoryMongoRepository productCostHistoryMongoRepository,
            PersistenceMapper persistenceMapper
    ) {
        this.productCostHistoryMongoRepository = productCostHistoryMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductCostHistory save(ProductCostHistory productCostHistory) {
        ProductCostHistoryDocument document = persistenceMapper.toProductCostHistoryDocument(productCostHistory);
        ProductCostHistoryDocument savedDocument = productCostHistoryMongoRepository.save(document);
        return persistenceMapper.toProductCostHistoryEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductCostHistory> findByProductOid(String productOid) {
        return persistenceMapper.toProductCostHistoryEntityList(
                productCostHistoryMongoRepository.findByProductOid(productOid)
        );
    }
}
