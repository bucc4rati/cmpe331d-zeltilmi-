package com.flightroster.flightinfo.blackbox.controller;

import com.flightroster.flightinfo.controller.FlightController;
import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.service.FlightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void getAllFlights_returnsOk() throws Exception {
        Mockito.when(flightService.getAllFlights()).thenReturn(List.of(new Flight()));

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk());
    }

    @Test
    void getFlightByNumber_found_returnsOk() throws Exception {
        Flight flight = new Flight();
        flight.setFlightNumber("TK1234");
        Mockito.when(flightService.getFlightByNumber("TK1234")).thenReturn(flight);

        mockMvc.perform(get("/api/flights/TK1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK1234"));
    }

    @Test
    void getFlightByNumber_notFound_returns404() throws Exception {
        Mockito.when(flightService.getFlightByNumber(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/flights/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchFlights_returnsOk() throws Exception {
        Mockito.when(flightService.searchFlights(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new Flight()));

        mockMvc.perform(get("/api/flights/search")
                        .param("sourceAirport", "IST"))
                .andExpect(status().isOk());
    }

    @Test
    void createFlight_returnsOk() throws Exception {
        Flight flight = new Flight();
        flight.setFlightNumber("TK1234");
        flight.setFlightDate(LocalDateTime.now());
        Mockito.when(flightService.createFlight(any(Flight.class))).thenReturn(flight);

        String json = """
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
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("TK1234"));
    }

    @Test
    void updateFlight_found_returnsOk() throws Exception {
        Flight flight = new Flight();
        flight.setFlightNumber("TK1234");
        Mockito.when(flightService.updateFlight(anyString(), any(Flight.class))).thenReturn(flight);

        String json = """
                {
                  "flightNumber": "TK1234",
                  "flightDate": "2024-01-01T10:00:00",
                  "durationMinutes": 120,
                  "distanceKm": 2000.0,
                  "isSharedFlight": false
                }
                """;

        mockMvc.perform(put("/api/flights/TK1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateFlight_notFound_returns404() throws Exception {
        Mockito.when(flightService.updateFlight(anyString(), any(Flight.class))).thenReturn(null);

        String json = """
                {
                  "flightNumber": "UNKNOWN",
                  "flightDate": "2024-01-01T10:00:00",
                  "durationMinutes": 120,
                  "distanceKm": 2000.0,
                  "isSharedFlight": false
                }
                """;

        mockMvc.perform(put("/api/flights/UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFlight_found_returnsOk() throws Exception {
        Mockito.when(flightService.deleteFlight("TK1234")).thenReturn(true);

        mockMvc.perform(delete("/api/flights/TK1234"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFlight_notFound_returns404() throws Exception {
        Mockito.when(flightService.deleteFlight("UNKNOWN")).thenReturn(false);

        mockMvc.perform(delete("/api/flights/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}

