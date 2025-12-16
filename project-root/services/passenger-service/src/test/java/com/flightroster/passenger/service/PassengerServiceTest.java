package com.flightroster.passenger.service;

import com.flightroster.passenger.entity.Passenger;
import com.flightroster.passenger.entity.SeatType;
import com.flightroster.passenger.repository.PassengerRepository;
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
class PassengerServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerService passengerService;

    private Passenger samplePassenger;

    @BeforeEach
    void setUp() {
        samplePassenger = new Passenger(
                1L,
                "PAX-001",
                "FL1234",
                "Alice",
                25,
                "Female",
                "TR",
                SeatType.ECONOMY,
                null,
                false,
                null,
                null
        );
    }

    @Test
    void getAllPassengers_returnsList() {
        when(passengerRepository.findAll()).thenReturn(List.of(samplePassenger));

        List<Passenger> result = passengerService.getAllPassengers();

        assertEquals(1, result.size());
        assertEquals("PAX-001", result.get(0).getPassengerId());
        verify(passengerRepository).findAll();
    }

    @Test
    void getPassengersByFlightId_usesRepositoryMethod() {
        when(passengerRepository.findByFlightId("FL1234")).thenReturn(List.of(samplePassenger));

        List<Passenger> result = passengerService.getPassengersByFlightId("FL1234");

        assertEquals(1, result.size());
        verify(passengerRepository).findByFlightId("FL1234");
    }

    @Test
    void getPassengerById_found_returnsEntity() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(samplePassenger));

        Passenger result = passengerService.getPassengerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(passengerRepository).findById(1L);
    }

    @Test
    void getPassengerById_notFound_returnsNull() {
        when(passengerRepository.findById(99L)).thenReturn(Optional.empty());

        Passenger result = passengerService.getPassengerById(99L);

        assertNull(result);
        verify(passengerRepository).findById(99L);
    }

    @Test
    void getPassengerByPassengerId_found_returnsEntity() {
        when(passengerRepository.findByPassengerId("PAX-001")).thenReturn(Optional.of(samplePassenger));

        Passenger result = passengerService.getPassengerByPassengerId("PAX-001");

        assertNotNull(result);
        assertEquals("PAX-001", result.getPassengerId());
        verify(passengerRepository).findByPassengerId("PAX-001");
    }

    @Test
    void getPassengerByPassengerId_notFound_returnsNull() {
        when(passengerRepository.findByPassengerId("UNKNOWN")).thenReturn(Optional.empty());

        Passenger result = passengerService.getPassengerByPassengerId("UNKNOWN");

        assertNull(result);
        verify(passengerRepository).findByPassengerId("UNKNOWN");
    }

    @Test
    void getPassengersByFlightAndSeatType_validType_callsRepository() {
        when(passengerRepository.findByFlightIdAndSeatType("FL1234", SeatType.ECONOMY))
                .thenReturn(List.of(samplePassenger));

        List<Passenger> result = passengerService.getPassengersByFlightAndSeatType("FL1234", "economy");

        assertEquals(1, result.size());
        verify(passengerRepository).findByFlightIdAndSeatType("FL1234", SeatType.ECONOMY);
    }

    @Test
    void getPassengersByFlightAndSeatType_invalidType_returnsEmptyList() {
        List<Passenger> result = passengerService.getPassengersByFlightAndSeatType("FL1234", "invalid_type");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(passengerRepository, never()).findByFlightIdAndSeatType(anyString(), any());
    }

    @Test
    void getInfantsByFlightId_usesRepositoryMethod() {
        when(passengerRepository.findInfantsByFlightId("FL1234")).thenReturn(List.of(samplePassenger));

        List<Passenger> result = passengerService.getInfantsByFlightId("FL1234");

        assertEquals(1, result.size());
        verify(passengerRepository).findInfantsByFlightId("FL1234");
    }

    @Test
    void getUnassignedSeatsByFlightId_usesRepositoryMethod() {
        when(passengerRepository.findUnassignedSeatsByFlightId("FL1234")).thenReturn(List.of(samplePassenger));

        List<Passenger> result = passengerService.getUnassignedSeatsByFlightId("FL1234");

        assertEquals(1, result.size());
        verify(passengerRepository).findUnassignedSeatsByFlightId("FL1234");
    }

    @Test
    void getAssignedSeatsByFlightId_usesRepositoryMethod() {
        when(passengerRepository.findAssignedSeatsByFlightId("FL1234")).thenReturn(List.of(samplePassenger));

        List<Passenger> result = passengerService.getAssignedSeatsByFlightId("FL1234");

        assertEquals(1, result.size());
        verify(passengerRepository).findAssignedSeatsByFlightId("FL1234");
    }

    @Test
    void createPassenger_savesEntity() {
        when(passengerRepository.save(samplePassenger)).thenReturn(samplePassenger);

        Passenger result = passengerService.createPassenger(samplePassenger);

        assertEquals(samplePassenger, result);
        verify(passengerRepository).save(samplePassenger);
    }

    @Test
    void updatePassenger_found_updatesAndSaves() {
        Passenger updatedDetails = new Passenger(
                null,
                "PAX-002",
                "FL5678",
                "Bob",
                40,
                "Male",
                "US",
                SeatType.BUSINESS,
                "1A",
                true,
                "PAX-001",
                "[\"PAX-003\"]"
        );

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(samplePassenger));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Passenger result = passengerService.updatePassenger(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("PAX-002", result.getPassengerId());
        assertEquals("FL5678", result.getFlightId());
        assertEquals("Bob", result.getName());
        assertEquals(SeatType.BUSINESS, result.getSeatType());
        assertEquals("1A", result.getSeatNumber());
        assertTrue(result.getIsInfant());
        assertEquals("PAX-001", result.getParentPassengerId());
        assertEquals("[\"PAX-003\"]", result.getAffiliatedPassengerIds());
        verify(passengerRepository).findById(1L);
        verify(passengerRepository).save(samplePassenger);
    }

    @Test
    void updatePassenger_notFound_returnsNull() {
        when(passengerRepository.findById(99L)).thenReturn(Optional.empty());

        Passenger result = passengerService.updatePassenger(99L, samplePassenger);

        assertNull(result);
        verify(passengerRepository).findById(99L);
        verify(passengerRepository, never()).save(any());
    }

    @Test
    void deletePassenger_found_deletesAndReturnsTrue() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(samplePassenger));

        boolean deleted = passengerService.deletePassenger(1L);

        assertTrue(deleted);
        verify(passengerRepository).findById(1L);
        verify(passengerRepository).delete(samplePassenger);
    }

    @Test
    void deletePassenger_notFound_returnsFalse() {
        when(passengerRepository.findById(99L)).thenReturn(Optional.empty());

        boolean deleted = passengerService.deletePassenger(99L);

        assertFalse(deleted);
        verify(passengerRepository).findById(99L);
        verify(passengerRepository, never()).delete(any());
    }

    @Test
    void assignSeat_found_updatesSeatAndSaves() {
        when(passengerRepository.findByPassengerId("PAX-001")).thenReturn(Optional.of(samplePassenger));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Passenger result = passengerService.assignSeat("PAX-001", "12C");

        assertNotNull(result);
        assertEquals("12C", result.getSeatNumber());
        verify(passengerRepository).findByPassengerId("PAX-001");
        verify(passengerRepository).save(samplePassenger);
    }

    @Test
    void assignSeat_notFound_returnsNull() {
        when(passengerRepository.findByPassengerId("UNKNOWN")).thenReturn(Optional.empty());

        Passenger result = passengerService.assignSeat("UNKNOWN", "12C");

        assertNull(result);
        verify(passengerRepository).findByPassengerId("UNKNOWN");
        verify(passengerRepository, never()).save(any());
    }
}


