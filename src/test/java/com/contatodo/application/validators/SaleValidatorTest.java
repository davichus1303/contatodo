package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SaleValidator} pack rules.
 */
class SaleValidatorTest {

    private final SaleValidator validator = new SaleValidator(new FieldValidator());

    private CreateSaleRequest request() {
        CreateSaleRequest request = new CreateSaleRequest();
        request.setProductOid("product-1");
        request.setQuantity(2);
        request.setUnitOfMeasureOid("uom-1");
        request.setIsPack(false);
        request.setTotalSalePrice(40.0);
        return request;
    }

    @Test
    void acceptsARequestWithThePackFlag() {
        assertDoesNotThrow(() -> validator.validateCreateRequest(request()));
    }

    @Test
    void rejectsARequestWithoutThePackFlag() {
        CreateSaleRequest request = request();
        request.setIsPack(null);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getDetails().contains(SaleConstants.SALE_IS_PACK_REQUIRED));
    }

    @Test
    void acceptsAPositivePackSize() {
        CreateSaleRequest request = request();
        request.setPackSize(12);

        assertDoesNotThrow(() -> validator.validateCreateRequest(request));
    }

    @Test
    void rejectsANonPositivePackSize() {
        CreateSaleRequest request = request();
        request.setPackSize(0);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getDetails().stream()
                .anyMatch(detail -> detail.contains(SaleConstants.SALE_PACK_SIZE_REQUIRED)));
    }
}
