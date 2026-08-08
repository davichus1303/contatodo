package com.contatodo.application.dto.response;

/**
 * Response DTO for total expenses within a date range.
 */
public class TotalExpensesResponse {

    private Double total;

    /**
     * Creates an empty total expenses response.
     */
    public TotalExpensesResponse() {
    }

    /**
     * Creates a total expenses response with the specified total.
     *
     * @param total Total amount of expenses.
     */
    public TotalExpensesResponse(Double total) {
        this.total = total;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
