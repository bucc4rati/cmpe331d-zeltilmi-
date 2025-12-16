package com.flightroster.main.service;

import com.flightroster.main.client.*;
import com.flightroster.main.entity.FlightRoster;
import com.flightroster.main.repository.FlightRosterRepository;
import com.flightroster.main.repository.RosterPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightRosterServiceTest {

    @Mock
    private FlightRosterRepository flightRosterRepository;

    @Mock
    private RosterPersonRepository rosterPersonRepository;

    @Mock
    private FlightInfoClient flightInfoClient;

    @Mock
    private PilotClient pilotClient;

    @Mock
    private CabinClient cabinClient;

    @Mock
    private PassengerClient passengerClient;

    @Mock
    private SeatAssignmentService seatAssignmentService;

    @InjectMocks
    private FlightRosterService flightRosterService;

    private Map<String, Object> mockFlight;
    private Map<String, Object> mockVehicleType;
    private List<Map<String, Object>> mockPilots;
    private List<Map<String, Object>> mockChiefs;
    private List<Map<String, Object>> mockRegulars;
    private List<Map<String, Object>> mockChefs;

    @BeforeEach
    void setUp() {
        // Setup mock vehicle type
        mockVehicleType = new HashMap<>();
        mockVehicleType.put("typeName", "Airbus A320");
        mockVehicleType.put("totalSeats", 180);
        mockVehicleType.put("businessSeats", 20);
        mockVehicleType.put("economySeats", 160);
        mockVehicleType.put("seatingPlan", "{\"rows\":30,\"seatsPerRow\":6,\"businessRows\":3}");

        // Setup mock flight
        mockFlight = new HashMap<>();
        mockFlight.put("flightNumber", "TK001");
        mockFlight.put("vehicleType", mockVehicleType);

        // Setup mock pilots
        mockPilots = new ArrayList<>();
        
        // Add SENIOR pilot
        Map<String, Object> seniorPilot = new HashMap<>();
        seniorPilot.put("id", 1L);
        seniorPilot.put("pilotId", "P001");
        seniorPilot.put("seniorityLevel", "SENIOR");
        seniorPilot.put("isAvailable", true);
        seniorPilot.put("vehicleType", "Airbus A320");
        mockPilots.add(seniorPilot);

        // Add JUNIOR pilot
        Map<String, Object> juniorPilot = new HashMap<>();
        juniorPilot.put("id", 2L);
        juniorPilot.put("pilotId", "P002");
        juniorPilot.put("seniorityLevel", "JUNIOR");
        juniorPilot.put("isAvailable", true);
        juniorPilot.put("vehicleType", "Airbus A320");
        mockPilots.add(juniorPilot);

        // Add TRAINEE pilot
        Map<String, Object> traineePilot = new HashMap<>();
        traineePilot.put("id", 3L);
        traineePilot.put("pilotId", "P003");
        traineePilot.put("seniorityLevel", "TRAINEE");
        traineePilot.put("isAvailable", true);
        traineePilot.put("vehicleType", "Airbus A320");
        mockPilots.add(traineePilot);

        // Setup mock cabin crew - CHIEF
        mockChiefs = new ArrayList<>();
        Map<String, Object> chief1 = new HashMap<>();
        chief1.put("id", 1L);
        chief1.put("attendantId", "C001");
        chief1.put("attendantType", "CHIEF");
        chief1.put("isAvailable", true);
        chief1.put("vehicleRestrictions", "[\"Airbus A320\"]");
        mockChiefs.add(chief1);

        // Setup mock cabin crew - REGULAR
        mockRegulars = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> regular = new HashMap<>();
            regular.put("id", (long) i);
            regular.put("attendantId", "R00" + i);
            regular.put("attendantType", "REGULAR");
            regular.put("isAvailable", true);
            regular.put("vehicleRestrictions", "[\"Airbus A320\"]");
            mockRegulars.add(regular);
        }

        // Setup mock cabin crew - CHEF
        mockChefs = new ArrayList<>();
        Map<String, Object> chef1 = new HashMap<>();
        chef1.put("id", 1L);
        chef1.put("attendantId", "CH001");
        chef1.put("attendantType", "CHEF");
        chef1.put("isAvailable", true);
        chef1.put("vehicleRestrictions", "[\"Airbus A320\"]");
        mockChefs.add(chef1);
    }

    @Test
    void testCreateRoster_Success() {
        // Given
        when(flightInfoClient.getFlightByNumber("TK001")).thenReturn(mockFlight);
        when(pilotClient.getPilotsByVehicleType("Airbus A320")).thenReturn(mockPilots);
        when(cabinClient.getAvailableChiefs()).thenReturn(mockChiefs);
        when(cabinClient.getAvailableRegulars()).thenReturn(mockRegulars);
        when(cabinClient.getAvailableChefs()).thenReturn(mockChefs);
        when(passengerClient.getPassengersByFlightId("TK001")).thenReturn(new ArrayList<>());
        when(seatAssignmentService.assignSeatsAutomatically(any(), any())).thenReturn(new ArrayList<>());
        when(flightRosterRepository.save(any(FlightRoster.class))).thenAnswer(invocation -> {
            FlightRoster roster = invocation.getArgument(0);
            roster.setId(1L);
            return roster;
        });

        // When
        FlightRoster result = flightRosterService.createRoster("TK001", "SQL");

        // Then
        assertNotNull(result);
        assertEquals("TK001", result.getFlightNumber());
        assertEquals("SQL", result.getDatabaseType());
        assertNotNull(result.getRosterData());
        verify(flightInfoClient, times(1)).getFlightByNumber("TK001");
        verify(pilotClient, times(1)).getPilotsByVehicleType("Airbus A320");
        verify(cabinClient, times(1)).getAvailableChiefs();
        verify(cabinClient, times(1)).getAvailableRegulars();
        verify(flightRosterRepository, times(1)).save(any(FlightRoster.class));
    }

    @Test
    void testCreateRoster_FlightNotFound() {
        // Given
        when(flightInfoClient.getFlightByNumber("TK999")).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightRosterService.createRoster("TK999", "SQL");
        });

        assertTrue(exception.getMessage().contains("Flight not found"));
        verify(flightInfoClient, times(1)).getFlightByNumber("TK999");
        verify(flightRosterRepository, never()).save(any(FlightRoster.class));
    }

    @Test
    void testCreateRoster_NoSeniorPilot() {
        // Given
        List<Map<String, Object>> pilotsWithoutSenior = new ArrayList<>();
        Map<String, Object> juniorPilot = new HashMap<>();
        juniorPilot.put("id", 2L);
        juniorPilot.put("pilotId", "P002");
        juniorPilot.put("seniorityLevel", "JUNIOR");
        juniorPilot.put("isAvailable", true);
        pilotsWithoutSenior.add(juniorPilot);

        when(flightInfoClient.getFlightByNumber("TK001")).thenReturn(mockFlight);
        when(pilotClient.getPilotsByVehicleType("Airbus A320")).thenReturn(pilotsWithoutSenior);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightRosterService.createRoster("TK001", "SQL");
        });

        assertTrue(exception.getMessage().contains("No available SENIOR pilot"));
        verify(pilotClient, times(1)).getPilotsByVehicleType("Airbus A320");
        verify(flightRosterRepository, never()).save(any(FlightRoster.class));
    }

    @Test
    void testCreateRoster_NoJuniorPilot() {
        // Given
        List<Map<String, Object>> pilotsWithoutJunior = new ArrayList<>();
        Map<String, Object> seniorPilot = new HashMap<>();
        seniorPilot.put("id", 1L);
        seniorPilot.put("pilotId", "P001");
        seniorPilot.put("seniorityLevel", "SENIOR");
        seniorPilot.put("isAvailable", true);
        pilotsWithoutJunior.add(seniorPilot);

        when(flightInfoClient.getFlightByNumber("TK001")).thenReturn(mockFlight);
        when(pilotClient.getPilotsByVehicleType("Airbus A320")).thenReturn(pilotsWithoutJunior);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightRosterService.createRoster("TK001", "SQL");
        });

        assertTrue(exception.getMessage().contains("No available JUNIOR pilot"));
        verify(pilotClient, times(1)).getPilotsByVehicleType("Airbus A320");
        verify(flightRosterRepository, never()).save(any(FlightRoster.class));
    }

    @Test
    void testCreateRoster_NoChiefAttendant() {
        // Given
        when(flightInfoClient.getFlightByNumber("TK001")).thenReturn(mockFlight);
        when(pilotClient.getPilotsByVehicleType("Airbus A320")).thenReturn(mockPilots);
        when(cabinClient.getAvailableChiefs()).thenReturn(new ArrayList<>());
        when(cabinClient.getAvailableRegulars()).thenReturn(mockRegulars);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightRosterService.createRoster("TK001", "SQL");
        });

        assertTrue(exception.getMessage().contains("No available CHIEF attendant"));
        verify(cabinClient, times(1)).getAvailableChiefs();
        verify(flightRosterRepository, never()).save(any(FlightRoster.class));
    }

    @Test
    void testCreateRoster_NotEnoughRegularAttendants() {
        // Given
        List<Map<String, Object>> insufficientRegulars = new ArrayList<>();
        for (int i = 1; i <= 2; i++) { // Only 2 regulars, need at least 4
            Map<String, Object> regular = new HashMap<>();
            regular.put("id", (long) i);
            regular.put("attendantId", "R00" + i);
            regular.put("attendantType", "REGULAR");
            regular.put("isAvailable", true);
            regular.put("vehicleRestrictions", "[\"Airbus A320\"]");
            insufficientRegulars.add(regular);
        }

        when(flightInfoClient.getFlightByNumber("TK001")).thenReturn(mockFlight);
        when(pilotClient.getPilotsByVehicleType("Airbus A320")).thenReturn(mockPilots);
        when(cabinClient.getAvailableChiefs()).thenReturn(mockChiefs);
        when(cabinClient.getAvailableRegulars()).thenReturn(insufficientRegulars);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightRosterService.createRoster("TK001", "SQL");
        });

        assertTrue(exception.getMessage().contains("Not enough REGULAR attendants"));
        verify(cabinClient, times(1)).getAvailableRegulars();
        verify(flightRosterRepository, never()).save(any(FlightRoster.class));
    }

    @Test
    void testGetAllRosters() {
        // Given
        List<FlightRoster> mockRosters = new ArrayList<>();
        FlightRoster roster1 = new FlightRoster();
        roster1.setId(1L);
        roster1.setFlightNumber("TK001");
        mockRosters.add(roster1);

        when(flightRosterRepository.findByIsActiveTrue()).thenReturn(mockRosters);

        // When
        List<FlightRoster> result = flightRosterService.getAllRosters();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TK001", result.get(0).getFlightNumber());
        verify(flightRosterRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetRosterById() {
        // Given
        FlightRoster mockRoster = new FlightRoster();
        mockRoster.setId(1L);
        mockRoster.setFlightNumber("TK001");

        when(flightRosterRepository.findById(1L)).thenReturn(Optional.of(mockRoster));

        // When
        FlightRoster result = flightRosterService.getRosterById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TK001", result.getFlightNumber());
        verify(flightRosterRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRosterById_NotFound() {
        // Given
        when(flightRosterRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        FlightRoster result = flightRosterService.getRosterById(999L);

        // Then
        assertNull(result);
        verify(flightRosterRepository, times(1)).findById(999L);
    }
}

