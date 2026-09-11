package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.mapper.ProductMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.ProductConstants;
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
 * Unit tests for {@link ProductService} covering creation and lookup rules.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductValidator productValidator;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepository, userRepository, productValidator,
                productMapper, authenticatedUserProvider
        );
    }

    private User owner() {
        return User.builder()
                .id("user-1")
                .userName("david")
                .email("david@example.com")
                .password("hashed")
                .name("David")
                .build();
    }

    private CreateProductRequest createRequest() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cafe");
        request.setStock(10);
        return request;
    }

    @Test
    void createProductRejectsUnknownAuthenticatedUser() {
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.createProduct(createRequest())
        );
        assertEquals(ProductConstants.USER_NOT_FOUND, exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductByCodeReturnsMappedProduct() {
        Product product = Product.builder()
                .id("p-1")
                .name("Cafe")
                .stock(5)
                .code("42")
                .realCost(50.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .userOid("user-1")
                .build();
        when(productRepository.findByCode("42")).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.getProductByCode("42");

        verify(productRepository).findByCode("42");
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductByUnknownCodeThrowsNotFound() {
        when(productRepository.findByCode("999")).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductByCode("999")
        );
        assertEquals(ProductConstants.PRODUCT_NOT_FOUND, exception.getMessage());
    }
}
