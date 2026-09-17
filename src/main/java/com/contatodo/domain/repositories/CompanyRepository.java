package com.contatodo.domain.repositories;

import com.contatodo.domain.entities.Company;

import java.util.List;
import java.util.Optional;

/**
 * Port for company persistence operations.
 */
public interface CompanyRepository {

    /**
     * Saves a company.
     *
     * @param company Company to save.
     * @return Saved company.
     */
    Company save(Company company);

    /**
     * Saves a batch of companies.
     *
     * @param companies Companies to save.
     * @return Saved companies.
     */
    List<Company> saveAll(List<Company> companies);

    /**
     * Finds a company by identifier.
     *
     * @param id Company identifier.
     * @return Optional company.
     */
    Optional<Company> findById(String id);

    /**
     * Finds all non-deleted companies (both active and inactive).
     *
     * @return List of non-deleted companies ordered by name.
     */
    List<Company> findAllNotDeleted();
}
