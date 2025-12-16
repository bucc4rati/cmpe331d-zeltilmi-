package com.flightroster.passenger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightroster.passenger.entity.Passenger;
import com.flightroster.passenger.entity.SeatType;
import com.flightroster.passenger.service.PassengerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private Passenger samplePassenger() {
        return new Passenger(
                1L,
                "PAX-001",
                "FL1234",
                "Alice",
                25,
                "Female",
                "TR",
                SeatType.ECONOMY,
                null,
                false,
                null,
                null
        );
    }

    @Test
    void getAllPassengers_returnsOkWithList() throws Exception {
        when(passengerService.getAllPassengers()).thenReturn(List.of(samplePassenger()));

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passengerId").value("PAX-001"))
                .andExpect(jsonPath("$[0].name").value("Alice"));

        verify(passengerService).getAllPassengers();
    }

    @Test
    void getPassengerById_found_returnsOk() throws Exception {
        when(passengerService.getPassengerById(1L)).thenReturn(samplePassenger());

        mockMvc.perform(get("/api/passengers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("PAX-001"));

        verify(passengerService).getPassengerById(1L);
    }

    @Test
    void getPassengerById_notFound_returns404() throws Exception {
        when(passengerService.getPassengerById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/passengers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPassenger_returnsCreatedEntity() throws Exception {
        Passenger toCreate = samplePassenger();
        toCreate.setId(null);
        Passenger created = samplePassenger();

        when(passengerService.createPassenger(toCreate)).thenReturn(created);

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("PAX-001"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void assignSeat_found_returnsOk() throws Exception {
        Passenger updated = samplePassenger();
        updated.setSeatNumber("12C");

        when(passengerService.assignSeat("PAX-001", "12C")).thenReturn(updated);

        mockMvc.perform(put("/api/passengers/PAX-001/assign-seat/12C"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value("12C"));

        verify(passengerService).assignSeat("PAX-001", "12C");
    }

    @Test
    void assignSeat_notFound_returns404() throws Exception {
        when(passengerService.assignSeat(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(put("/api/passengers/UNKNOWN/assign-seat/12C"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePassenger_found_returnsOk() throws Exception {
        when(passengerService.deletePassenger(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/passengers/1"))
                .andExpect(status().isOk());

        verify(passengerService).deletePassenger(1L);
    }

    @Test
    void deletePassenger_notFound_returns404() throws Exception {
        when(passengerService.deletePassenger(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/passengers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPassengersByFlightId_returnsOkWithList() throws Exception {
        when(passengerService.getPassengersByFlightId("FL1234")).thenReturn(List.of(samplePassenger()));

        mockMvc.perform(get("/api/passengers/flight/FL1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passengerId").value("PAX-001"))
                .andExpect(jsonPath("$[0].flightId").value("FL1234"));

        verify(passengerService).getPassengersByFlightId("FL1234");
    }

    @Test
    void getPassengerByPassengerId_found_returnsOk() throws Exception {
        when(passengerService.getPassengerByPassengerId("PAX-001")).thenReturn(samplePassenger());

        mockMvc.perform(get("/api/passengers/passenger-id/PAX-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value("PAX-001"));

        verify(passengerService).getPassengerByPassengerId("PAX-001");
    }

    @Test
    void getPassengerByPassengerId_notFound_returns404() throws Exception {
        when(passengerService.getPassengerByPassengerId("UNKNOWN")).thenReturn(null);

        mockMvc.perform(get("/api/passengers/passenger-id/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPassengersByFlightAndSeatType_returnsOkWithList() throws Exception {
        when(passengerService.getPassengersByFlightAndSeatType("FL1234", "economy"))
                .thenReturn(List.of(samplePassenger()));

        mockMvc.perform(get("/api/passengers/flight/FL1234/seat-type/economy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passengerId").value("PAX-001"))
                .andExpect(jsonPath("$[0].seatType").value("ECONOMY"));

        verify(passengerService).getPassengersByFlightAndSeatType("FL1234", "economy");
    }

    @Test
    void getInfantsByFlightId_returnsOkWithList() throws Exception {
        Passenger infant = samplePassenger();
        infant.setIsInfant(true);
        when(passengerService.getInfantsByFlightId("FL1234")).thenReturn(List.of(infant));

        mockMvc.perform(get("/api/passengers/flight/FL1234/infants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isInfant").value(true));

        verify(passengerService).getInfantsByFlightId("FL1234");
    }

    @Test
    void getUnassignedSeatsByFlightId_returnsOkWithList() throws Exception {
        Passenger unassigned = samplePassenger();
        unassigned.setSeatNumber(null);
        when(passengerService.getUnassignedSeatsByFlightId("FL1234")).thenReturn(List.of(unassigned));

        mockMvc.perform(get("/api/passengers/flight/FL1234/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").doesNotExist());

        verify(passengerService).getUnassignedSeatsByFlightId("FL1234");
    }

    @Test
    void getAssignedSeatsByFlightId_returnsOkWithList() throws Exception {
        Passenger assigned = samplePassenger();
        assigned.setSeatNumber("12A");
        when(passengerService.getAssignedSeatsByFlightId("FL1234")).thenReturn(List.of(assigned));

        mockMvc.perform(get("/api/passengers/flight/FL1234/assigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value("12A"));

        verify(passengerService).getAssignedSeatsByFlightId("FL1234");
    }

    @Test
    void updatePassenger_found_returnsOk() throws Exception {
        Passenger updated = samplePassenger();
        updated.setName("Bob");
        updated.setAge(30);

        when(passengerService.updatePassenger(1L, updated)).thenReturn(updated);

        mockMvc.perform(put("/api/passengers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.age").value(30));

        verify(passengerService).updatePassenger(1L, updated);
    }

    @Test
    void updatePassenger_notFound_returns404() throws Exception {
        Passenger toUpdate = samplePassenger();
        when(passengerService.updatePassenger(99L, toUpdate)).thenReturn(null);

        mockMvc.perform(put("/api/passengers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().isNotFound());
    }
}


