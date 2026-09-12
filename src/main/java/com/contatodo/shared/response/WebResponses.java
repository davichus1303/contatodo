package com.contatodo.shared.response;

import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Builders for standard success responses.
 *
 * <p>These replace the repeated {@code ResponseEntity.ok(ApiResponse.success(...))}
 * wrapping found in controllers.</p>
 */
public final class WebResponses {

    private WebResponses() {
    }

    /**
     * Builds a 200 OK response with a success message and payload.
     *
     * @param message Success message.
     * @param data Response payload.
     * @param <T> Type of the payload.
     * @return OK response.
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * Builds a 200 OK response with a success message and no payload.
     *
     * @param message Success message.
     * @return OK response without data.
     */
    public static ResponseEntity<ApiResponse<List<Object>>> okNoData(String message) {
        return ResponseEntity.ok(ApiResponse.successWithoutData(message));
    }
}