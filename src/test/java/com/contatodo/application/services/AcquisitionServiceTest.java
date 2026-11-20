package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.application.mapper.AcquisitionMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.AcquisitionValidator;
import com.contatodo.application.validators.UnitOfMeasureValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
    private UnitOfMeasureRepository unitOfMeasureRepository;

    private UnitOfMeasureValidator unitOfMeasureValidator;

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
        unitOfMeasureValidator = new UnitOfMeasureValidator(unitOfMeasureRepository);
        acquisitionService = new AcquisitionService(
                acquisitionRepository,
                productRepository,
                acquisitionTypeRepository,
                acquisitionValidator,
                unitOfMeasureValidator,
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
        when(unitOfMeasureRepository.findActiveByCompany(CompanyOid.of("company-1")))
                .thenReturn(List.of(unitOfMeasure("uom-1")));
        when(acquisitionMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(savedAcquisition);
        when(acquisitionRepository.save(any())).thenReturn(savedAcquisition);
        when(acquisitionMapper.toResponse(any(), any(), any())).thenReturn(new AcquisitionResponse());

        acquisitionService.registerAcquisition(request);

        ArgumentCaptor<CreateExpenseRequest> captor = ArgumentCaptor.forClass(CreateExpenseRequest.class);
        verify(expenseService).createExpense(captor.capture());
        assertEquals("uom-1", captor.getValue().getUnitOfMeasure());
    }

    @Test
    void registerAcquisitionRejectsAnUnknownUnitOfMeasure() {
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setAcquisitionTypeOid("type-1");
        request.setProductName("Fuel");
        request.setRealCost(100.0);
        request.setUnitOfMeasure("uom-missing");

        AcquisitionType acquisitionType = AcquisitionType.builder()
                .id("type-1")
                .name("Fuel")
                .byUserOid("user-1")
                .affectsInventory(false)
                .build();

        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(acquisitionTypeRepository.findById("type-1")).thenReturn(Optional.of(acquisitionType));
        when(unitOfMeasureRepository.findActiveByCompany(CompanyOid.of("company-1")))
                .thenReturn(List.of(unitOfMeasure("uom-1")));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> acquisitionService.registerAcquisition(request)
        );

        assertTrue(exception.getDetails().contains("Unit of measure not found: uom-missing"));
        verify(acquisitionRepository, never()).save(any());
        verify(expenseService, never()).createExpense(any());
    }

    @Test
    void registerAcquisitionRejectsUnknownProductUnitsOfMeasure() {
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setAcquisitionTypeOid("type-1");
        request.setProductName("Fuel");
        request.setQuantity(5);
        request.setRealCost(100.0);
        request.setUnitOfMeasure("uom-1");
        request.setUnitOfMeasures(List.of("uom-1", "uom-missing"));

        AcquisitionType acquisitionType = AcquisitionType.builder()
                .id("type-1")
                .name("Fuel")
                .byUserOid("user-1")
                .affectsInventory(true)
                .build();

        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid(any())).thenReturn(CompanyOid.of("company-1"));
        when(acquisitionTypeRepository.findById("type-1")).thenReturn(Optional.of(acquisitionType));
        when(unitOfMeasureRepository.findActiveByCompany(CompanyOid.of("company-1")))
                .thenReturn(List.of(unitOfMeasure("uom-1")));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> acquisitionService.registerAcquisition(request)
        );

        assertTrue(exception.getDetails().contains("Unit of measure not found: uom-missing"));
        verify(productInventoryHandler, never()).applyToInventory(any(), any(), any(), anyBoolean());
        verify(acquisitionRepository, never()).save(any());
    }

    @Test
    void registerInventoryAcquisitionUsesTheConfiguredPackUnitAndPersistsIt() {
        CompanyOid companyOid = CompanyOid.of("company-1");
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setAcquisitionTypeOid("type-1");
        request.setProductName("Cafe");
        request.setQuantity(2);
        request.setPackSize(12);
        request.setRealCost(120.0);
        request.setUnitOfMeasure("uom-pack");
        request.setUnitOfMeasureOid("uom-pack");
        request.setUnitOfMeasures(List.of("uom-pack"));

        AcquisitionType acquisitionType = AcquisitionType.builder()
                .id("type-1")
                .name("Compra")
                .byUserOid("user-1")
                .affectsInventory(true)
                .build();
        Acquisition savedAcquisition = Acquisition.builder()
                .id("a-1")
                .acquisitionTypeOid("type-1")
                .productName("Cafe")
                .unitOfMeasure("uom-pack")
                .unitOfMeasureOid("uom-pack")
                .packSize(12)
                .quantity(2)
                .realCost(120.0)
                .acquisitionDate(LocalDateTime.now())
                .byUserOid("user-1")
                .build();
        ProductInventoryHandler.InventoryOutcome outcome =
                new ProductInventoryHandler.InventoryOutcome("p-1", "Cafe", 5.0, 12, 24);

        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid(any())).thenReturn(companyOid);
        when(acquisitionTypeRepository.findById("type-1")).thenReturn(Optional.of(acquisitionType));
        when(unitOfMeasureRepository.findActiveByCompany(companyOid))
                .thenReturn(List.of(unitOfMeasure("uom-pack", true)));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(companyOid, "uom-pack"))
                .thenReturn(Optional.of(unitOfMeasure("uom-pack", true)));
        when(productInventoryHandler.applyToInventory(request, "user-1", companyOid, true))
                .thenReturn(outcome);
        when(acquisitionMapper.toEntity(
                request, "p-1", "user-1", 5.0, companyOid, 12, "uom-pack"))
                .thenReturn(savedAcquisition);
        when(acquisitionRepository.save(any())).thenReturn(savedAcquisition);
        when(acquisitionMapper.toResponse(any(), any(), any())).thenReturn(new AcquisitionResponse());

        acquisitionService.registerAcquisition(request);

        verify(productInventoryHandler).applyToInventory(request, "user-1", companyOid, true);
        verify(acquisitionMapper).toEntity(
                request, "p-1", "user-1", 5.0, companyOid, 12, "uom-pack");
        verify(productInventoryHandler).recordCostHistory("p-1", request, savedAcquisition, outcome);
    }

    @Test
    void registerInventoryAcquisitionFallsBackToTheAcquisitionUnitOfMeasure() {
        CompanyOid companyOid = CompanyOid.of("company-1");
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setAcquisitionTypeOid("type-1");
        request.setProductName("Cafe");
        request.setQuantity(2);
        request.setRealCost(20.0);
        request.setUnitOfMeasure("uom-1");
        request.setUnitOfMeasures(List.of("uom-1"));

        AcquisitionType acquisitionType = AcquisitionType.builder()
                .id("type-1")
                .name("Compra")
                .byUserOid("user-1")
                .affectsInventory(true)
                .build();
        Acquisition savedAcquisition = Acquisition.builder()
                .id("a-1")
                .acquisitionTypeOid("type-1")
                .productName("Cafe")
                .unitOfMeasure("uom-1")
                .unitOfMeasureOid("uom-1")
                .quantity(2)
                .realCost(20.0)
                .acquisitionDate(LocalDateTime.now())
                .byUserOid("user-1")
                .build();
        ProductInventoryHandler.InventoryOutcome outcome =
                new ProductInventoryHandler.InventoryOutcome("p-1", "Cafe", 10.0, null, 2);

        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(companyService.resolveCompanyOid(any())).thenReturn(companyOid);
        when(acquisitionTypeRepository.findById("type-1")).thenReturn(Optional.of(acquisitionType));
        when(unitOfMeasureRepository.findActiveByCompany(companyOid))
                .thenReturn(List.of(unitOfMeasure("uom-1")));
        when(unitOfMeasureRepository.findActiveByCompanyAndOid(companyOid, "uom-1"))
                .thenReturn(Optional.of(unitOfMeasure("uom-1")));
        when(productInventoryHandler.applyToInventory(request, "user-1", companyOid, false))
                .thenReturn(outcome);
        when(acquisitionMapper.toEntity(
                request, "p-1", "user-1", 10.0, companyOid, null, "uom-1"))
                .thenReturn(savedAcquisition);
        when(acquisitionRepository.save(any())).thenReturn(savedAcquisition);
        when(acquisitionMapper.toResponse(any(), any(), any())).thenReturn(new AcquisitionResponse());

        acquisitionService.registerAcquisition(request);

        verify(productInventoryHandler).applyToInventory(request, "user-1", companyOid, false);
        verify(acquisitionMapper).toEntity(
                request, "p-1", "user-1", 10.0, companyOid, null, "uom-1");
    }

    private UnitOfMeasure unitOfMeasure(String id) {
        return unitOfMeasure(id, false);
    }

    private UnitOfMeasure unitOfMeasure(String id, boolean isPack) {
        return UnitOfMeasure.builder()
                .id(id)
                .name("Kilogramo")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .isPack(isPack)
                .build();
    }
}
