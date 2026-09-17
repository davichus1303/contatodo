package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateCompaniesRequest;
import com.contatodo.application.dto.request.CreateCompanyRequest;
import com.contatodo.application.dto.request.UpdateCompanyRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.mapper.CompanyMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.CompanyValidator;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.exceptions.CompanyNotFoundException;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(
                companyRepository,
                userRepository,
                new CompanyMapper(),
                new CompanyValidator(new FieldValidator()),
                authenticatedUserProvider
        );
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

    private CreateCompanyRequest createRequest(String name, String contactUserOId) {
        CreateCompanyRequest request = new CreateCompanyRequest();
        request.setName(name);
        request.setRfc("RFC-" + name);
        request.setWebSite("https://" + name.toLowerCase() + ".example.com");
        request.setUbication("Lima");
        request.setContactUserOId(contactUserOId);
        return request;
    }

    private Company persistedCompany() {
        return Company.builder()
                .id("company-1")
                .name("Acme")
                .rfc("ACM010101ABC")
                .webSite("https://acme.example.com")
                .ubication("Lima")
                .contactUserOId("user-1")
                .isActive(true)
                .isDeleted(false)
                .createdDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .updatedDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .createdBy("creator-1")
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

    @Test
    @SuppressWarnings("unchecked")
    void createCompaniesMapsEveryCompanyAndRecordsTheAuthenticatedCreator() {
        CreateCompaniesRequest request = new CreateCompaniesRequest();
        request.setCompanies(List.of(createRequest("Acme", "user-1"), createRequest("Globex", null)));
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("creator-1");
        when(companyRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(contactUser()));

        List<CompanyResponse> response = companyService.createCompanies(request);

        assertEquals(2, response.size());
        assertEquals("Acme", response.get(0).getName());
        assertEquals("Ana Contacto", response.get(0).getContactName());
        assertEquals("Globex", response.get(1).getName());
        assertNull(response.get(1).getContactName());

        ArgumentCaptor<List<Company>> captor = ArgumentCaptor.forClass(List.class);
        verify(companyRepository).saveAll(captor.capture());
        List<Company> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertEquals("RFC-Acme", saved.get(0).getRfc());
        assertEquals("creator-1", saved.get(0).getCreatedBy());
        assertEquals(Boolean.TRUE, saved.get(0).getIsActive());
        assertEquals(Boolean.FALSE, saved.get(0).getIsDeleted());
        assertEquals(saved.get(0).getCreatedDate(), saved.get(0).getUpdatedDate());
    }

    @Test
    void createCompaniesRejectsAnEmptyBatch() {
        CreateCompaniesRequest request = new CreateCompaniesRequest();
        request.setCompanies(List.of());

        assertThrows(InvalidRequestException.class, () -> companyService.createCompanies(request));
        verify(companyRepository, never()).saveAll(anyList());
        verify(authenticatedUserProvider, never()).getCurrentUserOid();
    }

    @Test
    void createCompaniesRejectsABatchWithACompanyWithoutName() {
        CreateCompaniesRequest request = new CreateCompaniesRequest();
        CreateCompanyRequest companyWithoutName = new CreateCompanyRequest();
        request.setCompanies(List.of(createRequest("Acme", null), companyWithoutName));

        assertThrows(InvalidRequestException.class, () -> companyService.createCompanies(request));
        verify(companyRepository, never()).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createCompaniesDefaultsTheActiveFlagToTrueWhenItIsNotProvided() {
        CreateCompaniesRequest request = new CreateCompaniesRequest();
        request.setCompanies(List.of(createRequest("Acme", null)));
        when(authenticatedUserProvider.getCurrentUserOid()).thenReturn("creator-1");
        when(companyRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        companyService.createCompanies(request);

        ArgumentCaptor<List<Company>> captor = ArgumentCaptor.forClass(List.class);
        verify(companyRepository).saveAll(captor.capture());
        assertEquals(Boolean.TRUE, captor.getValue().get(0).getIsActive());
    }

    @Test
    void updateCompanyAppliesProvidedFieldsAndKeepsTheRest() {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setName("Acme Updated");
        request.setUbication("Cusco");
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(persistedCompany()));
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(contactUser()));

        CompanyResponse response = companyService.updateCompany("company-1", request);

        assertEquals("company-1", response.getId());
        assertEquals("Acme Updated", response.getName());
        assertEquals("Cusco", response.getUbication());
        assertEquals("ACM010101ABC", response.getRfc());
        assertEquals("https://acme.example.com", response.getWebSite());
        assertEquals("user-1", response.getContactUserOId());
        assertEquals("Ana Contacto", response.getContactName());

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
        Company saved = captor.getValue();
        assertEquals("company-1", saved.getId());
        assertEquals(Boolean.FALSE, saved.getIsDeleted());
        assertEquals("creator-1", saved.getCreatedBy());
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), saved.getCreatedDate());
        assertTrue(saved.getUpdatedDate().isAfter(LocalDateTime.of(2026, 1, 1, 0, 0)));
    }

    @Test
    void updateCompanyThrowsWhenCompanyDoesNotExist() {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setName("Acme Updated");
        when(companyRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.updateCompany("missing", request));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void updateCompanyThrowsWhenCompanyIsDeleted() {
        Company deletedCompany = Company.builder()
                .id("company-1")
                .name("Acme")
                .isDeleted(true)
                .build();
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(deletedCompany));

        assertThrows(CompanyNotFoundException.class,
                () -> companyService.updateCompany("company-1", new UpdateCompanyRequest()));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void updateCompanyRejectsABlankName() {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setName("   ");

        assertThrows(InvalidRequestException.class, () -> companyService.updateCompany("company-1", request));
        verify(companyRepository, never()).findById(anyString());
        verify(companyRepository, never()).save(any(Company.class));
    }

}
