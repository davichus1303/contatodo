package com.contatodo.application.services;

import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.mapper.UnitOfMeasureMapper;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
}
