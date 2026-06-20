package com.contatodo.shared.response;

import java.util.List;

/**
 * Standard success response wrapper.
 *
 * @param <T> Type of the response data.
 */
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    /**
     * Creates an empty response.
     */
    public ApiResponse() {
    }

    /**
     * Creates a response with all fields.
     *
     * @param status HTTP status code.
     * @param message Response message.
     * @param data Response data.
     */
    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Builds a success response.
     *
     * @param message Success message.
     * @param data Response data.
     * @param <T> Type of the response data.
     * @return Success response.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Builds a success response without data.
     *
     * @param message Success message.
     * @return Success response.
     */
    public static ApiResponse<List<Object>> successWithoutData(String message) {
        return new ApiResponse<>(200, message, List.of());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
