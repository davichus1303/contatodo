package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.application.services.ProductService;
import com.contatodo.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web slice tests for {@link ProductController} verifying routing and payload shape.
 */
@WebMvcTest(ProductController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    private ProductResponse productResponse() {
        ProductResponse response = new ProductResponse();
        response.setId("p1");
        response.setName("Cafe");
        response.setStock(10);
        response.setCode("1");
        response.setRealCost(100.0);
        response.setUnitRealCost(10.0);
        response.setUnitPublicCost(15.0);
        response.setIsActive(true);
        return response;
    }

    @Test
    void getAllProductsReturnsOkWithEnvelope() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productResponse()));

        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success."))
                .andExpect(jsonPath("$.data[0].id").value("p1"))
                .andExpect(jsonPath("$.data[0].name").value("Cafe"));
    }

    @Test
    void getProductByCodeReturnsOk() throws Exception {
        when(productService.getProductByCode("1")).thenReturn(productResponse());

        mockMvc.perform(get("/products/code/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("1"));
    }
}
