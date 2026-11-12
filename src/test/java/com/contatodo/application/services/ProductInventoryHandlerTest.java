package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProductInventoryHandler} covering product creation,
 * replenishment and cost history recording.
 */
@ExtendWith(MockitoExtension.class)
class ProductInventoryHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AcquisitionRepository acquisitionRepository;

    @Mock
    private ProductCostHistoryRepository productCostHistoryRepository;

    private ProductInventoryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ProductInventoryHandler(
                productRepository,
                acquisitionRepository,
                productCostHistoryRepository,
                new ProductValidator(new FieldValidator())
        );
    }

    private CreateAcquisitionRequest request(Double realCost, Integer quantity) {
        return request(realCost, quantity, List.of("uom-1"));
    }

    private CreateAcquisitionRequest request(Double realCost, Integer quantity, List<String> unitOfMeasures) {
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setProductName("Cafe");
        request.setDescription("Grano");
        request.setRealCost(realCost);
        request.setQuantity(quantity);
        request.setUnitPublicCost(20.0);
        request.setUnitOfMeasures(unitOfMeasures);
        request.setUnitOfMeasure("uom-acq");
        return request;
    }

    private Product existingProduct() {
        return Product.builder()
                .id("p-1")
                .name("Cafe")
                .unitOfMeasures(List.of("uom-existing"))
                .stock(4)
                .code("7")
                .realCost(40.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .byUserOid("user-1")
                .build();
    }

    @Test
    void applyToInventoryCreatesProductWhenMissing() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe")).thenReturn(Optional.empty());
        when(productRepository.findAll(any(CompanyOid.class))).thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(invocation -> {
            Product toSave = invocation.getArgument(0);
            return Product.builder()
                    .id("p-new")
                    .name(toSave.getName())
                    .description(toSave.getDescription())
                    .unitOfMeasures(toSave.getUnitOfMeasures())
                    .stock(toSave.getStock())
                    .code(toSave.getCode())
                    .realCost(toSave.getRealCost())
                    .unitRealCost(toSave.getUnitRealCost())
                    .unitPublicCost(toSave.getUnitPublicCost())
                    .isActive(true)
                    .byUserOid(toSave.getByUserOid())
                    .companyOid(toSave.getCompanyOid())
                    .build();
        });

        ProductInventoryHandler.InventoryOutcome outcome =
                handler.applyToInventory(request(50.0, 5), "user-1", CompanyOid.of("company-1"));

        assertEquals("p-new", outcome.productOid());
        assertEquals("Cafe", outcome.productName());
        assertEquals(10.0, outcome.averageUnitRealCost());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getStock());
        assertEquals("1", captor.getValue().getCode());
        assertEquals(List.of("uom-1"), captor.getValue().getUnitOfMeasures());
        assertEquals(CompanyOid.of("company-1"), captor.getValue().getCompanyOid());
    }

    @Test
    void applyToInventoryRequiresUnitsOfMeasureWhenCreatingAProduct() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe")).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> handler.applyToInventory(request(50.0, 5, null), "user-1", CompanyOid.of("company-1"))
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
        verify(productRepository, never()).save(any());
    }

    @Test
    void applyToInventoryReplenishesExistingProductWithWeightedAverage() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(acquisitionRepository.findByProductOid("p-1")).thenReturn(List.of(
                acquisition(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductInventoryHandler.InventoryOutcome outcome =
                handler.applyToInventory(request(60.0, 6), "user-1", CompanyOid.of("company-1"));

        assertEquals("p-1", outcome.productOid());
        assertEquals(10.0, outcome.averageUnitRealCost());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(10, captor.getValue().getStock());
        assertEquals(100.0, captor.getValue().getRealCost());
        assertEquals(20.0, captor.getValue().getUnitPublicCost());
        assertEquals(List.of("uom-existing"), captor.getValue().getUnitOfMeasures());
    }

    @Test
    void applyToInventoryKeepsExistingUnitsOfMeasureWhenTheAcquisitionOmitsThem() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(acquisitionRepository.findByProductOid("p-1")).thenReturn(List.of(
                acquisition(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        handler.applyToInventory(request(60.0, 6, null), "user-1", CompanyOid.of("company-1"));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(List.of("uom-existing"), captor.getValue().getUnitOfMeasures());
    }

    @Test
    void applyToInventoryKeepsExistingUnitsOfMeasureWhenTheAcquisitionSendsAnEmptyList() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(acquisitionRepository.findByProductOid("p-1")).thenReturn(List.of(
                acquisition(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        handler.applyToInventory(request(60.0, 6, List.of()), "user-1", CompanyOid.of("company-1"));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(List.of("uom-existing"), captor.getValue().getUnitOfMeasures());
    }

    @Test
    void recordCostHistorySavesEntryForAcquisition() {
        CreateAcquisitionRequest request = request(50.0, 5);
        Acquisition saved = Acquisition.builder()
                .id("a-1")
                .productOid("p-1")
                .acquisitionTypeOid("t-1")
                .unitOfMeasure("uom-1")
                .quantity(5)
                .realCost(50.0)
                .unitRealCost(10.0)
                .acquisitionDate(LocalDateTime.of(2026, 8, 21, 10, 0))
                .byUserOid("user-1")
                .build();

        handler.recordCostHistory("p-1", request, saved, 10.0);

        ArgumentCaptor<ProductCostHistory> captor = ArgumentCaptor.forClass(ProductCostHistory.class);
        verify(productCostHistoryRepository).save(captor.capture());
        ProductCostHistory history = captor.getValue();
        assertEquals("p-1", history.getProductOid());
        assertEquals("a-1", history.getAcquisitionOid());
        assertEquals("uom-1", history.getUnitOfMeasureOid());
        assertEquals(5, history.getRemainingQuantity());
        assertEquals(10.0, history.getUnitRealCost());
    }

    private Acquisition acquisition(Double realCost, int quantity) {
        return Acquisition.builder()
                .id("a-old")
                .productOid("p-1")
                .acquisitionTypeOid("t-1")
                .unitOfMeasure("uom-1")
                .quantity(quantity)
                .realCost(realCost)
                .unitRealCost(realCost / quantity)
                .acquisitionDate(LocalDateTime.now().minusDays(1))
                .byUserOid("user-1")
                .build();
    }
}
