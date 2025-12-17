package com.flightroster.main.controller;

import com.flightroster.main.entity.FlightRoster;
import com.flightroster.main.service.FlightRosterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FlightRosterController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class FlightRosterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightRosterService flightRosterService;

    @Test
    void testGetAllRosters_Success() throws Exception {
        // Given
        List<FlightRoster> mockRosters = new ArrayList<>();
        FlightRoster roster1 = new FlightRoster();
        roster1.setId(1L);
        roster1.setFlightNumber("TK001");
        mockRosters.add(roster1);

        when(flightRosterService.getAllRosters()).thenReturn(mockRosters);

        // When & Then
        mockMvc.perform(get("/api/rosters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").value("TK001"));
    }

    @Test
    void testGetRosterById_Success() throws Exception {
        // Given
        FlightRoster mockRoster = new FlightRoster();
        mockRoster.setId(1L);
        mockRoster.setFlightNumber("TK001");

        when(flightRosterService.getRosterById(1L)).thenReturn(mockRoster);

        // When & Then
        mockMvc.perform(get("/api/rosters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.flightNumber").value("TK001"));
    }

    @Test
    void testGetRosterById_NotFound() throws Exception {
        // Given
        when(flightRosterService.getRosterById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/rosters/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRoster_Success() throws Exception {
        // Given
        FlightRoster mockRoster = new FlightRoster();
        mockRoster.setId(1L);
        mockRoster.setFlightNumber("TK001");
        mockRoster.setDatabaseType("SQL");

        when(flightRosterService.createRoster("TK001", "SQL")).thenReturn(mockRoster);

        // When & Then
        mockMvc.perform(get("/api/rosters/new")
                .param("flightNumber", "TK001")
                .param("databaseType", "SQL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK001"))
                .andExpect(jsonPath("$.databaseType").value("SQL"));
    }

    @Test
    void testCreateRoster_FlightNotFound() throws Exception {
        // Given
        when(flightRosterService.createRoster(anyString(), anyString()))
                .thenThrow(new RuntimeException("Flight not found: TK999"));

        // When & Then
        mockMvc.perform(get("/api/rosters/new")
                .param("flightNumber", "TK999")
                .param("databaseType", "SQL"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Flight not found: TK999"));
    }

    @Test
    void testGetRosterByFlightNumber_Success() throws Exception {
        // Given
        FlightRoster mockRoster = new FlightRoster();
        mockRoster.setId(1L);
        mockRoster.setFlightNumber("TK001");

        when(flightRosterService.getRosterByFlightNumber("TK001")).thenReturn(mockRoster);

        // When & Then
        mockMvc.perform(get("/api/rosters/flight/TK001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK001"));
    }

    @Test
    void testGetRosterByFlightNumber_NotFound() throws Exception {
        // Given
        when(flightRosterService.getRosterByFlightNumber("TK999")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/rosters/flight/TK999"))
                .andExpect(status().isNotFound());
    }
}

