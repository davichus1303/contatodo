package com.contatodo.application.services;

import com.contatodo.application.dto.response.AcquisitionTypeResponse;
import com.contatodo.application.mapper.AcquisitionTypeMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.AcquisitionTypeValidator;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.shared.constants.AcquisitionTypeConstants;
import com.contatodo.shared.exceptions.AcquisitionTypeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AcquisitionTypeService} delete and lookup rules.
 */
@ExtendWith(MockitoExtension.class)
class AcquisitionTypeServiceTest {

    @Mock
    private AcquisitionTypeRepository acquisitionTypeRepository;

    @Mock
    private AcquisitionTypeValidator acquisitionTypeValidator;

    @Mock
    private AcquisitionTypeMapper acquisitionTypeMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private AcquisitionTypeService service;

    @BeforeEach
    void setUp() {
        service = new AcquisitionTypeService(
                acquisitionTypeRepository, acquisitionTypeValidator,
                acquisitionTypeMapper, authenticatedUserProvider
        );
    }

    private AcquisitionType activeType() {
        return AcquisitionType.builder()
                .id("t-1")
                .name("Compra")
                .userOid("user-1")
                .isActive(true)
                .isDeleted(false)
                .affectsInventory(true)
                .build();
    }

    @Test
    void deleteAcquisitionTypeMarksDeletedWithoutMutatingOriginal() {
        AcquisitionType existing = activeType();
        when(acquisitionTypeRepository.findById("t-1")).thenReturn(Optional.of(existing));
        when(acquisitionTypeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(acquisitionTypeMapper.toResponse(any())).thenReturn(new AcquisitionTypeResponse());

        service.deleteAcquisitionType("t-1");

        ArgumentCaptorSupport.assertMarkDeleted(acquisitionTypeRepository);
        assertEquals(Boolean.FALSE, existing.getIsDeleted());
        verify(acquisitionTypeRepository).save(any());
    }

    @Test
    void deleteUnknownTypeThrowsNotFound() {
        when(acquisitionTypeRepository.findById("missing")).thenReturn(Optional.empty());

        AcquisitionTypeNotFoundException exception = assertThrows(
                AcquisitionTypeNotFoundException.class,
                () -> service.deleteAcquisitionType("missing")
        );
        assertEquals(AcquisitionTypeConstants.NOT_FOUND_ERROR, exception.getMessage());
        verify(acquisitionTypeRepository, never()).save(any());
    }

    @Test
    void deleteAlreadyDeletedTypeThrowsAlreadyDeleted() {
        AcquisitionType deleted = activeType().markDeleted();
        when(acquisitionTypeRepository.findById("t-1")).thenReturn(Optional.of(deleted));

        AcquisitionTypeNotFoundException exception = assertThrows(
                AcquisitionTypeNotFoundException.class,
                () -> service.deleteAcquisitionType("t-1")
        );
        assertEquals(AcquisitionTypeConstants.ALREADY_DELETED_ERROR, exception.getMessage());
        verify(acquisitionTypeRepository, never()).save(any());
    }

    /**
     * Small helper to capture and assert the saved acquisition type state.
     */
    private static final class ArgumentCaptorSupport {

        private static void assertMarkDeleted(AcquisitionTypeRepository repository) {
            org.mockito.ArgumentCaptor<AcquisitionType> captor =
                    org.mockito.ArgumentCaptor.forClass(AcquisitionType.class);
            org.mockito.Mockito.verify(repository).save(captor.capture());
            org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, captor.getValue().getIsDeleted());
            org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, captor.getValue().getIsActive());
        }
    }
}
