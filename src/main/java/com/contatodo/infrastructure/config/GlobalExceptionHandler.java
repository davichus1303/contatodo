package com.contatodo.infrastructure.config;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.AcquisitionTypeNotFoundException;
import com.contatodo.shared.exceptions.AuthenticationException;
import com.contatodo.shared.exceptions.InsufficientStockException;
import com.contatodo.shared.exceptions.InvalidDateRangeException;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import com.contatodo.shared.exceptions.SaleWithoutProfitException;
import com.contatodo.shared.exceptions.UserAlreadyExistsException;
import com.contatodo.shared.exceptions.UserNotFoundException;
import com.contatodo.shared.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler that translates domain and application exceptions
 * into uniform HTTP error responses.
 *
 * <p>Every handler keeps the pre-existing API contract: same HTTP status codes
 * and {@link ErrorResponse} payload shape. Unexpected errors are logged with
 * full detail server-side but never leaked to the client.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles invalid request exceptions.
     *
     * @param exception Invalid request exception.
     * @return 400 response with field details.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), exception.getDetails());
    }

    /**
     * Handles domain invariant violations raised while building entities.
     *
     * @param exception Invalid entity state exception.
     * @return 400 response.
     */
    @ExceptionHandler(InvalidEntityStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEntityStateException(InvalidEntityStateException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of());
    }

    /**
     * Handles invalid date range exceptions.
     *
     * @param exception Invalid date range exception.
     * @return 400 response.
     */
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateRangeException(InvalidDateRangeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of());
    }

    /**
     * Handles user not found exceptions.
     *
     * @param exception User not found exception.
     * @return 404 response.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
    }

    /**
     * Handles product not found exceptions.
     *
     * @param exception Product not found exception.
     * @return 404 response.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
    }

    /**
     * Handles acquisition type not found exceptions.
     *
     * @param exception Acquisition type not found exception.
     * @return 404 response.
     */
    @ExceptionHandler(AcquisitionTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAcquisitionTypeNotFoundException(AcquisitionTypeNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
    }

    /**
     * Handles user already exists exceptions.
     *
     * @param exception User already exists exception.
     * @return 409 response.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), List.of());
    }

    /**
     * Handles authentication exceptions.
     *
     * @param exception Authentication exception.
     * @return 401 response.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), List.of());
    }

    /**
     * Handles insufficient stock exceptions.
     *
     * @param exception Insufficient stock exception.
     * @return 400 response.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of());
    }

    /**
     * Handles sale without profit exceptions.
     *
     * @param exception Sale without profit exception.
     * @return 400 response.
     */
    @ExceptionHandler(SaleWithoutProfitException.class)
    public ResponseEntity<ErrorResponse> handleSaleWithoutProfitException(SaleWithoutProfitException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of());
    }

    /**
     * Handles unexpected exceptions. The original cause is logged for
     * observability; only an opaque message reaches the client.
     *
     * @param exception Unexpected exception.
     * @return 500 response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        log.error("Unhandled exception while processing request", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ResponseConstants.VALIDATION_ERROR_MESSAGE, List.of());
    }

    /**
     * Builds a uniform error response entity.
     *
     * @param status HTTP status to return.
     * @param message Error message exposed to the client.
     * @param details Optional list of error details.
     * @return Error response wrapped in an entity with the given status.
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, List<String> details) {
        ErrorResponse response = ErrorResponse.of(status.value(), message, details);
        return ResponseEntity.status(status).body(response);
    }
}
