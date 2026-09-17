package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.services.CompanyService;
import com.contatodo.infrastructure.security.JwtService;
import com.contatodo.shared.constants.CompanyConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web slice tests for {@link CompanyController} verifying routing, payload shape and error mapping.
 */
@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private JwtService jwtService;

    private CompanyResponse companyResponse() {
        CompanyResponse response = new CompanyResponse();
        response.setId("c1");
        response.setName("Acme");
        response.setContactUserOId("u1");
        response.setContactName("David");
        response.setIsActive(true);
        response.setIsDeleted(false);
        return response;
    }

    @Test
    void getCompaniesReturnsOkWithEnvelope() throws Exception {
        when(companyService.getCompanies()).thenReturn(List.of(companyResponse()));

        mockMvc.perform(get("/companies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseConstants.SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.data[0].id").value("c1"))
                .andExpect(jsonPath("$.data[0].name").value("Acme"));
    }

    @Test
    void createCompaniesReturnsOkWithMessage() throws Exception {
        when(companyService.createCompanies(any())).thenReturn(List.of(companyResponse()));

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companies\":[{\"name\":\"Acme\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(CompanyConstants.COMPANIES_CREATED))
                .andExpect(jsonPath("$.data[0].name").value("Acme"));

        verify(companyService).createCompanies(any());
    }

    @Test
    void createCompaniesReturnsBadRequestWhenServiceThrows() throws Exception {
        when(companyService.createCompanies(any()))
                .thenThrow(new InvalidRequestException(
                        ResponseConstants.VALIDATION_ERROR_MESSAGE,
                        List.of("Name is required.")
                ));

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companies\":[{\"name\":null}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(ResponseConstants.VALIDATION_ERROR_MESSAGE))
                .andExpect(jsonPath("$.details[0]").value("Name is required."));
    }
}
