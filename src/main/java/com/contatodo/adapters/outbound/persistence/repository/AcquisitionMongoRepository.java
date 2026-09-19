package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.AcquisitionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for acquisitions.
 */
public interface AcquisitionMongoRepository extends MongoRepository<AcquisitionDocument, String> {
}