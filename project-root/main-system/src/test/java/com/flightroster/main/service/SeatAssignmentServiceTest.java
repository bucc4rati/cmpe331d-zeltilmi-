package com.flightroster.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeatAssignmentServiceTest {

    @InjectMocks
    private SeatAssignmentService seatAssignmentService;

    private Map<String, Object> vehicleType;
    private List<Map<String, Object>> passengers;

    @BeforeEach
    void setUp() {
        // Setup vehicle type
        vehicleType = new HashMap<>();
        vehicleType.put("totalSeats", 180);
        vehicleType.put("businessSeats", 24);
        vehicleType.put("economySeats", 156);
        vehicleType.put("seatingPlan", "{\"rows\":30,\"seatsPerRow\":6,\"businessRows\":4}");

        // Setup passengers
        passengers = new ArrayList<>();
    }

    @Test
    void testAssignSeatsAutomatically_EmptyPassengers() {
        List<Map<String, Object>> result = seatAssignmentService.assignSeatsAutomatically(
                new ArrayList<>(), vehicleType);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAssignSeatsAutomatically_NullPassengers() {
        List<Map<String, Object>> result = seatAssignmentService.assignSeatsAutomatically(
                null, vehicleType);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAssignSeatsAutomatically_BusinessClassPassengers() {
        Map<String, Object> passenger1 = new HashMap<>();
        passenger1.put("passengerId", "P001");
        passenger1.put("name", "John Doe");
        passenger1.put("seatType", "BUSINESS");

        Map<String, Object> passenger2 = new HashMap<>();
        passenger2.put("passengerId", "P002");
        passenger2.put("name", "Jane Doe");
        passenger2.put("seatType", "BUSINESS");

        passengers.add(passenger1);
        passengers.add(passenger2);

        List<Map<String, Object>> result = seatAssignmentService.assignSeatsAutomatically(
                passengers, vehicleType);

        assertEquals(2, result.size());
        assertNotNull(result.get(0).get("seatNumber"));
        assertNotNull(result.get(1).get("seatNumber"));
        assertEquals("BUSINESS", result.get(0).get("seatType"));
        assertEquals("BUSINESS", result.get(1).get("seatType"));
    }

    @Test
    void testAssignSeatsAutomatically_EconomyClassPassengers() {
        Map<String, Object> passenger1 = new HashMap<>();
        passenger1.put("passengerId", "P001");
        passenger1.put("name", "John Doe");
        passenger1.put("seatType", "ECONOMY");

        Map<String, Object> passenger2 = new HashMap<>();
        passenger2.put("passengerId", "P002");
        passenger2.put("name", "Jane Doe");
        passenger2.put("seatType", "ECONOMY");

        passengers.add(passenger1);
        passengers.add(passenger2);

        List<Map<String, Object>> result = seatAssignmentService.assignSeatsAutomatically(
                passengers, vehicleType);

        assertEquals(2, result.size());
        assertNotNull(result.get(0).get("seatNumber"));
        assertNotNull(result.get(1).get("seatNumber"));
        assertEquals("ECONOMY", result.get(0).get("seatType"));
        assertEquals("ECONOMY", result.get(1).get("seatType"));
    }

    @Test
    void testAssignSeatsAutomatically_MixedClassPassengers() {
        Map<String, Object> businessPassenger = new HashMap<>();
        businessPassenger.put("passengerId", "P001");
        businessPassenger.put("name", "Business Passenger");
        businessPassenger.put("seatType", "BUSINESS");

        Map<String, Object> economyPassenger = new HashMap<>();
        economyPassenger.put("passengerId", "P002");
        economyPassenger.put("name", "Economy Passenger");
        economyPassenger.put("seatType", "ECONOMY");

        passengers.add(businessPassenger);
        passengers.add(economyPassenger);

        List<Map<String, Object>> result = seatAssignmentService.assignSeatsAutomatically(
                passengers, vehicleType);

        assertEquals(2, result.size());
        assertEquals("BUSINESS", result.get(0).get("seatType"));
        assertEquals("ECONOMY", result.get(1).get("seatType"));
    }

    @Test
    void testGetSeatMap_EmptyPassengers() {
        Map<String, Object> seatMap = seatAssignmentService.getSeatMap(
                new ArrayList<>(), vehicleType);

        assertNotNull(seatMap);
        assertTrue(seatMap.containsKey("layout"));
        assertTrue(seatMap.containsKey("totalRows"));
        assertTrue(seatMap.containsKey("seatsPerRow"));
        assertTrue(seatMap.containsKey("businessRows"));
        assertTrue(seatMap.containsKey("totalSeats"));
    }

    @Test
    void testGetSeatMap_WithPassengers() {
        Map<String, Object> passenger1 = new HashMap<>();
        passenger1.put("passengerId", "P001");
        passenger1.put("name", "John Doe");
        passenger1.put("seatNumber", "1A");
        passenger1.put("seatType", "BUSINESS");

        passengers.add(passenger1);

        Map<String, Object> seatMap = seatAssignmentService.getSeatMap(passengers, vehicleType);

        assertNotNull(seatMap);
        assertTrue(seatMap.containsKey("layout"));
        
        @SuppressWarnings("unchecked")
        List<List<Map<String, Object>>> layout = (List<List<Map<String, Object>>>) seatMap.get("layout");
        assertNotNull(layout);
        assertFalse(layout.isEmpty());
    }

    @Test
    void testAssignSeatsAutomatically_NoSeatingPlan() {
        vehicleType.remove("seatingPlan");
        
        Map<String, Object> passenger = new HashMap<>();
        passenger.put("passengerId", "P001");
        passenger.put("name", "John Doe");
        passenger.put("seatType", "ECONOMY");

        passengers.add(passenger);

        List<Map<String, Object>> result = seatAssignmentService.assignSeatsAutomatically(
                passengers, vehicleType);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).get("seatNumber"));
    }
}

