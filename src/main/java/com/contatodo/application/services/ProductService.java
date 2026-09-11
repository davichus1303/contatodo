package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.application.mapper.ProductMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service containing product business logic.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductValidator productValidator;
    private final ProductMapper productMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    /**
     * Creates a product service.
     *
     * @param productRepository Product repository port.
     * @param userRepository User repository port.
     * @param productValidator Product validator.
     * @param productMapper Product mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     */
    public ProductService(
            ProductRepository productRepository,
            UserRepository userRepository,
            ProductValidator productValidator,
            ProductMapper productMapper,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productValidator = productValidator;
        this.productMapper = productMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Creates a new product with auto-incremented code.
     *
     * @param request Create product request.
     * @return Created product response.
     */
    public ProductResponse createProduct(CreateProductRequest request) {
        productValidator.validateCreateRequest(request);

        String userEmail = authenticatedUserProvider.getCurrentUserEmail();
        String userOid = userRepository.findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new ProductNotFoundException(ProductConstants.USER_NOT_FOUND));

        String nextCode = generateNextCode();
        Product product = productMapper.toEntity(request, nextCode, userOid);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    /**
     * Updates an existing product.
     *
     * @param id Product identifier.
     * @param request Update product request.
     * @return Updated product response.
     */
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        productValidator.validateUpdateRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(ProductConstants.PRODUCT_NOT_FOUND));

        Product updatedProduct = productRepository.save(productMapper.applyUpdate(product, request));
        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Retrieves all products.
     *
     * @return List of product responses.
     */
    public List<ProductResponse> getAllProducts() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    /**
     * Retrieves a product by code.
     *
     * @param code Product code.
     * @return Product response.
     */
    public ProductResponse getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(ProductConstants.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }

    /**
     * Retrieves products by name.
     *
     * @param name Product name.
     * @return List of product responses.
     */
    public List<ProductResponse> getProductsByName(String name) {
        return productMapper.toResponseList(productRepository.findByName(name));
    }

    /**
     * Retrieves products for the authenticated user with stock greater than 0.
     *
     * @return List of product responses.
     */
    public List<ProductResponse> getAvailableProductsForUser(String userOid) {
        return productMapper.toResponseList(productRepository.findByUserOidAndStockGreaterThan(userOid, 0));
    }

    /**
     * Generates the next product code.
     *
     * @return Next product code.
     */
    private String generateNextCode() {
        return productRepository.findTopByOrderByCodeDesc()
                .map(product -> {
                    try {
                        int currentCode = Integer.parseInt(product.getCode());
                        return String.valueOf(currentCode + 1);
                    } catch (NumberFormatException exception) {
                        return "1";
                    }
                })
                .orElse("1");
    }

}
