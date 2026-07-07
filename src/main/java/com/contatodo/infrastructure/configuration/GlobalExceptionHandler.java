package com.contatodo.infrastructure.configuration;

import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.AuthenticationException;
import com.contatodo.shared.exceptions.InsufficientStockException;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import com.contatodo.shared.exceptions.SaleWithoutProfitException;
import com.contatodo.shared.exceptions.UserAlreadyExistsException;
import com.contatodo.shared.exceptions.UserNotFoundException;
import com.contatodo.shared.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid request exceptions.
     *
     * @param exception Invalid request exception.
     * @return Error response.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                exception.getDetails()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles user not found exceptions.
     *
     * @param exception User not found exception.
     * @return Error response.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                java.util.List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles product not found exceptions.
     *
     * @param exception Product not found exception.
     * @return Error response.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                java.util.List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles user already exists exceptions.
     *
     * @param exception User already exists exception.
     * @return Error response.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                java.util.List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handles authentication exceptions.
     *
     * @param exception Authentication exception.
     * @return Error response.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                java.util.List.of()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles insufficient stock exceptions.
     *
     * @param exception Insufficient stock exception.
     * @return Error response.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                java.util.List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles sale without profit exceptions.
     *
     * @param exception Sale without profit exception.
     * @return Error response.
     */
    @ExceptionHandler(SaleWithoutProfitException.class)
    public ResponseEntity<ErrorResponse> handleSaleWithoutProfitException(SaleWithoutProfitException exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                java.util.List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles unexpected exceptions.
     *
     * @param exception Unexpected exception.
     * @return Error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ResponseConstants.VALIDATION_ERROR_MESSAGE,
                java.util.List.of(exception.getMessage() != null ? exception.getMessage() : AuthConstants.ACCESS_DENIED)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
