package com.contatodo.shared.response;

import java.util.List;

/**
 * Standard error response wrapper.
 */
public class ErrorResponse {

    private int status;
    private String message;
    private List<String> details;

    /**
     * Creates an empty error response.
     */
    public ErrorResponse() {
    }

    /**
     * Creates an error response with all fields.
     *
     * @param status HTTP status code.
     * @param message Error message.
     * @param details Error details.
     */
    public ErrorResponse(int status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    /**
     * Builds an error response.
     *
     * @param status HTTP status code.
     * @param message Error message.
     * @param details Error details.
     * @return Error response.
     */
    public static ErrorResponse of(int status, String message, List<String> details) {
        return new ErrorResponse(status, message, details);
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

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
