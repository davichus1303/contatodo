package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.services.CompanyService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import com.contatodo.shared.response.WebResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for company endpoints.
 */
@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Creates a company controller.
     *
     * @param companyService Company service.
     */
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Retrieves all non-deleted companies with their resolved contact data.
     *
     * @return List of companies.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getCompanies() {
        List<CompanyResponse> companies = companyService.getCompanies();
        return WebResponses.ok(ResponseConstants.SUCCESS_MESSAGE, companies);
    }
}
