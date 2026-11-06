package com.contatodo.application.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the acquisition request payload contract.
 */
class CreateAcquisitionRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializesUnitOfMeasures() throws Exception {
        CreateAcquisitionRequest request = objectMapper.readValue(
                "{\"unitOfMeasures\":[\"uom-1\",\"uom-2\"]}",
                CreateAcquisitionRequest.class
        );

        assertEquals(List.of("uom-1", "uom-2"), request.getUnitOfMeasures());
    }
}
