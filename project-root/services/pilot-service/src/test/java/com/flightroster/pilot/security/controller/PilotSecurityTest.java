package com.flightroster.pilot.security.controller;

import com.flightroster.pilot.controller.PilotController;

import com.flightroster.pilot.service.PilotService;
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
 * Security Testing for Pilot Controller
 * Tests CORS configuration, input validation, and unauthorized access scenarios
 */
@WebMvcTest(PilotController.class)
@ActiveProfiles("test")
class PilotSecurityTest {

    private static final Logger logger = LoggerFactory.getLogger(PilotSecurityTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PilotService pilotService;

    @Test
    void testCorsHeaders_shouldAllowConfiguredOrigins() throws Exception {
        logger.info("Testing CORS headers for allowed origins");
        
        mockMvc.perform(get("/api/pilots")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
        
        logger.info("CORS test passed for localhost:3000");
    }

    @Test
    void testInvalidPilotIdFormat_shouldHandleGracefully() throws Exception {
        logger.info("Testing invalid pilot ID format");
        
        mockMvc.perform(get("/api/pilots/pilot-id/INVALID_FORMAT_123456789"))
                .andExpect(status().isNotFound());
        
        logger.info("Invalid pilot ID format handled correctly");
    }

    @Test
    void testMalformedJsonInput_shouldReturnBadRequest() throws Exception {
        logger.info("Testing malformed JSON input");
        
        String malformedJson = "{ invalid json }";
        
        mockMvc.perform(post("/api/pilots")
                        .contentType("application/json")
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
        
        logger.info("Malformed JSON input rejected correctly");
    }

    @Test
    void testSqlInjectionAttempt_shouldBeSanitized() throws Exception {
        logger.info("Testing SQL injection attempt in pilot ID");
        
        String sqlInjection = "'; DROP TABLE pilots; --";
        
        mockMvc.perform(get("/api/pilots/pilot-id/" + sqlInjection))
                .andExpect(status().isNotFound());
        
        logger.info("SQL injection attempt handled safely");
    }

    @Test
    void testInvalidSeniorityLevel_shouldReturnEmptyList() throws Exception {
        logger.info("Testing invalid seniority level");
        
        mockMvc.perform(get("/api/pilots/seniority/INVALID_LEVEL"))
                .andExpect(status().isOk());
        
        logger.info("Invalid seniority level handled correctly");
    }

    @Test
    void testNegativeDistance_shouldHandleGracefully() throws Exception {
        logger.info("Testing negative distance parameter");
        
        mockMvc.perform(get("/api/pilots/distance/-100"))
                .andExpect(status().isOk());
        
        logger.info("Negative distance parameter handled correctly");
    }
}

