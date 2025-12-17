package com.flightroster.pilot.blackbox.controller;

import com.flightroster.pilot.controller.PilotController;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.entity.SeniorityLevel;
import com.flightroster.pilot.service.PilotService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PilotController.class)
@ActiveProfiles("test")
class PilotControllerTest {

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
    void getAllPilots_returnsOk() throws Exception {
        Mockito.when(pilotService.getAllPilots()).thenReturn(List.of(buildSamplePilot()));

        mockMvc.perform(get("/api/pilots"))
                .andExpect(status().isOk());
    }

    @Test
    void getAvailablePilots_returnsOk() throws Exception {
        Mockito.when(pilotService.getAvailablePilots()).thenReturn(List.of(buildSamplePilot()));

        mockMvc.perform(get("/api/pilots/available"))
                .andExpect(status().isOk());
    }

    @Test
    void getPilotById_found_returnsOk() throws Exception {
        Mockito.when(pilotService.getPilotById(1L)).thenReturn(buildSamplePilot());

        mockMvc.perform(get("/api/pilots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pilotId").value("P123"));
    }

    @Test
    void getPilotById_notFound_returns404() throws Exception {
        Mockito.when(pilotService.getPilotById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/pilots/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPilotByPilotId_found_returnsOk() throws Exception {
        Mockito.when(pilotService.getPilotByPilotId("P123")).thenReturn(buildSamplePilot());

        mockMvc.perform(get("/api/pilots/pilot-id/P123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pilotId").value("P123"));
    }

    @Test
    void getPilotByPilotId_notFound_returns404() throws Exception {
        Mockito.when(pilotService.getPilotByPilotId(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/pilots/pilot-id/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPilotsByVehicleType_returnsOk() throws Exception {
        Mockito.when(pilotService.getPilotsByVehicleType("A320")).thenReturn(List.of(buildSamplePilot()));

        mockMvc.perform(get("/api/pilots/vehicle-type/A320"))
                .andExpect(status().isOk());
    }

    @Test
    void getPilotsBySeniorityLevel_returnsOk() throws Exception {
        Mockito.when(pilotService.getPilotsBySeniorityLevel("SENIOR")).thenReturn(List.of(buildSamplePilot()));

        mockMvc.perform(get("/api/pilots/seniority/SENIOR"))
                .andExpect(status().isOk());
    }

    @Test
    void getPilotsByMaxDistance_returnsOk() throws Exception {
        Mockito.when(pilotService.getPilotsByMaxDistance(anyDouble())).thenReturn(List.of(buildSamplePilot()));

        mockMvc.perform(get("/api/pilots/distance/1500"))
                .andExpect(status().isOk());
    }

    @Test
    void createPilot_returnsOk() throws Exception {
        Mockito.when(pilotService.createPilot(any(Pilot.class))).thenReturn(buildSamplePilot());

        String json = """
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
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pilotId").value("P123"));
    }

    @Test
    void getPilotsByIds_returnsOk() throws Exception {
        Mockito.when(pilotService.getPilotsByIds(List.of(1L))).thenReturn(List.of(buildSamplePilot()));

        String json = "[1]";

        mockMvc.perform(post("/api/pilots/by-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateBulkAvailability_returnsOk() throws Exception {
        Mockito.when(pilotService.updateBulkAvailability(any(), anyBoolean()))
                .thenReturn(List.of(buildSamplePilot()));

        String json = "[1,2]";

        mockMvc.perform(put("/api/pilots/bulk-availability")
                        .param("isAvailable", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void setAllPilotsAvailable_returnsOk() throws Exception {
        Mockito.when(pilotService.setAllPilotsAvailable(true)).thenReturn(List.of(buildSamplePilot()));

        mockMvc.perform(put("/api/pilots/all/availability")
                        .param("isAvailable", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePilot_found_returnsOk() throws Exception {
        Mockito.when(pilotService.updatePilot(anyLong(), any(Pilot.class)))
                .thenReturn(buildSamplePilot());

        String json = """
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

        mockMvc.perform(put("/api/pilots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updatePilot_notFound_returns404() throws Exception {
        Mockito.when(pilotService.updatePilot(anyLong(), any(Pilot.class))).thenReturn(null);

        String json = """
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

        mockMvc.perform(put("/api/pilots/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAvailability_found_returnsOk() throws Exception {
        Mockito.when(pilotService.updateAvailability(anyLong(), anyBoolean()))
                .thenReturn(buildSamplePilot());

        mockMvc.perform(put("/api/pilots/1/availability")
                        .param("isAvailable", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void updateAvailability_notFound_returns404() throws Exception {
        Mockito.when(pilotService.updateAvailability(anyLong(), anyBoolean()))
                .thenReturn(null);

        mockMvc.perform(put("/api/pilots/99/availability")
                        .param("isAvailable", "false"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePilot_found_returnsOk() throws Exception {
        Mockito.when(pilotService.deletePilot(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/pilots/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePilot_notFound_returns404() throws Exception {
        Mockito.when(pilotService.deletePilot(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/pilots/99"))
                .andExpect(status().isNotFound());
    }
}


