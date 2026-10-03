package com.contatodo.application.validators;

import com.contatodo.domain.entities.Company;
import com.contatodo.domain.repositories.CompanyRepository;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyOidValidator Tests")
class CompanyOidValidatorTest {

    @Mock
    private CompanyRepository companyRepository;

    private CompanyOidValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CompanyOidValidator(companyRepository);
    }

    private static Company company(String id, Boolean isActive, Boolean isDeleted) {
        return Company.builder().id(id).name("Acme").isActive(isActive).isDeleted(isDeleted).build();
    }

    @Test
    @DisplayName("Should accept an existing, active and not deleted company")
    void shouldAcceptUsableCompany() {
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(company("company-1", true, false)));

        assertEquals("company-1", validator.validate("company-1"));
    }

    @Test
    @DisplayName("Should reject a company that does not exist")
    void shouldRejectMissingCompany() {
        when(companyRepository.findById("company-404")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> validator.validate("company-404")
        );

        assertTrue(exception.getMessage().contains("company-404"));
    }

    @Test
    @DisplayName("Should reject an inactive company")
    void shouldRejectInactiveCompany() {
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(company("company-1", false, false)));

        assertThrows(ResourceNotFoundException.class, () -> validator.validate("company-1"));
    }

    @Test
    @DisplayName("Should reject a deleted company")
    void shouldRejectDeletedCompany() {
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(company("company-1", true, true)));

        assertThrows(ResourceNotFoundException.class, () -> validator.validate("company-1"));
    }

    @Test
    @DisplayName("Should reject a company that is both inactive and deleted")
    void shouldRejectInactiveAndDeletedCompany() {
        when(companyRepository.findById("company-1")).thenReturn(Optional.of(company("company-1", false, true)));

        assertThrows(ResourceNotFoundException.class, () -> validator.validate("company-1"));
    }

    @Test
    @DisplayName("Should reject a company whose flags are null, from documents written before the flags existed")
    void shouldRejectCompanyWithNullFlags() {
        when(companyRepository.findById("legacy")).thenReturn(Optional.of(company("legacy", null, null)));

        assertThrows(ResourceNotFoundException.class, () -> validator.validate("legacy"));
    }

    @Test
    @DisplayName("Should not distinguish a deleted company from a missing one")
    void shouldNotRevealWhetherTheCompanyExists() {
        when(companyRepository.findById("company-404")).thenReturn(Optional.empty());
        String missing = assertThrows(ResourceNotFoundException.class, () -> validator.validate("company-404")).getMessage();

        when(companyRepository.findById("company-1")).thenReturn(Optional.of(company("company-1", true, true)));
        String deleted = assertThrows(ResourceNotFoundException.class, () -> validator.validate("company-1")).getMessage();

        assertEquals(missing.substring(0, missing.indexOf("Id:")), deleted.substring(0, deleted.indexOf("Id:")));
    }

    @Test
    @DisplayName("Should return null without consulting the repository when no company is supplied")
    void shouldReturnNullWhenNoCompanySupplied() {
        assertNull(validator.validate(null));
        assertNull(validator.validate(""));
        assertNull(validator.validate("   "));
    }
}
