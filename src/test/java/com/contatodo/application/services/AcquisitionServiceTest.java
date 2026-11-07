package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.application.mapper.AcquisitionMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.AcquisitionValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Test
    void registerNonInventoryAffectingAcquisitionPropagatesTheUnitOfMeasureToTheExpense() {
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setAcquisitionTypeOid("type-1");
        request.setProductName("Fuel");
        request.setRealCost(100.0);
        request.setUnitOfMeasure("uom-1");

        AcquisitionType acquisitionType = AcquisitionType.builder()
                .id("type-1")
                .name("Fuel")
                .byUserOid("user-1")
                .affectsInventory(false)
                .build();
        Acquisition savedAcquisition = Acquisition.builder()
                .id("a-1")
                .acquisitionTypeOid("type-1")
                .productName("Fuel")
                .unitOfMeasure("uom-1")
                .quantity(1)
                .realCost(100.0)
                .acquisitionDate(LocalDateTime.now())
                .byUserOid("user-1")
                .build();

        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(acquisitionTypeRepository.findById("type-1")).thenReturn(Optional.of(acquisitionType));
        when(acquisitionMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(savedAcquisition);
        when(acquisitionRepository.save(any())).thenReturn(savedAcquisition);
        when(acquisitionMapper.toResponse(any(), any(), any())).thenReturn(new AcquisitionResponse());

        acquisitionService.registerAcquisition(request);

        ArgumentCaptor<CreateExpenseRequest> captor = ArgumentCaptor.forClass(CreateExpenseRequest.class);
        verify(expenseService).createExpense(captor.capture());
        assertEquals("uom-1", captor.getValue().getUnitOfMeasure());
    }
}
