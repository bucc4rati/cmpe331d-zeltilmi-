package com.flightroster.pilot.performance.controller;

import com.flightroster.pilot.controller.PilotController;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.service.PilotService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Performance Testing for Pilot Controller
 * Tests response time and performance metrics
 */
@WebMvcTest(PilotController.class)
class PilotPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(PilotPerformanceTest.class);
    private static final long MAX_RESPONSE_TIME_MS = 1000; // 1 second

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PilotService pilotService;

    @Test
    void testGetAllPilots_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/pilots");
        
        when(pilotService.getAllPilots()).thenReturn(List.of(new Pilot()));
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/pilots"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/pilots: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }

    @Test
    void testGetAvailablePilots_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/pilots/available");
        
        when(pilotService.getAvailablePilots()).thenReturn(List.of(new Pilot()));
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/pilots/available"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/pilots/available: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }

    @Test
    void testGetPilotsByVehicleType_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/pilots/vehicle-type/{vehicleType}");
        
        when(pilotService.getPilotsByVehicleType(anyString())).thenReturn(List.of(new Pilot()));
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/pilots/vehicle-type/A320"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/pilots/vehicle-type/{vehicleType}: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }

    @Test
    void testGetPilotsByMaxDistance_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/pilots/distance/{distance}");
        
        when(pilotService.getPilotsByMaxDistance(anyDouble())).thenReturn(List.of(new Pilot()));
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/pilots/distance/1500"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/pilots/distance/{distance}: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }
}

