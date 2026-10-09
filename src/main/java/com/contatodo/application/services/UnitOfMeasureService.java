package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUnitOfMeasureRequest;
import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.mapper.UnitOfMeasureMapper;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.UnitOfMeasureConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    /**
     * Creates a new unit of measure for the resolved company.
     *
     * <p>The owning company is resolved from the session claim, falling back to
     * the requested company. When an active, non-deleted unit with the same
     * name already exists in that company nothing is written and a formatted
     * validation error is returned.</p>
     *
     * @param request Create unit of measure request.
     * @return Created unit of measure response.
     * @throws com.contatodo.shared.exceptions.InvalidRequestException when the name already exists.
     */
    public UnitOfMeasureResponse createUnitOfMeasure(CreateUnitOfMeasureRequest request) {
        CompanyOid companyOid = companyService.resolveRequiredCompanyOid(request.getCompanyOid());

        if (findExistingUnitOfMeasure(companyOid, request.getName()).isPresent()) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(UnitOfMeasureConstants.NAME_ALREADY_EXISTS_ERROR)
            );
        }

        UnitOfMeasure unitOfMeasure = unitOfMeasureMapper.toEntity(request, companyOid.value());
        UnitOfMeasure savedUnitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
        return unitOfMeasureMapper.toResponse(savedUnitOfMeasure);
    }

    /**
     * Finds the existing unit of measure with the given name inside the company.
     *
     * <p>Reusable by the create, update and delete flows. A unit is considered
     * to exist when it is active, not deleted and belongs to the company.</p>
     *
     * @param companyOid Owning company.
     * @param name Unit of measure name.
     * @return Optional matching unit of measure, empty when it does not exist.
     */
    public Optional<UnitOfMeasure> findExistingUnitOfMeasure(CompanyOid companyOid, String name) {
        return unitOfMeasureRepository.findActiveByCompanyAndName(companyOid, name);
    }
}
