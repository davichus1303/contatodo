package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.SaleValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InsufficientStockException;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SaleService} covering stock and profit rules.
 */
@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SaleValidator saleValidator;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private CompanyService companyService;

    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleService(
                saleRepository, productRepository, userRepository,
                saleValidator, saleMapper, authenticatedUserProvider, companyService
        );
    }

    private Product productWithStock(int stock) {
        return Product.builder()
                .name("Cafe")
                .stock(stock)
                .code("1")
                .realCost(10.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .byUserOid("user-1")
                .build();
    }

    private CreateSaleRequest saleRequest(Double totalSalePrice) {
        CreateSaleRequest request = new CreateSaleRequest();
        request.setProductOid("product-1");
        request.setQuantity(2);
        request.setTotalSalePrice(totalSalePrice);
        request.setNotes(null);
        return request;
    }

    private void stubAuthenticatedContext() {
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(
                User.builder().id("user-1").userName("david").email("d@example.com").password("x").name("D").build()
        ));
    }

    @Test
    void createSaleRejectsSaleWithoutStock() {
        stubAuthenticatedContext();
        when(productRepository.findById("product-1")).thenReturn(Optional.of(productWithStock(0)));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> saleService.createSale(saleRequest(40.0))
        );
        assertEquals(SaleConstants.PRODUCT_OUT_OF_STOCK, exception.getMessage());
        verify(saleRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createSaleRejectsUnknownProduct() {
        stubAuthenticatedContext();
        when(productRepository.findById("product-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(saleRequest(40.0)));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void getTodaySalesDelegatesCompanyResolutionAndQueriesWithTheResult() {
        when(companyService.resolveReadCompanyOid("param-company")).thenReturn(CompanyOid.of("resolved-company"));
        when(saleRepository.findBySaleDate(eq(CompanyOid.of("resolved-company")), any())).thenReturn(java.util.List.of());
        when(saleMapper.toResponseList(any())).thenReturn(java.util.List.of());

        saleService.getTodaySales("param-company");

        verify(companyService).resolveReadCompanyOid("param-company");
        verify(saleRepository).findBySaleDate(eq(CompanyOid.of("resolved-company")), any());
    }

    @Test
    void getTodaySalesPropagatesAnUnresolvableCompany() {
        when(companyService.resolveReadCompanyOid(any())).thenThrow(
                new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED)
        );

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> saleService.getTodaySales("anything")
        );

        assertEquals(AuthConstants.COMPANY_CONTEXT_REQUIRED, exception.getMessage());
        verify(saleRepository, never()).findBySaleDate(any(), any());
    }

    @Test
    void getSalesByDateRangeDelegatesCompanyResolutionAndQueriesWithTheResult() {
        when(companyService.resolveReadCompanyOid("param-company")).thenReturn(CompanyOid.of("param-company"));
        when(saleRepository.findBySaleDateBetween(eq(CompanyOid.of("param-company")), any(), any()))
                .thenReturn(java.util.List.of());
        when(saleMapper.toResponseList(any())).thenReturn(java.util.List.of());

        saleService.getSalesByDateRange("param-company", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        verify(companyService).resolveReadCompanyOid("param-company");
        verify(saleRepository).findBySaleDateBetween(eq(CompanyOid.of("param-company")), any(), any());
    }

    @Test
    void createSalePlacesSaleAndDecreasesStock() {
        stubAuthenticatedContext();
        when(productRepository.findById("product-1")).thenReturn(Optional.of(productWithStock(5)));
        when(saleRepository.findBySaleDateBetween(any(), any(), any())).thenReturn(java.util.List.of());
        when(saleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        SaleResponse expected = new SaleResponse();
        when(saleMapper.toResponse(any())).thenReturn(expected);

        SaleResponse response = saleService.createSale(saleRequest(40.0));

        verify(productRepository).save(any());
        verify(saleRepository).save(any());
        assertEquals(expected, response);
    }
}
