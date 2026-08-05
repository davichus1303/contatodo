package com.contatodo.application.dto.request;

/**
 * Request DTO for creating an expense.
 */
public class CreateExpenseRequest {

    private String acquisitionOid;
    private String acquisitionTypeOid;
    private String name;
    private Integer quantity;
    private Double amount;
    private String currency;
    private String expenseDate;

    public CreateExpenseRequest() {
    }

    public String getAcquisitionOid() {
        return acquisitionOid;
    }

    public void setAcquisitionOid(String acquisitionOid) {
        this.acquisitionOid = acquisitionOid;
    }

    public String getAcquisitionTypeOid() {
        return acquisitionTypeOid;
    }

    public void setAcquisitionTypeOid(String acquisitionTypeOid) {
        this.acquisitionTypeOid = acquisitionTypeOid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }
}
