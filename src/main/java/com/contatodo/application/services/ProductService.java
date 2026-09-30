package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.application.mapper.ProductMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.validators.CompanyOidValidator;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    private final CompanyContextProvider companyContextProvider;
    private final CompanyOidValidator companyOidValidator;

    /**
     * Creates a product service.
     *
     * @param productRepository Product repository port.
     * @param userRepository User repository port.
     * @param productValidator Product validator.
     * @param productMapper Product mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyRepository Company repository port.
     *@param companyContextProvider Company context provider.
     */
    public ProductService(
            ProductRepository productRepository,
            UserRepository userRepository,
            ProductValidator productValidator,
            ProductMapper productMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyContextProvider companyContextProvider,
            CompanyOidValidator companyOidValidator
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productValidator = productValidator;
        this.productMapper = productMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyContextProvider = companyContextProvider;
        this.companyOidValidator = companyOidValidator;
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
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstants.USER_NOT_FOUND));

        String nextCode = generateNextCode();
        CompanyOid companyOid = resolveCompanyOid(request.getCompanyOid());
        Product product = productMapper.toEntity(request, nextCode, userOid, companyOid);

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
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstants.PRODUCT_NOT_FOUND));

        Product updatedProduct = productRepository.save(
                productMapper.applyUpdate(product, request, authenticatedUserProvider.getCurrentUserOid())
        );
        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Retrieves all products.
     *
     * @return List of product responses.
     */
    public List<ProductResponse> getAllProducts(String requestedCompanyOid) {
        CompanyOid companyOid = resolveReadCompanyOid(requestedCompanyOid);
        return productMapper.toResponseList(productRepository.findAll(companyOid));
    }

    /**
     * Retrieves a product by code.
     *
     * @param code Product code.
     * @return Product response.
     */
    public ProductResponse getProductByCode(String requestedCompanyOid, String code) {
        CompanyOid companyOid = resolveReadCompanyOid(requestedCompanyOid);
        Product product = productRepository.findByCode(companyOid, code)
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstants.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }

    /**
     * Retrieves products by name.
     *
     * @param name Product name.
     * @return List of product responses.
     */
    public List<ProductResponse> getProductsByName(String requestedCompanyOid, String name) {
        CompanyOid companyOid = resolveReadCompanyOid(requestedCompanyOid);
        return productMapper.toResponseList(productRepository.findByName(companyOid, name));
    }

    /**
     * Retrieves available products with stock greater than 0.
     *
     * @return List of product responses.
     */
    public List<ProductResponse> getAvailableProducts(String requestedCompanyOid) {
        CompanyOid companyOid = resolveReadCompanyOid(requestedCompanyOid);
        return productMapper.toResponseList(productRepository.findByStockGreaterThan(companyOid, 0));
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

    /**
     * Resolves the owning company for a write.
     *
     * <p>The company of the session always wins, so a caller can never move a record
     * out of the company its own token points at. Only when the token carries no
     * company, which is the case for the root user and for any session without a
     * company, the optional value supplied in the request is used. The identifier is
     * left {@code null} when neither is available, instead of being rejected.</p>
     *
     * <p>The value supplied in the request is validated against the stored companies
     * before it is used, so a record can never be attached to a company that does
     * not exist. The value is only validated when it is actually the one applied,
     * which is when the session carries no company.</p>
     *
     * @param requestedCompanyOid Optional company identifier supplied in the request.
     * @return Company identifier, or {@code null} when neither source provides one.
     * @throws ResourceNotFoundException when the requested company does not exist.
     */
    private CompanyOid resolveCompanyOid(String requestedCompanyOid) {
        Optional<CompanyOid> sessionCompany = companyContextProvider.currentCompanyOid();
        if (sessionCompany.isPresent()) {
            return sessionCompany.get();
        }
        if (requestedCompanyOid != null && !requestedCompanyOid.isBlank()) {
            return CompanyOid.of(companyOidValidator.validate(requestedCompanyOid));
        }
        return null;
    }

    /**
     * Resolves the owning company read by a query.
     *
     * <p>The company of the session always wins. Only when the token carries
     * none, the company identifier supplied in the request is used. A read has
     * no separate bucket to fall back to, so a missing, blank or invalid value
     * is rejected.</p>
     *
     * @param requestedCompanyOid Company identifier supplied in the request.
     * @return Company to scope the query to.
     * @throws ResourceNotFoundException when no company can be resolved.
     */
    private CompanyOid resolveReadCompanyOid(String requestedCompanyOid) {
        Optional<CompanyOid> sessionCompany = companyContextProvider.currentCompanyOid();
        if (sessionCompany.isPresent()) {
            return sessionCompany.get();
        }
        if (requestedCompanyOid != null && !requestedCompanyOid.isBlank()) {
            return CompanyOid.of(companyOidValidator.validate(requestedCompanyOid));
        }
        throw new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED);
    }

}
