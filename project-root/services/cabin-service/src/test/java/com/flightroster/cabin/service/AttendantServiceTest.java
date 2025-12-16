package com.flightroster.cabin.service;

import com.flightroster.cabin.entity.Attendant;
import com.flightroster.cabin.entity.AttendantType;
import com.flightroster.cabin.repository.AttendantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendantServiceTest {

    @Mock
    private AttendantRepository attendantRepository;

    @InjectMocks
    private AttendantService attendantService;

    private Attendant sampleAttendant;

    @BeforeEach
    void setUp() {
        sampleAttendant = new Attendant(
                1L,
                "ATT-001",
                "John Doe",
                30,
                "Male",
                "TR",
                "[\"EN\",\"TR\"]",
                AttendantType.REGULAR,
                "[]",
                true,
                null
        );
    }

    @Test
    void getAllAttendants_returnsList() {
        when(attendantRepository.findAll()).thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAllAttendants();

        assertEquals(1, result.size());
        assertEquals("ATT-001", result.get(0).getAttendantId());
        verify(attendantRepository, times(1)).findAll();
    }

    @Test
    void getAvailableAttendants_usesRepositoryMethod() {
        when(attendantRepository.findByIsAvailableTrue()).thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAvailableAttendants();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAvailable());
        verify(attendantRepository).findByIsAvailableTrue();
    }

    @Test
    void getAttendantById_found_returnsEntity() {
        when(attendantRepository.findById(1L)).thenReturn(Optional.of(sampleAttendant));

        Attendant result = attendantService.getAttendantById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(attendantRepository).findById(1L);
    }

    @Test
    void getAttendantById_notFound_returnsNull() {
        when(attendantRepository.findById(99L)).thenReturn(Optional.empty());

        Attendant result = attendantService.getAttendantById(99L);

        assertNull(result);
        verify(attendantRepository).findById(99L);
    }

    @Test
    void getAttendantByAttendantId_found_returnsEntity() {
        when(attendantRepository.findByAttendantId("ATT-001")).thenReturn(Optional.of(sampleAttendant));

        Attendant result = attendantService.getAttendantByAttendantId("ATT-001");

        assertNotNull(result);
        assertEquals("ATT-001", result.getAttendantId());
        verify(attendantRepository).findByAttendantId("ATT-001");
    }

    @Test
    void getAttendantByAttendantId_notFound_returnsNull() {
        when(attendantRepository.findByAttendantId("UNKNOWN")).thenReturn(Optional.empty());

        Attendant result = attendantService.getAttendantByAttendantId("UNKNOWN");

        assertNull(result);
        verify(attendantRepository).findByAttendantId("UNKNOWN");
    }

    @Test
    void getAttendantsByType_validType_callsRepository() {
        when(attendantRepository.findByAttendantTypeAndAvailable(AttendantType.REGULAR))
                .thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAttendantsByType("regular");

        assertEquals(1, result.size());
        verify(attendantRepository).findByAttendantTypeAndAvailable(AttendantType.REGULAR);
    }

    @Test
    void getAttendantsByType_invalidType_returnsEmptyList() {
        List<Attendant> result = attendantService.getAttendantsByType("invalid_type");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attendantRepository, never()).findByAttendantTypeAndAvailable(any());
    }

    @Test
    void getAvailableChefs_usesRepositoryMethod() {
        when(attendantRepository.findAvailableChefs()).thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAvailableChefs();

        assertEquals(1, result.size());
        verify(attendantRepository).findAvailableChefs();
    }

    @Test
    void getAvailableChiefs_usesRepositoryMethod() {
        when(attendantRepository.findAvailableChiefs()).thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAvailableChiefs();

        assertEquals(1, result.size());
        verify(attendantRepository).findAvailableChiefs();
    }

    @Test
    void getAvailableRegulars_usesRepositoryMethod() {
        when(attendantRepository.findAvailableRegulars()).thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAvailableRegulars();

        assertEquals(1, result.size());
        verify(attendantRepository).findAvailableRegulars();
    }

    @Test
    void createAttendant_savesEntity() {
        when(attendantRepository.save(sampleAttendant)).thenReturn(sampleAttendant);

        Attendant result = attendantService.createAttendant(sampleAttendant);

        assertEquals(sampleAttendant, result);
        verify(attendantRepository).save(sampleAttendant);
    }

    @Test
    void updateAttendant_found_updatesAndSaves() {
        Attendant updatedDetails = new Attendant(
                null,
                "ATT-002",
                "Jane Doe",
                28,
                "Female",
                "US",
                "[\"EN\"]",
                AttendantType.CHEF,
                "[\"B737\"]",
                false,
                "[\"VEGAN\"]"
        );

        when(attendantRepository.findById(1L)).thenReturn(Optional.of(sampleAttendant));
        when(attendantRepository.save(any(Attendant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Attendant result = attendantService.updateAttendant(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("ATT-002", result.getAttendantId());
        assertEquals("Jane Doe", result.getName());
        assertEquals(AttendantType.CHEF, result.getAttendantType());
        assertEquals(false, result.getIsAvailable());
        verify(attendantRepository).findById(1L);
        verify(attendantRepository).save(sampleAttendant);
    }

    @Test
    void updateAttendant_notFound_returnsNull() {
        when(attendantRepository.findById(99L)).thenReturn(Optional.empty());

        Attendant result = attendantService.updateAttendant(99L, sampleAttendant);

        assertNull(result);
        verify(attendantRepository).findById(99L);
        verify(attendantRepository, never()).save(any());
    }

    @Test
    void deleteAttendant_found_deletesAndReturnsTrue() {
        when(attendantRepository.findById(1L)).thenReturn(Optional.of(sampleAttendant));

        boolean deleted = attendantService.deleteAttendant(1L);

        assertTrue(deleted);
        verify(attendantRepository).findById(1L);
        verify(attendantRepository).delete(sampleAttendant);
    }

    @Test
    void deleteAttendant_notFound_returnsFalse() {
        when(attendantRepository.findById(99L)).thenReturn(Optional.empty());

        boolean deleted = attendantService.deleteAttendant(99L);

        assertFalse(deleted);
        verify(attendantRepository).findById(99L);
        verify(attendantRepository, never()).delete(any());
    }

    @Test
    void getAttendantsByIds_usesRepositoryMethod() {
        when(attendantRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(sampleAttendant));

        List<Attendant> result = attendantService.getAttendantsByIds(List.of(1L, 2L));

        assertEquals(1, result.size());
        verify(attendantRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void updateBulkAvailability_updatesFlagAndSavesAll() {
        Attendant another = new Attendant(
                2L,
                "ATT-002",
                "Jane Doe",
                28,
                "Female",
                "US",
                "[\"EN\"]",
                AttendantType.CHEF,
                "[\"B737\"]",
                true,
                "[\"VEGAN\"]"
        );

        when(attendantRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(sampleAttendant, another));
        when(attendantRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<Attendant> result = attendantService.updateBulkAvailability(List.of(1L, 2L), false);

        assertEquals(2, result.size());
        assertFalse(result.get(0).getIsAvailable());
        assertFalse(result.get(1).getIsAvailable());
        verify(attendantRepository).findAllById(List.of(1L, 2L));
        verify(attendantRepository).saveAll(anyList());
    }
}


