package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.application.services.UserService;
import com.contatodo.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web slice tests for {@link UserController} verifying routing, payload shape
 * and how the session email is resolved on the public registration endpoint.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private UserResponse userResponse() {
        UserResponse response = new UserResponse();
        response.setId("u1");
        response.setEmail("david@example.com");
        response.setName("David");
        response.setActive(true);
        return response;
    }

    @Test
    void getAllUsersReturnsOkWithEnvelope() throws Exception {
        when(userService.getAllUsers("company-1")).thenReturn(List.of(userResponse()));

        mockMvc.perform(get("/users").param("companyOid", "company-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].id").value("u1"))
                .andExpect(jsonPath("$.data[0].email").value("david@example.com"));
    }

    @Test
    @WithMockUser(username = "david@example.com")
    void getContactCandidatesReturnsOkWithEnvelope() throws Exception {
        when(userService.getContactCandidates()).thenReturn(List.of(userResponse()));

        mockMvc.perform(get("/users/contacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].id").value("u1"))
                .andExpect(jsonPath("$.data[0].email").value("david@example.com"));

        verify(userService).getContactCandidates();
    }

    @Test
    @WithAnonymousUser
    void createUserWithoutSessionCreatesAPublicRegistration() throws Exception {
        when(userService.createUser(any(), eq(Optional.empty()))).thenReturn(userResponse());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@example.com\",\"password\":\"secret123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("u1"));

        verify(userService).createUser(any(), eq(Optional.empty()));
    }

    @Test
    @WithMockUser(username = "david@example.com")
    void createUserWithSessionPassesTheAuthenticatedEmail() throws Exception {
        when(userService.createUser(any(), eq(Optional.of("david@example.com")))).thenReturn(userResponse());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@example.com\",\"password\":\"secret123\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("u1"));

        verify(userService).createUser(any(), eq(Optional.of("david@example.com")));
    }
}
