package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.AcquisitionConstants;
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
 * replenishment, pack conversion and cost history recording.
 */
@ExtendWith(MockitoExtension.class)
class ProductInventoryHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCostHistoryRepository productCostHistoryRepository;

    private ProductInventoryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ProductInventoryHandler(
                productRepository,
                productCostHistoryRepository,
                new ProductValidator(new FieldValidator()),
                new PackConversionService()
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

    private ProductCostHistory history(Double realCost, Integer quantity) {
        return ProductCostHistory.builder()
                .productOid("p-1")
                .acquisitionOid("a-old")
                .unitOfMeasureOid("uom-old")
                .quantity(quantity)
                .remainingQuantity(quantity)
                .realCost(realCost)
                .unitRealCost(realCost / quantity)
                .acquisitionDate(LocalDateTime.now().minusDays(1))
                .byUserOid("user-1")
                .createdDate(LocalDateTime.now().minusDays(1))
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
                    .packSize(toSave.getPackSize())
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
                handler.applyToInventory(request(50.0, 5), "user-1", CompanyOid.of("company-1"), false);

        assertEquals("p-new", outcome.productOid());
        assertEquals("Cafe", outcome.productName());
        assertEquals(10.0, outcome.averageUnitRealCost());
        assertEquals(5, outcome.baseQuantity());
        assertEquals(null, outcome.packSize());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getStock());
        assertEquals("1", captor.getValue().getCode());
        assertEquals(List.of("uom-1"), captor.getValue().getUnitOfMeasures());
        assertEquals(CompanyOid.of("company-1"), captor.getValue().getCompanyOid());
    }

    @Test
    void applyToInventoryCreatesProductWithTheAcquiredPackSize() {
        CreateAcquisitionRequest request = request(120.0, 2);
        request.setPackSize(12);
        when(productRepository.findByNameAndIsActiveTrue("Cafe")).thenReturn(Optional.empty());
        when(productRepository.findAll(any(CompanyOid.class))).thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(invocation -> {
            Product toSave = invocation.getArgument(0);
            return Product.builder()
                    .id("p-new")
                    .name(toSave.getName())
                    .description(toSave.getDescription())
                    .unitOfMeasures(toSave.getUnitOfMeasures())
                    .packSize(toSave.getPackSize())
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
                handler.applyToInventory(request, "user-1", CompanyOid.of("company-1"), true);

        assertEquals(24, outcome.baseQuantity());
        assertEquals(12, outcome.packSize());
        assertEquals(5.0, outcome.averageUnitRealCost());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(24, captor.getValue().getStock());
        assertEquals(12, captor.getValue().getPackSize());
    }

    @Test
    void applyToInventoryRequiresUnitsOfMeasureWhenCreatingAProduct() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe")).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> handler.applyToInventory(
                        request(50.0, 5, null), "user-1", CompanyOid.of("company-1"), false)
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
        verify(productRepository, never()).save(any());
    }

    @Test
    void applyToInventoryRejectsAPackQuantityWithoutPackSize() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe")).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> handler.applyToInventory(
                        request(50.0, 5), "user-1", CompanyOid.of("company-1"), true)
        );

        assertTrue(exception.getDetails().contains(AcquisitionConstants.PACK_SIZE_REQUIRED));
        verify(productRepository, never()).save(any());
    }

    @Test
    void applyToInventoryReplenishesExistingProductWithWeightedAverage() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(productCostHistoryRepository.findByProductOid("p-1")).thenReturn(List.of(
                history(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductInventoryHandler.InventoryOutcome outcome =
                handler.applyToInventory(request(60.0, 6), "user-1", CompanyOid.of("company-1"), false);

        assertEquals("p-1", outcome.productOid());
        assertEquals(10.0, outcome.averageUnitRealCost());
        assertEquals(6, outcome.baseQuantity());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(10, captor.getValue().getStock());
        assertEquals(100.0, captor.getValue().getRealCost());
        assertEquals(20.0, captor.getValue().getUnitPublicCost());
        assertEquals(List.of("uom-existing"), captor.getValue().getUnitOfMeasures());
    }

    @Test
    void applyToInventoryReplenishesUsingTheBaseUnitsOfThePack() {
        CreateAcquisitionRequest request = request(120.0, 2);
        request.setPackSize(10);
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(productCostHistoryRepository.findByProductOid("p-1")).thenReturn(List.of(
                history(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductInventoryHandler.InventoryOutcome outcome =
                handler.applyToInventory(request, "user-1", CompanyOid.of("company-1"), true);

        assertEquals(20, outcome.baseQuantity());
        assertEquals(10, outcome.packSize());
        assertEquals(6.67, outcome.averageUnitRealCost());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(24, captor.getValue().getStock());
        assertEquals(10, captor.getValue().getPackSize());
    }

    @Test
    void applyToInventoryUsesTheProductPackSizeWhenTheAcquisitionOmitsIt() {
        CreateAcquisitionRequest request = request(120.0, 2);
        Product productWithPack = Product.builder()
                .id("p-1")
                .name("Cafe")
                .unitOfMeasures(List.of("uom-existing"))
                .packSize(8)
                .stock(4)
                .code("7")
                .realCost(40.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .byUserOid("user-1")
                .build();
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(productWithPack));
        when(productCostHistoryRepository.findByProductOid("p-1")).thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductInventoryHandler.InventoryOutcome outcome =
                handler.applyToInventory(request, "user-1", CompanyOid.of("company-1"), true);

        assertEquals(16, outcome.baseQuantity());
        assertEquals(8, outcome.packSize());
    }

    @Test
    void applyToInventoryKeepsExistingUnitsOfMeasureWhenTheAcquisitionOmitsThem() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(productCostHistoryRepository.findByProductOid("p-1")).thenReturn(List.of(
                history(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        handler.applyToInventory(request(60.0, 6, null), "user-1", CompanyOid.of("company-1"), false);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(List.of("uom-existing"), captor.getValue().getUnitOfMeasures());
    }

    @Test
    void applyToInventoryKeepsExistingUnitsOfMeasureWhenTheAcquisitionSendsAnEmptyList() {
        when(productRepository.findByNameAndIsActiveTrue("Cafe"))
                .thenReturn(Optional.of(existingProduct()));
        when(productCostHistoryRepository.findByProductOid("p-1")).thenReturn(List.of(
                history(40.0, 4)
        ));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        handler.applyToInventory(request(60.0, 6, List.of()), "user-1", CompanyOid.of("company-1"), false);

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
                .unitOfMeasureOid("uom-oid-1")
                .packSize(10)
                .quantity(5)
                .realCost(50.0)
                .unitRealCost(10.0)
                .acquisitionDate(LocalDateTime.of(2026, 8, 21, 10, 0))
                .byUserOid("user-1")
                .build();
        ProductInventoryHandler.InventoryOutcome outcome =
                new ProductInventoryHandler.InventoryOutcome("p-1", "Cafe", 10.0, 10, 50);

        handler.recordCostHistory("p-1", request, saved, outcome);

        ArgumentCaptor<ProductCostHistory> captor = ArgumentCaptor.forClass(ProductCostHistory.class);
        verify(productCostHistoryRepository).save(captor.capture());
        ProductCostHistory history = captor.getValue();
        assertEquals("p-1", history.getProductOid());
        assertEquals("a-1", history.getAcquisitionOid());
        assertEquals("uom-oid-1", history.getUnitOfMeasureOid());
        assertEquals(10, history.getPackSize());
        assertEquals(50, history.getQuantity());
        assertEquals(50, history.getRemainingQuantity());
        assertEquals(10.0, history.getUnitRealCost());
    }

    @Test
    void recordCostHistoryFallsBackToTheAcquisitionUnitOfMeasure() {
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
        ProductInventoryHandler.InventoryOutcome outcome =
                new ProductInventoryHandler.InventoryOutcome("p-1", "Cafe", 10.0, null, 5);

        handler.recordCostHistory("p-1", request, saved, outcome);

        ArgumentCaptor<ProductCostHistory> captor = ArgumentCaptor.forClass(ProductCostHistory.class);
        verify(productCostHistoryRepository).save(captor.capture());
        assertEquals("uom-1", captor.getValue().getUnitOfMeasureOid());
        assertEquals(5, captor.getValue().getQuantity());
    }
}
