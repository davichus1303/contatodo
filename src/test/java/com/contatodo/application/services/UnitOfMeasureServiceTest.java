package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateUnitOfMeasureRequest;
import com.contatodo.application.dto.request.UpdateUnitOfMeasureRequest;
import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.mapper.UnitOfMeasureMapper;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.UnitOfMeasureConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UnitOfMeasureService} covering company resolution and lookup.
 */
@ExtendWith(MockitoExtension.class)
class UnitOfMeasureServiceTest {

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @Mock
    private UnitOfMeasureMapper unitOfMeasureMapper;

    @Mock
    private CompanyService companyService;

    private UnitOfMeasureService unitOfMeasureService;

    @BeforeEach
    void setUp() {
        unitOfMeasureService = new UnitOfMeasureService(
                unitOfMeasureRepository, unitOfMeasureMapper, companyService
        );
    }

    @Test
    void getActiveUnitsOfMeasureDelegatesCompanyResolutionAndQueriesWithTheResult() {
        when(companyService.resolveReadCompanyOid("param-company")).thenReturn(CompanyOid.of("resolved-company"));
        when(unitOfMeasureRepository.findActiveByCompany(CompanyOid.of("resolved-company"))).thenReturn(List.of());
        when(unitOfMeasureMapper.toResponseList(any())).thenReturn(List.of());

        unitOfMeasureService.getActiveUnitsOfMeasure("param-company");

        verify(companyService).resolveReadCompanyOid("param-company");
        verify(unitOfMeasureRepository).findActiveByCompany(eq(CompanyOid.of("resolved-company")));
        verify(unitOfMeasureRepository, never()).findActiveByCompany(eq(CompanyOid.of("param-company")));
    }

    @Test
    void getActiveUnitsOfMeasureUsesTheCompanyResolvedFromTheToken() {
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("claim-company")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasureResponse response = new UnitOfMeasureResponse();
        response.setId("u-1");
        response.setName("Kilogram");
        response.setAbrev("kg");
        response.setCompanyOid("claim-company");
        response.setIsActive(true);
        response.setIsDeleted(false);
        when(companyService.resolveReadCompanyOid("param-company")).thenReturn(CompanyOid.of("claim-company"));
        when(unitOfMeasureRepository.findActiveByCompany(CompanyOid.of("claim-company"))).thenReturn(List.of(unitOfMeasure));
        when(unitOfMeasureMapper.toResponseList(List.of(unitOfMeasure))).thenReturn(List.of(response));

        List<UnitOfMeasureResponse> result = unitOfMeasureService.getActiveUnitsOfMeasure("param-company");

        assertEquals(1, result.size());
        assertEquals("kg", result.get(0).getAbrev());
        verify(unitOfMeasureRepository, never()).findActiveByCompany(eq(CompanyOid.of("param-company")));
    }

