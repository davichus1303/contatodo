package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUnitOfMeasureRequest;
import com.contatodo.application.dto.request.UpdateUnitOfMeasureRequest;
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

    /**
     * Finds the active, non-deleted unit of measure with the given identifier
     * inside the given company.
     *
     * <p>Reusable by the update, delete and read flows. A unit is only returned
     * when it is active, not deleted and belongs to the company.</p>
     *
     * @param companyOid Owning company.
     * @param unitOfMeasuresOid Unit of measure identifier.
     * @return Optional matching unit of measure, empty when it does not exist.
     */
    public Optional<UnitOfMeasure> getUnitOfMeasuresByOid(CompanyOid companyOid, String unitOfMeasuresOid) {
        return unitOfMeasureRepository.findActiveByCompanyAndOid(companyOid, unitOfMeasuresOid);
    }

    /**
     * Updates the given unit of measure.
     *
     * <p>Returns {@code true} when the unit was saved without error. Persistence
     * failures propagate to the exception handler so the caller receives a
     * formatted error.</p>
     *
     * @param unitOfMeasure Unit of measure to update.
     * @return {@code true} when the save succeeded.
     */
    public boolean updateUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        unitOfMeasureRepository.save(unitOfMeasure);
        return true;
    }

    /**
     * Controls an update, merging the requested fields over the existing unit.
     *
     * <p>The owning company is resolved from the session claim, falling back to
     * the requested company. When no active, non-deleted unit of that company
     * matches the given identifier a formatted validation error is returned.
     * Otherwise the updated unit is saved and returned.</p>
     *
     * @param requestedCompanyOid Optional company identifier supplied in the request.
     * @param request Update unit of measure request.
     * @param unitOfMeasuresOid Unit of measure identifier to update.
     * @return Updated unit of measure response.
     * @throws com.contatodo.shared.exceptions.InvalidRequestException when the unit is missing or the update fails.
     */
    public UnitOfMeasureResponse updateUnitOfMeasureControl(
            String requestedCompanyOid, UpdateUnitOfMeasureRequest request, String unitOfMeasuresOid
    ) {
        CompanyOid companyOid = companyService.resolveRequiredCompanyOid(requestedCompanyOid);
        UnitOfMeasure unitOfMeasure = getUnitOfMeasuresByOid(companyOid, unitOfMeasuresOid)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseConstants.VALIDATION_ERROR_MESSAGE,
                        List.of(UnitOfMeasureConstants.NOT_FOUND_ERROR)
                ));

        UnitOfMeasure updatedUnitOfMeasure = unitOfMeasureMapper.updateEntityFromRequest(unitOfMeasure, request);
        if (updateUnitOfMeasure(updatedUnitOfMeasure)) {
            return unitOfMeasureMapper.toResponse(updatedUnitOfMeasure);
        }
        throw new InvalidRequestException(
                ResponseConstants.VALIDATION_ERROR_MESSAGE,
                List.of(UnitOfMeasureConstants.UPDATE_ERROR)
        );
    }

    /**
     * Performs the logical deletion of a unit of measure.
     *
     * <p>The owning company is resolved from the session claim, falling back to
     * the requested company. The existing unit is fetched through
     * {@link #getUnitOfMeasuresByOid(CompanyOid, String)} and, when present, the
     * deletion is delegated to
     * {@link #updateUnitOfMeasureControl(String, UpdateUnitOfMeasureRequest, String)}
     * with the active flag set to {@code false} and the logical delete flag set
     * to {@code true}.</p>
     *
     * @param unitOfMeasuresOid Unit of measure identifier to delete.
     * @param requestedCompanyOid Optional company identifier supplied in the request.
     * @throws com.contatodo.shared.exceptions.InvalidRequestException when the unit is missing.
     */
    public void deleteUnitOfMeasureControl(String unitOfMeasuresOid, String requestedCompanyOid) {
        CompanyOid companyOid = companyService.resolveRequiredCompanyOid(requestedCompanyOid);
        getUnitOfMeasuresByOid(companyOid, unitOfMeasuresOid)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseConstants.VALIDATION_ERROR_MESSAGE,
                        List.of(UnitOfMeasureConstants.NOT_FOUND_ERROR)
                ));

        UpdateUnitOfMeasureRequest request = new UpdateUnitOfMeasureRequest();
        request.setIsActive(false);
        request.setIsDeleted(true);
        updateUnitOfMeasureControl(requestedCompanyOid, request, unitOfMeasuresOid);
    }
}
