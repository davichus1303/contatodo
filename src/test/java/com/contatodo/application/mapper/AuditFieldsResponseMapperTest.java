package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.CompanyResponse;
import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.domain.entities.Company;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.repositories.ModuleRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests asserting that the audit fields declared by the response DTOs are
 * actually populated by the mappers, so they never reach the API as null.
 */
class AuditFieldsResponseMapperTest {

    private final RoleMapper roleMapper = new RoleMapper(null);
    private final CompanyMapper companyMapper = new CompanyMapper();
    private final ProductMapper productMapper = new ProductMapper();

    private static final LocalDateTime WHEN = LocalDateTime.of(2026, 1, 1, 0, 0);

    @Test
    void roleResponseExposesCreatorAndLastEditor() {
        Role role = Role.builder()
                .id("role-1")
                .name("Administrator")
                .byUserOid("creator-1")
                .updatedByUserOid("editor-1")
                .isActive(true)
                .isDeleted(false)
                .createdDate(WHEN)
                .updatedDate(WHEN)
                .build();

        RoleResponse response = roleMapper.toResponse(role);

        assertEquals("creator-1", response.getByUserOid());
        assertEquals("editor-1", response.getUpdatedByUserOid());
    }

    @Test
    void companyResponseExposesCreatorAndLastEditor() {
        Company company = Company.builder()
                .id("company-1")
                .name("Acme")
                .byUserOid("creator-1")
                .updatedByUserOid("editor-1")
                .isActive(true)
                .isDeleted(false)
                .createdDate(WHEN)
                .updatedDate(WHEN)
                .build();

        CompanyResponse response = companyMapper.toResponse(company, null);

        assertEquals("creator-1", response.getByUserOid());
        assertEquals("editor-1", response.getUpdatedByUserOid());
    }

    @Test
    void productResponseExposesCreatorAndLastEditor() {
        Product product = Product.builder()
                .id("p-1")
                .name("Cafe")
                .stock(5)
                .code("42")
                .byUserOid("creator-1")
                .updatedByUserOid("editor-1")
                .isActive(true)
                .createdDate(WHEN)
                .updatedDate(WHEN)
                .build();

        ProductResponse response = productMapper.toResponse(product);

        assertEquals("creator-1", response.getByUserOid());
        assertEquals("editor-1", response.getUpdatedByUserOid());
    }

    @Test
    void updateKeepsTheOriginalCreatorAndSetsTheEditor() {
        Role role = Role.builder()
                .id("role-1")
                .name("Administrator")
                .byUserOid("creator-1")
                .updatedByUserOid("editor-1")
                .isActive(true)
                .isDeleted(false)
                .createdDate(WHEN)
                .updatedDate(WHEN)
                .build();

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setName("Administrador");

        Role updated = roleMapper.updateEntity(role, request, "editor-2");

        assertEquals("creator-1", updated.getByUserOid());
        assertEquals("editor-2", updated.getUpdatedByUserOid());
    }
}
