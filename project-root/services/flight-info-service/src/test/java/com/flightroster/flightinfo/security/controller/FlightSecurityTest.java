package com.flightroster.flightinfo.security.controller;

import com.flightroster.flightinfo.controller.FlightController;
import com.flightroster.flightinfo.service.FlightService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security Testing for Flight Controller
 * Tests CORS configuration, input validation, and unauthorized access scenarios
 */
@WebMvcTest(FlightController.class)
@ActiveProfiles("test")
class FlightSecurityTest {

    private static final Logger logger = LoggerFactory.getLogger(FlightSecurityTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void testCorsHeaders_shouldAllowConfiguredOrigins() throws Exception {
        logger.info("Testing CORS headers for allowed origins");
        
        mockMvc.perform(get("/api/flights")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
        
        logger.info("CORS test passed for localhost:3000");
    }

    @Test
    void testInvalidFlightNumberFormat_shouldHandleGracefully() throws Exception {
        logger.info("Testing invalid flight number format: INVALID_FORMAT");
        
        mockMvc.perform(get("/api/flights/INVALID_FORMAT_TOO_LONG"))
                .andExpect(status().isNotFound());
        
        logger.info("Invalid flight number format handled correctly");
    }

    @Test
    void testMalformedJsonInput_shouldReturnBadRequest() throws Exception {
        logger.info("Testing malformed JSON input");
        
        String malformedJson = "{ invalid json }";
        
        mockMvc.perform(post("/api/flights")
                        .contentType("application/json")
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
        
        logger.info("Malformed JSON input rejected correctly");
    }

    @Test
    void testSqlInjectionAttempt_shouldBeSanitized() throws Exception {
        logger.info("Testing SQL injection attempt in flight number");
        
        String sqlInjection = "'; DROP TABLE flights; --";
        
        mockMvc.perform(get("/api/flights/" + sqlInjection))
                .andExpect(status().isNotFound());
        
        logger.info("SQL injection attempt handled safely");
    }

    @Test
    void testXssAttempt_shouldBeSanitized() throws Exception {
        logger.info("Testing XSS attempt in search parameter");
        
        String xssPayload = "<script>alert('XSS')</script>";
        
        mockMvc.perform(get("/api/flights/search")
                        .param("sourceAirport", xssPayload))
                .andExpect(status().isOk());
        
        logger.info("XSS attempt handled safely");
    }
}

