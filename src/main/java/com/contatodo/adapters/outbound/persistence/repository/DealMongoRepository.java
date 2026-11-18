package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.DealDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for deals.
 */
public interface DealMongoRepository extends MongoRepository<DealDocument, String> {

    List<DealDocument> findByCompanyOidAndIsDeleteFalse(String companyOid);

    List<DealDocument> findByCompanyOidAndIsActiveTrueAndIsDeleteFalse(String companyOid);

    Optional<DealDocument> findByCompanyOidAndProductOidAndIsActiveTrueAndIsDeleteFalse(
            String companyOid, String productOid);

    Optional<DealDocument> findByCompanyOidAndProductOidAndIsDeleteFalse(String companyOid, String productOid);
}
