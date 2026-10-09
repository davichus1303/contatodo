package com.contatodo.application.services;

import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.mapper.UnitOfMeasureMapper;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service containing unit of measure business logic.
 */
@Service
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final UnitOfMeasureMapper unitOfMeasureMapper;
    private final CompanyService companyService;

    /**
     * Creates a unit of measure service.
     *
     * @param unitOfMeasureRepository Unit of measure repository port.
     * @param unitOfMeasureMapper Unit of measure mapper.
     * @param companyService Company service used to resolve the owning company.
     */
    public UnitOfMeasureService(
            UnitOfMeasureRepository unitOfMeasureRepository,
            UnitOfMeasureMapper unitOfMeasureMapper,
            CompanyService companyService
    ) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.unitOfMeasureMapper = unitOfMeasureMapper;
        this.companyService = companyService;
    }

    /**
     * Retrieves the active, non-deleted units of measure of the resolved company.
     *
     * @param requestedCompanyOid Company identifier supplied in the request.
     * @return List of unit of measure responses.
     */
    public List<UnitOfMeasureResponse> getActiveUnitsOfMeasure(String requestedCompanyOid) {
        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);
        List<UnitOfMeasure> unitsOfMeasure = unitOfMeasureRepository.findActiveByCompany(companyOid);
        return unitOfMeasureMapper.toResponseList(unitsOfMeasure);
    }
}
