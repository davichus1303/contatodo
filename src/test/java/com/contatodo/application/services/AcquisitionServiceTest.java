package com.contatodo.application.services;

import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.application.mapper.AcquisitionMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.AcquisitionValidator;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.domain.repositories.ProductRepository;
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
 * Unit tests for {@link AcquisitionService} covering the company scoped reads.
 */
@ExtendWith(MockitoExtension.class)
class AcquisitionServiceTest {

    @Mock
    private AcquisitionRepository acquisitionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AcquisitionTypeRepository acquisitionTypeRepository;

    @Mock
    private AcquisitionValidator acquisitionValidator;

    @Mock
    private AcquisitionMapper acquisitionMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private ProductInventoryHandler productInventoryHandler;

    @Mock
    private CompanyService companyService;

    private AcquisitionService acquisitionService;

    @BeforeEach
    void setUp() {
        acquisitionService = new AcquisitionService(
                acquisitionRepository,
                productRepository,
                acquisitionTypeRepository,
                acquisitionValidator,
                acquisitionMapper,
                authenticatedUserProvider,
                expenseService,
                productInventoryHandler,
                companyService
        );
    }

    @Test
    void getAcquisitionsDelegatesCompanyResolutionAndQueriesWithTheResult() {
        when(companyService.resolveReadCompanyOid("param-company")).thenReturn(CompanyOid.of("resolved-company"));
        when(acquisitionRepository.findByAcquisitionDateBetween(eq(CompanyOid.of("resolved-company")), any(), any()))
                .thenReturn(List.of());
        when(acquisitionMapper.toResponseList(any(), any(), any())).thenReturn(List.of());

        List<AcquisitionResponse> response = acquisitionService.getAcquisitions("param-company", null, null);

        assertEquals(0, response.size());
        verify(companyService).resolveReadCompanyOid("param-company");
        verify(acquisitionRepository).findByAcquisitionDateBetween(eq(CompanyOid.of("resolved-company")), any(), any());
    }

    @Test
    void getAcquisitionsPropagatesAnUnresolvableCompany() {
        when(companyService.resolveReadCompanyOid(any())).thenThrow(
                new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED)
        );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> acquisitionService.getAcquisitions(null, null, null)
        );

        assertEquals(AuthConstants.COMPANY_CONTEXT_REQUIRED, exception.getMessage());
        verify(acquisitionRepository, never()).findByAcquisitionDateBetween(any(), any(), any());
    }
}