    @Test
    void getActiveUnitsOfMeasurePropagatesAnUnresolvableCompany() {
        when(companyService.resolveReadCompanyOid(any())).thenThrow(
                new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED)
        );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> unitOfMeasureService.getActiveUnitsOfMeasure("anything")
        );

        assertEquals(AuthConstants.COMPANY_CONTEXT_REQUIRED, exception.getMessage());
        verify(unitOfMeasureRepository, never()).findActiveByCompany(any());
    }

    @Test
    void createUnitOfMeasureResolvesCompanyChecksExistenceAndSaves() {
        CreateUnitOfMeasureRequest request = new CreateUnitOfMeasureRequest();
        request.setName("Kilogram");
        request.setAbrev("kg");
        request.setCompanyOid("company-9");
        UnitOfMeasure entity = UnitOfMeasure.builder()
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-9")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasure saved = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-9")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasureResponse response = new UnitOfMeasureResponse();
        response.setId("u-1");
        when(companyService.resolveRequiredCompanyOid("company-9")).thenReturn(CompanyOid.of("company-9"));
        when(unitOfMeasureRepository.findActiveByCompanyAndName(CompanyOid.of("company-9"), "Kilogram"))
                .thenReturn(Optional.empty());
        when(unitOfMeasureMapper.toEntity(request, "company-9")).thenReturn(entity);
        when(unitOfMeasureRepository.save(entity)).thenReturn(saved);
        when(unitOfMeasureMapper.toResponse(saved)).thenReturn(response);

        UnitOfMeasureResponse result = unitOfMeasureService.createUnitOfMeasure(request);

        assertEquals(response, result);
        verify(companyService).resolveRequiredCompanyOid("company-9");
        verify(unitOfMeasureRepository).findActiveByCompanyAndName(CompanyOid.of("company-9"), "Kilogram");
        verify(unitOfMeasureRepository).save(entity);
    }

    @Test
    void createUnitOfMeasureRejectsAnExistingNameWithoutSaving() {
        CreateUnitOfMeasureRequest request = new CreateUnitOfMeasureRequest();
        request.setName("Kilogram");
        request.setAbrev("kg");
        UnitOfMeasure existing = UnitOfMeasure.builder()
                .name("Kilogram")
                .abrev("kg")
                .build();
        when(companyService.resolveRequiredCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(unitOfMeasureRepository.findActiveByCompanyAndName(CompanyOid.of("company-1"), "Kilogram"))
                .thenReturn(Optional.of(existing));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> unitOfMeasureService.createUnitOfMeasure(request)
        );

        assertEquals(List.of(UnitOfMeasureConstants.NAME_ALREADY_EXISTS_ERROR), exception.getDetails());
        verify(unitOfMeasureRepository, never()).save(any());
    }

    @Test
    void findExistingUnitOfMeasureDelegatesToTheRepository() {
        UnitOfMeasure existing = UnitOfMeasure.builder()
                .name("Kilogram")
                .abrev("kg")
                .build();
        when(unitOfMeasureRepository.findActiveByCompanyAndName(CompanyOid.of("company-1"), "Kilogram"))
                .thenReturn(Optional.of(existing));

        Optional<UnitOfMeasure> result =
                unitOfMeasureService.findExistingUnitOfMeasure(CompanyOid.of("company-1"), "Kilogram");

        assertTrue(result.isPresent());
        verify(unitOfMeasureRepository).findActiveByCompanyAndName(CompanyOid.of("company-1"), "Kilogram");
    }

    @Test
    void getUnitOfMeasuresByOidDelegatesToTheRepository() {
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .isActive(true)
                .isDeleted(false)
                .build();
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1"))
                .thenReturn(Optional.of(unitOfMeasure));

        Optional<UnitOfMeasure> result =
                unitOfMeasureService.getUnitOfMeasuresByOid(CompanyOid.of("company-1"), "u-1");

        assertTrue(result.isPresent());
        assertEquals("kg", result.get().getAbrev());
        verify(unitOfMeasureRepository).findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1");
    }

    @Test
    void getUnitOfMeasuresByOidReturnsEmptyWhenTheUnitDoesNotExist() {
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-99"))
                .thenReturn(Optional.empty());

        Optional<UnitOfMeasure> result =
                unitOfMeasureService.getUnitOfMeasuresByOid(CompanyOid.of("company-1"), "u-99");

        assertTrue(result.isEmpty());
    }

    @Test
    void updateUnitOfMeasureReturnsTrueWhenTheUnitIsSaved() {
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .isActive(true)
                .isDeleted(false)
                .build();

        boolean result = unitOfMeasureService.updateUnitOfMeasure(unitOfMeasure);

        assertTrue(result);
        verify(unitOfMeasureRepository).save(unitOfMeasure);
    }

    @Test
    void updateUnitOfMeasureControlResolvesCompanyFindsMergesAndSaves() {
        UpdateUnitOfMeasureRequest request = new UpdateUnitOfMeasureRequest();
        request.setName("Gram");
        UnitOfMeasure existing = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasure updated = UnitOfMeasure.builder()
                .id("u-1")
                .name("Gram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasureResponse response = new UnitOfMeasureResponse();
        response.setId("u-1");
        when(companyService.resolveRequiredCompanyOid("company-9")).thenReturn(CompanyOid.of("company-1"));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1"))
                .thenReturn(Optional.of(existing));
        when(unitOfMeasureMapper.updateEntityFromRequest(existing, request)).thenReturn(updated);
        when(unitOfMeasureMapper.toResponse(updated)).thenReturn(response);

        UnitOfMeasureResponse result = unitOfMeasureService.updateUnitOfMeasureControl("company-9", request, "u-1");

        assertEquals(response, result);
        verify(companyService).resolveRequiredCompanyOid("company-9");
        verify(unitOfMeasureRepository).findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1");
        verify(unitOfMeasureRepository).save(updated);
    }

    @Test
    void updateUnitOfMeasureControlRejectsMissingUnitWithoutSaving() {
        UpdateUnitOfMeasureRequest request = new UpdateUnitOfMeasureRequest();
        request.setName("Gram");
        when(companyService.resolveRequiredCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-99"))
                .thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> unitOfMeasureService.updateUnitOfMeasureControl(null, request, "u-99")
        );

        assertEquals(List.of(UnitOfMeasureConstants.NOT_FOUND_ERROR), exception.getDetails());
        verify(unitOfMeasureRepository, never()).save(any());
    }

    @Test
    void updateUnitOfMeasureControlAllowsTheLogicalDeleteFlagToBeUpdated() {
        UpdateUnitOfMeasureRequest request = new UpdateUnitOfMeasureRequest();
        request.setIsDeleted(true);
        UnitOfMeasure existing = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasure deleted = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(false)
                .isDeleted(true)
                .build();
        UnitOfMeasureResponse response = new UnitOfMeasureResponse();
        response.setIsDeleted(true);
        when(companyService.resolveRequiredCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1"))
                .thenReturn(Optional.of(existing));
        when(unitOfMeasureMapper.updateEntityFromRequest(existing, request)).thenReturn(deleted);
        when(unitOfMeasureRepository.save(deleted)).thenReturn(deleted);
        when(unitOfMeasureMapper.toResponse(deleted)).thenReturn(response);

        UnitOfMeasureResponse result = unitOfMeasureService.updateUnitOfMeasureControl(null, request, "u-1");

        assertEquals(response, result);
        verify(unitOfMeasureRepository).save(deleted);
    }

    @Test
    void deleteUnitOfMeasureControlResolvesFindsAndDelegatesToTheUpdateControl() {
        UnitOfMeasure existing = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
        UnitOfMeasure updated = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(false)
                .isDeleted(true)
                .build();
        when(companyService.resolveRequiredCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1"))
                .thenReturn(Optional.of(existing));
        when(unitOfMeasureMapper.updateEntityFromRequest(eq(existing), any(UpdateUnitOfMeasureRequest.class)))
                .thenReturn(updated);
        when(unitOfMeasureRepository.save(updated)).thenReturn(updated);
        when(unitOfMeasureMapper.toResponse(updated)).thenReturn(new UnitOfMeasureResponse());

        unitOfMeasureService.deleteUnitOfMeasureControl("u-1", null);

        ArgumentCaptor<UpdateUnitOfMeasureRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateUnitOfMeasureRequest.class);
        verify(unitOfMeasureRepository, times(2)).findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-1");
        verify(unitOfMeasureMapper).updateEntityFromRequest(eq(existing), requestCaptor.capture());
        verify(unitOfMeasureRepository).save(updated);
        assertFalse(requestCaptor.getValue().getIsActive());
        assertTrue(requestCaptor.getValue().getIsDeleted());
    }

    @Test
    void deleteUnitOfMeasureControlRejectsMissingUnitWithoutSaving() {
        when(companyService.resolveRequiredCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(CompanyOid.of("company-1"), "u-99"))
                .thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> unitOfMeasureService.deleteUnitOfMeasureControl("u-99", null)
        );

        assertEquals(List.of(UnitOfMeasureConstants.NOT_FOUND_ERROR), exception.getDetails());
        verify(unitOfMeasureRepository, never()).save(any());
    }
}
