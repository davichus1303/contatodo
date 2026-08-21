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
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InsufficientStockException;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleService(
                saleRepository, productRepository, userRepository,
                saleValidator, saleMapper, authenticatedUserProvider
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
                .userOid("user-1")
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

        assertThrows(ProductNotFoundException.class, () -> saleService.createSale(saleRequest(40.0)));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void createSalePlacesSaleAndDecreasesStock() {
        stubAuthenticatedContext();
        when(productRepository.findById("product-1")).thenReturn(Optional.of(productWithStock(5)));
        when(saleRepository.findByUserOidAndSaleDateBetween(any(), any(), any())).thenReturn(java.util.List.of());
        when(saleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        SaleResponse expected = new SaleResponse();
        when(saleMapper.toResponse(any())).thenReturn(expected);

        SaleResponse response = saleService.createSale(saleRequest(40.0));

        verify(productRepository).save(any());
        verify(saleRepository).save(any());
        assertEquals(expected, response);
    }
}
