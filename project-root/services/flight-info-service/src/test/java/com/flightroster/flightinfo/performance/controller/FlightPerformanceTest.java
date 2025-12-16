package com.flightroster.flightinfo.performance.controller;

import com.flightroster.flightinfo.controller.FlightController;
import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.service.FlightService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Performance Testing for Flight Controller
 * Tests response time and performance metrics
 */
@WebMvcTest(FlightController.class)
class FlightPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(FlightPerformanceTest.class);
    private static final long MAX_RESPONSE_TIME_MS = 1000; // 1 second

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void testGetAllFlights_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/flights");
        
        when(flightService.getAllFlights()).thenReturn(List.of(new Flight()));
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/flights: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }

    @Test
    void testSearchFlights_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/flights/search");
        
        when(flightService.searchFlights(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new Flight()));
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/flights/search")
                        .param("sourceAirport", "IST"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/flights/search: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }

    @Test
    void testGetFlightByNumber_responseTime() throws Exception {
        logger.info("Testing performance: GET /api/flights/{flightNumber}");
        
        Flight flight = new Flight();
        flight.setFlightNumber("TK1234");
        when(flightService.getFlightByNumber("TK1234")).thenReturn(flight);
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/flights/TK1234"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        logger.info("Response time for GET /api/flights/{flightNumber}: {} ms", responseTime);
        
        assert responseTime < MAX_RESPONSE_TIME_MS : 
            "Response time " + responseTime + " ms exceeds maximum " + MAX_RESPONSE_TIME_MS + " ms";
    }
}

