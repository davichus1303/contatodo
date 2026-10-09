package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.services.UnitOfMeasureService;
import com.contatodo.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web slice tests for {@link UnitOfMeasureController} verifying routing and payload shape.
 */
@WebMvcTest(UnitOfMeasureController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class UnitOfMeasureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UnitOfMeasureService unitOfMeasureService;

    @MockBean
    private JwtService jwtService;

    private UnitOfMeasureResponse unitOfMeasureResponse() {
        UnitOfMeasureResponse response = new UnitOfMeasureResponse();
        response.setId("u1");
        response.setName("Kilogram");
        response.setAbrev("kg");
        response.setCompanyOid("c1");
        response.setIsActive(true);
        response.setIsDeleted(false);
        return response;
    }

    @Test
    void getActiveUnitsOfMeasureReturnsOkWithEnvelope() throws Exception {
        when(unitOfMeasureService.getActiveUnitsOfMeasure(any())).thenReturn(List.of(unitOfMeasureResponse()));

        mockMvc.perform(get("/units-of-measure").param("companyOid", "c1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success."))
                .andExpect(jsonPath("$.data[0].id").value("u1"))
                .andExpect(jsonPath("$.data[0].name").value("Kilogram"))
                .andExpect(jsonPath("$.data[0].abrev").value("kg"))
                .andExpect(jsonPath("$.data[0].companyOid").value("c1"))
                .andExpect(jsonPath("$.data[0].isActive").value(true))
                .andExpect(jsonPath("$.data[0].isDeleted").value(false));
    }

    @Test
    void getActiveUnitsOfMeasureWithoutCompanyParamReturnsOk() throws Exception {
        when(unitOfMeasureService.getActiveUnitsOfMeasure(null)).thenReturn(List.of());

        mockMvc.perform(get("/units-of-measure").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void createUnitOfMeasureReturnsOkWithEnvelope() throws Exception {
        when(unitOfMeasureService.createUnitOfMeasure(any())).thenReturn(unitOfMeasureResponse());

        mockMvc.perform(post("/units-of-measure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kilogram\",\"abrev\":\"kg\",\"companyOid\":\"c1\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Unit of measure created successfully."))
                .andExpect(jsonPath("$.data.name").value("Kilogram"))
                .andExpect(jsonPath("$.data.abrev").value("kg"))
                .andExpect(jsonPath("$.data.companyOid").value("c1"));
    }
}
