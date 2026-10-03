package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.adapters.outbound.persistence.document.SaleDocument;
import com.contatodo.adapters.outbound.persistence.mapper.SalePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.repository.SaleMongoRepository;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.shared.utils.DateUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementing the sale repository port on top of MongoDB.
 *
 * <p>All reads are company-scoped; the owning company is resolved by the
 * application layer and passed into each query.</p>
 */
@Repository
public class SaleRepositoryAdapter implements SaleRepository {

    private final SaleMongoRepository saleMongoRepository;
    private final SalePersistenceMapper persistenceMapper;
    private final MongoTemplate mongoTemplate;

    /**
     * Creates a sale repository adapter.
     *
     * @param saleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     * @param mongoTemplate Mongo template.
     */
    public SaleRepositoryAdapter(
            SaleMongoRepository saleMongoRepository,
            SalePersistenceMapper persistenceMapper,
            MongoTemplate mongoTemplate
    ) {
        this.saleMongoRepository = saleMongoRepository;
        this.persistenceMapper = persistenceMapper;
        this.mongoTemplate = mongoTemplate;
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
    public List<Sale> findBySaleDate(CompanyOid companyOid, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.startOfDay(date);
        LocalDateTime endOfDay = DateUtils.endOfDay(date);
        return findBySaleDateBetween(companyOid, startOfDay, endOfDay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sale> findBySaleDateBetween(CompanyOid companyOid, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Query query = companyQuery(companyOid);
        query.addCriteria(Criteria.where("saleDate").gte(startOfDay).lte(endOfDay));
        return toEntityList(query);
    }

    private List<Sale> toEntityList(Query query) {
        return mongoTemplate.find(query, SaleDocument.class).stream()
                .map(persistenceMapper::toEntity)
                .toList();
    }

    /**
     * Builds the company filter for a query.
     *
     * <p>Records without a company are matched by {@code null}, so the write
     * path can still count the next sale number for a company-less sale.</p>
     *
     * @param companyOid Owning company, {@code null} keeps sales without a company.
     * @return Query scoped to the given company.
     */
    private Query companyQuery(CompanyOid companyOid) {
        return Query.query(Criteria.where("companyOid").is(companyOid != null ? companyOid.value() : null));
    }
}
