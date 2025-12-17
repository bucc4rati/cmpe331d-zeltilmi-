package com.flightroster.flightinfo.acceptance.controller;

import com.flightroster.flightinfo.controller.FlightController;
import com.flightroster.flightinfo.entity.Airport;
import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.entity.VehicleType;
import com.flightroster.flightinfo.service.FlightService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Acceptance Testing for Flight Controller
 * Tests complete user scenarios and business workflows
 */
@WebMvcTest(FlightController.class)
class FlightAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(FlightAcceptanceTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void testCompleteFlightManagementWorkflow() throws Exception {
        logger.info("Acceptance Test: Complete flight management workflow");
        
        // Setup test data
        Airport source = new Airport();
        source.setAirportCode("IST");
        Airport dest = new Airport();
        dest.setAirportCode("AMS");
        VehicleType vehicleType = new VehicleType();
        vehicleType.setTypeName("A320");
        
        Flight flight = new Flight();
        flight.setFlightNumber("TK1234");
        flight.setFlightDate(LocalDateTime.now());
        flight.setDurationMinutes(120);
        flight.setDistanceKm(2000.0);
        flight.setSourceAirport(source);
        flight.setDestinationAirport(dest);
        flight.setVehicleType(vehicleType);
        flight.setIsSharedFlight(false);
        
        // Step 1: Create a new flight
        logger.info("Step 1: Creating new flight TK1234");
        when(flightService.createFlight(any(Flight.class))).thenReturn(flight);
        
        String createJson = """
                {
                  "flightNumber": "TK1234",
                  "flightDate": "2024-01-01T10:00:00",
                  "durationMinutes": 120,
                  "distanceKm": 2000.0,
                  "isSharedFlight": false
                }
                """;
        
        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK1234"));
        
        logger.info("Step 1 passed: Flight created successfully");
        
        // Step 2: Retrieve the created flight
        logger.info("Step 2: Retrieving flight TK1234");
        when(flightService.getFlightByNumber("TK1234")).thenReturn(flight);
        
        mockMvc.perform(get("/api/flights/TK1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK1234"));
        
        logger.info("Step 2 passed: Flight retrieved successfully");
        
        // Step 3: Search flights by airport
        logger.info("Step 3: Searching flights by airport IST");
        when(flightService.searchFlights("IST", null, null, null, null))
                .thenReturn(List.of(flight));
        
        mockMvc.perform(get("/api/flights/search")
                        .param("sourceAirport", "IST"))
                .andExpect(status().isOk());
        
        logger.info("Step 3 passed: Flight search by airport successful");
        
        // Step 4: Update the flight
        logger.info("Step 4: Updating flight TK1234");
        Flight updatedFlight = new Flight();
        updatedFlight.setFlightNumber("TK1234");
        updatedFlight.setDurationMinutes(140);
        when(flightService.updateFlight(eq("TK1234"), any(Flight.class))).thenReturn(updatedFlight);
        
        String updateJson = """
                {
                  "flightNumber": "TK1234",
                  "flightDate": "2024-01-01T10:00:00",
                  "durationMinutes": 140,
                  "distanceKm": 2000.0,
                  "isSharedFlight": false
                }
                """;
        
        mockMvc.perform(put("/api/flights/TK1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
        
        logger.info("Step 4 passed: Flight updated successfully");
        
        // Step 5: Delete the flight
        logger.info("Step 5: Deleting flight TK1234");
        when(flightService.deleteFlight("TK1234")).thenReturn(true);
        
        mockMvc.perform(delete("/api/flights/TK1234"))
                .andExpect(status().isOk());
        
        logger.info("Step 5 passed: Flight deleted successfully");
        logger.info("Acceptance Test completed: All workflow steps passed");
    }

    @Test
    void testFlightSearchScenarios() throws Exception {
        logger.info("Acceptance Test: Flight search scenarios");
        
        Flight flight = new Flight();
        flight.setFlightNumber("TK1234");
        
        // Scenario 1: Search by source airport
        logger.info("Scenario 1: Search by source airport");
        when(flightService.searchFlights("IST", null, null, null, null))
                .thenReturn(List.of(flight));
        
        mockMvc.perform(get("/api/flights/search")
                        .param("sourceAirport", "IST"))
                .andExpect(status().isOk());
        
        logger.info("Scenario 1 passed");
        
        // Scenario 2: Search by vehicle type
        logger.info("Scenario 2: Search by vehicle type");
        when(flightService.searchFlights(null, null, "A320", null, null))
                .thenReturn(List.of(flight));
        
        mockMvc.perform(get("/api/flights/search")
                        .param("vehicleType", "A320"))
                .andExpect(status().isOk());
        
        logger.info("Scenario 2 passed");
        
        // Scenario 3: Search by date range
        logger.info("Scenario 3: Search by date range");
        when(flightService.searchFlights(null, null, null, "2024-01-01T00:00", "2024-12-31T23:59"))
                .thenReturn(List.of(flight));
        
        mockMvc.perform(get("/api/flights/search")
                        .param("startDate", "2024-01-01T00:00")
                        .param("endDate", "2024-12-31T23:59"))
                .andExpect(status().isOk());
        
        logger.info("Scenario 3 passed");
        logger.info("Acceptance Test completed: All search scenarios passed");
    }
}

