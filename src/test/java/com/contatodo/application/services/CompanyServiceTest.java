package com.contatodo.application.services;

import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.mapper.CompanyMapper;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CompanyService}.
 */
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, userRepository, new CompanyMapper());
    }

    private Company companyWithContact(String contactUserOId) {
        return Company.builder()
                .id("company-1")
                .name("Acme")
                .rfc("ACM010101ABC")
                .webSite("https://acme.example.com")
                .ubication("Lima")
                .contactUserOId(contactUserOId)
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    private User contactUser() {
        return User.builder()
                .id("user-1")
                .userName("ana")
                .email("ana@example.com")
                .password("hashed")
                .name("Ana Contacto")
                .phoneNumber("987654321")
                .build();
    }

    @Test
    void getCompaniesResolvesTheContactOfEachCompany() {
        when(companyRepository.findAllNotDeleted()).thenReturn(List.of(companyWithContact("user-1")));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(contactUser()));

        List<CompanyResponse> response = companyService.getCompanies();

        assertEquals(1, response.size());
        assertEquals("company-1", response.get(0).getId());
        assertEquals("Acme", response.get(0).getName());
        assertEquals("ACM010101ABC", response.get(0).getRfc());
        assertEquals("user-1", response.get(0).getContactUserOId());
        assertEquals("Ana Contacto", response.get(0).getContactName());
        assertEquals("987654321", response.get(0).getContactPhone());
        verify(userRepository).findById("user-1");
    }

    @Test
    void getCompaniesLeavesContactEmptyWhenTheCompanyHasNoContact() {
        when(companyRepository.findAllNotDeleted()).thenReturn(List.of(companyWithContact(null)));

        List<CompanyResponse> response = companyService.getCompanies();

        assertEquals(1, response.size());
        assertNull(response.get(0).getContactName());
        assertNull(response.get(0).getContactPhone());
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void getCompaniesLeavesContactEmptyWhenTheContactIsNotFound() {
        when(companyRepository.findAllNotDeleted()).thenReturn(List.of(companyWithContact("user-1")));
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        List<CompanyResponse> response = companyService.getCompanies();

        assertEquals(1, response.size());
        assertNull(response.get(0).getContactName());
        assertNull(response.get(0).getContactPhone());
    }

    @Test
    void getCompaniesStillReturnsCompaniesWhenAContactLookupFails() {
        when(companyRepository.findAllNotDeleted()).thenReturn(List.of(companyWithContact("user-1")));
        when(userRepository.findById("user-1")).thenThrow(new RuntimeException("contact lookup failed"));

        List<CompanyResponse> response = companyService.getCompanies();

        assertEquals(1, response.size());
        assertNull(response.get(0).getContactName());
        verify(userRepository).findById("user-1");
    }

    @Test
    void getCompaniesLooksUpEachRepeatedContactOnlyOnce() {
        when(companyRepository.findAllNotDeleted())
                .thenReturn(List.of(companyWithContact("user-1"), companyWithContact("user-1")));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(contactUser()));

        List<CompanyResponse> response = companyService.getCompanies();

        assertEquals(2, response.size());
        assertEquals("Ana Contacto", response.get(0).getContactName());
        assertEquals("Ana Contacto", response.get(1).getContactName());
        verify(userRepository, times(1)).findById("user-1");
    }
}
