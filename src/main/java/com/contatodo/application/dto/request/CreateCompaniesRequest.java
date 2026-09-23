package com.contatodo.application.dto.request;

import java.util.List;

/**
 * Request DTO for creating one or more companies in a single call.
 */
public class CreateCompaniesRequest {

    private List<CreateCompanyRequest> companies;

    public CreateCompaniesRequest() {
    }

    public List<CreateCompanyRequest> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CreateCompanyRequest> companies) {
        this.companies = companies;
    }
}
