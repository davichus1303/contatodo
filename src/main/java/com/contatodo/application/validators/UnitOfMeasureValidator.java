package com.contatodo.application.validators;

import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.UnitOfMeasureConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates that unit of measure identifiers reference existing, active units
 * owned by the given company.
 */
@Component
public class UnitOfMeasureValidator {

    private final UnitOfMeasureRepository unitOfMeasureRepository;

    /**
     * Creates a unit of measure validator.
     *
     * @param unitOfMeasureRepository Unit of measure repository port.
     */
    public UnitOfMeasureValidator(UnitOfMeasureRepository unitOfMeasureRepository) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    /**
     * Validates a single unit of measure identifier.
     *
     * @param companyOid Owning company identifier.
     * @param unitOfMeasure Unit of measure identifier, ignored when null.
     * @throws InvalidRequestException when the identifier cannot be resolved.
     */
    public void validateExist(CompanyOid companyOid, String unitOfMeasure) {
        if (unitOfMeasure == null) {
            return;
        }
        validateExist(companyOid, List.of(unitOfMeasure));
    }

    /**
     * Validates that every supplied unit of measure identifier exists, is
     * active and belongs to the given company.
     *
     * @param companyOid Owning company identifier.
     * @param unitOfMeasures Unit of measure identifiers, ignored when null or empty.
     * @throws InvalidRequestException when at least one identifier cannot be resolved.
     */
    public void validateExist(CompanyOid companyOid, Collection<String> unitOfMeasures) {
        if (unitOfMeasures == null || unitOfMeasures.isEmpty()) {
            return;
        }
        Set<String> existingOids = unitOfMeasureRepository.findActiveByCompany(companyOid).stream()
                .map(UnitOfMeasure::getId)
                .collect(Collectors.toSet());
        List<String> missingOids = unitOfMeasures.stream()
                .filter(Objects::nonNull)
                .distinct()
                .filter(oid -> !existingOids.contains(oid))
                .toList();
        if (!missingOids.isEmpty()) {
            List<String> errors = missingOids.stream()
                    .map(oid -> UnitOfMeasureConstants.NOT_FOUND_ERROR + ": " + oid)
                    .toList();
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, errors);
        }
    }
}
