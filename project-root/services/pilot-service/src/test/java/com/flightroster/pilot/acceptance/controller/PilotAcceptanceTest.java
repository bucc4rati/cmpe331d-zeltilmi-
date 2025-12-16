package com.flightroster.pilot.acceptance.controller;

import com.flightroster.pilot.controller.PilotController;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.entity.SeniorityLevel;
import com.flightroster.pilot.service.PilotService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Acceptance Testing for Pilot Controller
 * Tests complete user scenarios and business workflows
 */
@WebMvcTest(PilotController.class)
class PilotAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(PilotAcceptanceTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PilotService pilotService;

    private Pilot buildSamplePilot() {
        Pilot pilot = new Pilot();
        pilot.setId(1L);
        pilot.setPilotId("P123");
        pilot.setName("John Doe");
        pilot.setAge(40);
        pilot.setGender("Male");
        pilot.setNationality("TR");
        pilot.setKnownLanguages("[\"EN\",\"TR\"]");
        pilot.setVehicleRestriction("A320");
        pilot.setMaxDistanceKm(3000.0);
        pilot.setSeniorityLevel(SeniorityLevel.SENIOR);
        pilot.setIsAvailable(true);
        return pilot;
    }

    @Test
    void testCompletePilotManagementWorkflow() throws Exception {
        logger.info("Acceptance Test: Complete pilot management workflow");
        
        Pilot pilot = buildSamplePilot();
        
        // Step 1: Create a new pilot
        logger.info("Step 1: Creating new pilot P123");
        when(pilotService.createPilot(any(Pilot.class))).thenReturn(pilot);
        
        String createJson = """
                {
                  "pilotId": "P123",
                  "name": "John Doe",
                  "age": 40,
                  "gender": "Male",
                  "nationality": "TR",
                  "knownLanguages": "[\\"EN\\",\\"TR\\"]",
                  "vehicleRestriction": "A320",
                  "maxDistanceKm": 3000.0,
                  "seniorityLevel": "SENIOR",
                  "isAvailable": true
                }
                """;
        
        mockMvc.perform(post("/api/pilots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pilotId").value("P123"));
        
        logger.info("Step 1 passed: Pilot created successfully");
        
        // Step 2: Retrieve the created pilot
        logger.info("Step 2: Retrieving pilot by ID");
        when(pilotService.getPilotById(1L)).thenReturn(pilot);
        
        mockMvc.perform(get("/api/pilots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pilotId").value("P123"));
        
        logger.info("Step 2 passed: Pilot retrieved successfully");
        
        // Step 3: Search pilots by vehicle type
        logger.info("Step 3: Searching pilots by vehicle type A320");
        when(pilotService.getPilotsByVehicleType("A320")).thenReturn(List.of(pilot));
        
        mockMvc.perform(get("/api/pilots/vehicle-type/A320"))
                .andExpect(status().isOk());
        
        logger.info("Step 3 passed: Pilot search by vehicle type successful");
        
        // Step 4: Update pilot availability
        logger.info("Step 4: Updating pilot availability");
        pilot.setIsAvailable(false);
        when(pilotService.updateAvailability(1L, false)).thenReturn(pilot);
        
        mockMvc.perform(put("/api/pilots/1/availability")
                        .param("isAvailable", "false"))
                .andExpect(status().isOk());
        
        logger.info("Step 4 passed: Pilot availability updated successfully");
        
        // Step 5: Get available pilots
        logger.info("Step 5: Getting available pilots");
        when(pilotService.getAvailablePilots()).thenReturn(List.of());
        
        mockMvc.perform(get("/api/pilots/available"))
                .andExpect(status().isOk());
        
        logger.info("Step 5 passed: Available pilots retrieved successfully");
        
        // Step 6: Update pilot details
        logger.info("Step 6: Updating pilot details");
        pilot.setName("Jane Doe");
        when(pilotService.updatePilot(eq(1L), any(Pilot.class))).thenReturn(pilot);
        
        String updateJson = """
                {
                  "pilotId": "P123",
                  "name": "Jane Doe",
                  "age": 40,
                  "gender": "Female",
                  "nationality": "TR",
                  "knownLanguages": "[\\"EN\\"]",
                  "vehicleRestriction": "A320",
                  "maxDistanceKm": 3000.0,
                  "seniorityLevel": "SENIOR",
                  "isAvailable": false
                }
                """;
        
        mockMvc.perform(put("/api/pilots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
        
        logger.info("Step 6 passed: Pilot details updated successfully");
        
        // Step 7: Delete the pilot
        logger.info("Step 7: Deleting pilot");
        when(pilotService.deletePilot(1L)).thenReturn(true);
        
        mockMvc.perform(delete("/api/pilots/1"))
                .andExpect(status().isOk());
        
        logger.info("Step 7 passed: Pilot deleted successfully");
        logger.info("Acceptance Test completed: All workflow steps passed");
    }

    @Test
    void testPilotSearchAndFilterScenarios() throws Exception {
        logger.info("Acceptance Test: Pilot search and filter scenarios");
        
        Pilot pilot = buildSamplePilot();
        
        // Scenario 1: Search by vehicle type
        logger.info("Scenario 1: Search by vehicle type A320");
        when(pilotService.getPilotsByVehicleType("A320")).thenReturn(List.of(pilot));
        
        mockMvc.perform(get("/api/pilots/vehicle-type/A320"))
                .andExpect(status().isOk());
        
        logger.info("Scenario 1 passed");
        
        // Scenario 2: Search by seniority level
        logger.info("Scenario 2: Search by seniority level SENIOR");
        when(pilotService.getPilotsBySeniorityLevel("SENIOR")).thenReturn(List.of(pilot));
        
        mockMvc.perform(get("/api/pilots/seniority/SENIOR"))
                .andExpect(status().isOk());
        
        logger.info("Scenario 2 passed");
        
        // Scenario 3: Search by max distance
        logger.info("Scenario 3: Search by max distance 1500 km");
        when(pilotService.getPilotsByMaxDistance(1500.0)).thenReturn(List.of(pilot));
        
        mockMvc.perform(get("/api/pilots/distance/1500"))
                .andExpect(status().isOk());
        
        logger.info("Scenario 3 passed");
        
        // Scenario 4: Bulk availability update
        logger.info("Scenario 4: Bulk availability update");
        when(pilotService.updateBulkAvailability(any(), anyBoolean())).thenReturn(List.of(pilot));
        
        String json = "[1,2,3]";
        mockMvc.perform(put("/api/pilots/bulk-availability")
                        .param("isAvailable", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        
        logger.info("Scenario 4 passed");
        logger.info("Acceptance Test completed: All search and filter scenarios passed");
    }
}

