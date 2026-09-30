package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.mapper.ProductMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.validators.CompanyOidValidator;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductValidator productValidator;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private CompanyContextProvider companyContextProvider;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepository, userRepository, productValidator,
                productMapper, authenticatedUserProvider, companyContextProvider, new CompanyOidValidator(companyRepository)
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
    void createProductUsesTheRequestedCompanyWhenTheTokenHasNone() {
        CreateProductRequest request = createRequest();
        request.setCompanyOid("company-9");
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(companyRepository.findById("company-9")).thenReturn(Optional.of(Company.builder().id("company-9").name("Acme").isActive(true).isDeleted(false).build()));
        when(productRepository.findTopByOrderByCodeDesc()).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.createProduct(request);

        verify(productMapper).toEntity(any(), eq("1"), eq("user-1"), eq(CompanyOid.of("company-9")));
    }

    @Test
    void createProductRejectsARequestedCompanyThatDoesNotExist() {
        CreateProductRequest request = createRequest();
        request.setCompanyOid("company-404");
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(companyRepository.findById("company-404")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.createProduct(request)
        );

        assertTrue(exception.getMessage().contains("company-404"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProductIgnoresTheRequestedCompanyWhenTheTokenHasOne() {
        CreateProductRequest request = createRequest();
        request.setCompanyOid("company-9");
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid())
                .thenReturn(Optional.of(CompanyOid.of("company-1")));
        when(productRepository.findTopByOrderByCodeDesc()).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.createProduct(request);

        verify(productMapper).toEntity(any(), eq("1"), eq("user-1"), eq(CompanyOid.of("company-1")));
    }

    @Test
    void createProductIgnoresABlankRequestedCompany() {
        CreateProductRequest request = createRequest();
        request.setCompanyOid("   ");
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(productRepository.findTopByOrderByCodeDesc()).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.createProduct(request);

        verify(productMapper).toEntity(any(), eq("1"), eq("user-1"), isNull());
    }

    @Test
    void createProductRejectsUnknownAuthenticatedUser() {
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.createProduct(createRequest())
        );
        assertEquals(ProductConstants.USER_NOT_FOUND, exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProductAssignsCompanyToNormalUser() {
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid())
                .thenReturn(Optional.of(CompanyOid.of("company-1")));
        when(productRepository.findTopByOrderByCodeDesc()).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.createProduct(createRequest());

        verify(productMapper).toEntity(any(), eq("1"), eq("user-1"), eq(CompanyOid.of("company-1")));
    }

    @Test
    void createProductAssignsNullCompanyWhenTheTokenCarriesNoCompany() {
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(productRepository.findTopByOrderByCodeDesc()).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.createProduct(createRequest());

        verify(productMapper).toEntity(any(), eq("1"), eq("user-1"), isNull());
    }

    @Test
    void createProductAssignsNullCompanyToRoot() {
        when(authenticatedUserProvider.getCurrentUserEmail()).thenReturn("david@example.com");
        when(userRepository.findByEmail("david@example.com")).thenReturn(Optional.of(owner()));
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(productRepository.findTopByOrderByCodeDesc()).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.createProduct(createRequest());

        verify(productMapper).toEntity(any(), eq("1"), eq("user-1"), isNull());
    }

    @Test
    void getProductByCodeUsesTheCompanyFromTheTokenWhenPresent() {
        Product product = Product.builder()
                .id("p-1")
                .name("Cafe")
                .stock(5)
                .code("42")
                .realCost(50.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .byUserOid("user-1")
                .build();
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.of(CompanyOid.of("claim-company")));
        when(productRepository.findByCode(eq(CompanyOid.of("claim-company")), eq("42"))).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.getProductByCode("param-company", "42");

        verify(productRepository).findByCode(eq(CompanyOid.of("claim-company")), eq("42"));
        verify(productRepository, never()).findByCode(eq(CompanyOid.of("param-company")), any());
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductByCodeUsesTheRequestedCompanyWhenTheTokenHasNone() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(companyRepository.findById("param-company")).thenReturn(Optional.of(
                Company.builder().id("param-company").name("Acme").isActive(true).isDeleted(false).build()
        ));
        when(productRepository.findByCode(eq(CompanyOid.of("param-company")), eq("42"))).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductByCode("param-company", "42")
        );

        verify(productRepository).findByCode(eq(CompanyOid.of("param-company")), eq("42"));
    }

    @Test
    void getProductByUnknownCodeThrowsNotFound() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.of(CompanyOid.of("claim-company")));
        when(productRepository.findByCode(eq(CompanyOid.of("claim-company")), eq("999"))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductByCode("param-company", "999")
        );
        assertEquals(ProductConstants.PRODUCT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getAllProductsUsesTheCompanyFromTheTokenWhenPresent() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.of(CompanyOid.of("claim-company")));
        when(productRepository.findAll(eq(CompanyOid.of("claim-company")))).thenReturn(java.util.List.of());
        when(productMapper.toResponseList(any())).thenReturn(java.util.List.of());

        productService.getAllProducts("param-company");

        verify(productRepository).findAll(eq(CompanyOid.of("claim-company")));
        verify(productRepository, never()).findAll(eq(CompanyOid.of("param-company")));
    }

    @Test
    void getAllProductsUsesTheRequestedCompanyWhenTheTokenHasNone() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(companyRepository.findById("param-company")).thenReturn(Optional.of(
                Company.builder().id("param-company").name("Acme").isActive(true).isDeleted(false).build()
        ));
        when(productRepository.findAll(eq(CompanyOid.of("param-company")))).thenReturn(java.util.List.of());
        when(productMapper.toResponseList(any())).thenReturn(java.util.List.of());

        productService.getAllProducts("param-company");

        verify(productRepository).findAll(eq(CompanyOid.of("param-company")));
    }

    @Test
    void getAllProductsRejectsABlankCompanyWhenTheTokenHasNone() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getAllProducts("   ")
        );
        assertEquals(AuthConstants.COMPANY_CONTEXT_REQUIRED, exception.getMessage());
        verify(productRepository, never()).findAll(any());
    }

    @Test
    void getAllProductsRejectsAnInactiveCompanyWhenTheTokenHasNone() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(companyRepository.findById("inactive-company")).thenReturn(Optional.of(
                Company.builder().id("inactive-company").name("Dormant").isActive(false).isDeleted(false).build()
        ));

        assertThrows(ResourceNotFoundException.class, () -> productService.getAllProducts("inactive-company"));
        verify(productRepository, never()).findAll(any());
    }

    @Test
    void getProductsByNameUsesTheRequestedCompanyWhenTheTokenHasNone() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.empty());
        when(companyRepository.findById("param-company")).thenReturn(Optional.of(
                Company.builder().id("param-company").name("Acme").isActive(true).isDeleted(false).build()
        ));
        when(productRepository.findByName(eq(CompanyOid.of("param-company")), eq("Cafe"))).thenReturn(java.util.List.of());
        when(productMapper.toResponseList(any())).thenReturn(java.util.List.of());

        productService.getProductsByName("param-company", "Cafe");

        verify(productRepository).findByName(eq(CompanyOid.of("param-company")), eq("Cafe"));
    }

    @Test
    void getAvailableProductsUsesTheCompanyFromTheTokenWhenPresent() {
        when(companyContextProvider.currentCompanyOid()).thenReturn(Optional.of(CompanyOid.of("claim-company")));
        when(productRepository.findByStockGreaterThan(eq(CompanyOid.of("claim-company")), eq(0)))
                .thenReturn(java.util.List.of());
        when(productMapper.toResponseList(any())).thenReturn(java.util.List.of());

        productService.getAvailableProducts("param-company");

        verify(productRepository).findByStockGreaterThan(eq(CompanyOid.of("claim-company")), eq(0));
    }

    @Test
    void updateProductPassesTheAuthenticatedUserAsEditor() {
        Product existing = Product.builder()
                .id("p-1")
                .name("Cafe")
                .stock(5)
                .code("42")
                .byUserOid("creator-1")
                .build();
        when(productRepository.findById("p-1")).thenReturn(Optional.of(existing));
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("editor-1");
        when(productMapper.applyUpdate(any(), any(), eq("editor-1"))).thenReturn(existing);
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any())).thenReturn(new com.contatodo.application.dto.response.ProductResponse());

        productService.updateProduct("p-1", new com.contatodo.application.dto.request.UpdateProductRequest());

        verify(productMapper).applyUpdate(eq(existing), any(), eq("editor-1"));
    }
}
