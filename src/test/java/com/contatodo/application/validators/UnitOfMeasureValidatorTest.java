package com.contatodo.application.validators;

import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.shared.constants.UnitOfMeasureConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UnitOfMeasureValidator}.
 */
@ExtendWith(MockitoExtension.class)
class UnitOfMeasureValidatorTest {

    private static final CompanyOid COMPANY = CompanyOid.of("company-1");

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    private UnitOfMeasureValidator validator() {
        return new UnitOfMeasureValidator(unitOfMeasureRepository);
    }

    @Test
    void acceptsUnitsOfMeasureOwnedByTheCompany() {
        when(unitOfMeasureRepository.findActiveByCompany(COMPANY))
                .thenReturn(List.of(unitOfMeasure("uom-1")));

        assertDoesNotThrow(() -> validator().validateExist(COMPANY, List.of("uom-1")));
    }

    @Test
    void rejectsUnitsOfMeasureThatDoNotExistForTheCompany() {
        when(unitOfMeasureRepository.findActiveByCompany(COMPANY))
                .thenReturn(List.of(unitOfMeasure("uom-1")));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator().validateExist(COMPANY, List.of("uom-1", "uom-missing"))
        );

        assertTrue(exception.getDetails().contains(UnitOfMeasureConstants.NOT_FOUND_ERROR + ": uom-missing"));
    }

    @Test
    void rejectsASingleUnknownUnitOfMeasure() {
        when(unitOfMeasureRepository.findActiveByCompany(COMPANY))
                .thenReturn(List.of(unitOfMeasure("uom-1")));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator().validateExist(COMPANY, "uom-missing")
        );

        assertTrue(exception.getDetails().contains(UnitOfMeasureConstants.NOT_FOUND_ERROR + ": uom-missing"));
    }

    @Test
    void requireActiveReturnsTheConfiguredUnit() {
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(COMPANY, "uom-1"))
                .thenReturn(java.util.Optional.of(unitOfMeasure("uom-1")));

        UnitOfMeasure unit = validator().requireActive(COMPANY, "uom-1");

        assertEquals("uom-1", unit.getId());
    }

    @Test
    void requireActiveRejectsAnUnknownUnitOfMeasure() {
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(COMPANY, "uom-missing"))
                .thenReturn(java.util.Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator().requireActive(COMPANY, "uom-missing")
        );

        assertTrue(exception.getDetails().contains(UnitOfMeasureConstants.NOT_FOUND_ERROR + ": uom-missing"));
    }

    @Test
    void ignoresNullAndEmptyUnitCollections() {
        assertDoesNotThrow(() -> validator().validateExist(COMPANY, (List<String>) null));
        assertDoesNotThrow(() -> validator().validateExist(COMPANY, List.of()));
        verifyNoInteractions(unitOfMeasureRepository);
    }

    @Test
    void ignoresANullSingleUnitOfMeasure() {
        assertDoesNotThrow(() -> validator().validateExist(COMPANY, (String) null));
        verifyNoInteractions(unitOfMeasureRepository);
    }

    private UnitOfMeasure unitOfMeasure(String id) {
        return UnitOfMeasure.builder()
                .id(id)
                .name("Kilogramo")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
    }
}
