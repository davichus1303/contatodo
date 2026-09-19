package com.contatodo.adapters.outbound.persistence.repository;

import com.contatodo.adapters.outbound.persistence.document.AcquisitionTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for acquisition types.
 */
public interface AcquisitionTypeMongoRepository extends MongoRepository<AcquisitionTypeDocument, String> {
}