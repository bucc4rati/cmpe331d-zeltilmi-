package com.flightroster.cabin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightroster.cabin.entity.Attendant;
import com.flightroster.cabin.entity.AttendantType;
import com.flightroster.cabin.service.AttendantService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendantController.class)
class AttendantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendantService attendantService;

    private Attendant sampleAttendant() {
        return new Attendant(
                1L,
                "ATT-001",
                "John Doe",
                30,
                "Male",
                "TR",
                "[\"EN\",\"TR\"]",
                AttendantType.REGULAR,
                "[]",
                true,
                null
        );
    }

    @Test
    void getAllAttendants_returnsOkWithList() throws Exception {
        when(attendantService.getAllAttendants()).thenReturn(List.of(sampleAttendant()));

        mockMvc.perform(get("/api/attendants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantId").value("ATT-001"))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(attendantService).getAllAttendants();
    }

    @Test
    void getAttendantById_found_returnsOk() throws Exception {
        when(attendantService.getAttendantById(1L)).thenReturn(sampleAttendant());

        mockMvc.perform(get("/api/attendants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendantId").value("ATT-001"));

        verify(attendantService).getAttendantById(1L);
    }

    @Test
    void getAttendantById_notFound_returns404() throws Exception {
        when(attendantService.getAttendantById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/attendants/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAttendant_returnsCreatedEntity() throws Exception {
        Attendant toCreate = sampleAttendant();
        toCreate.setId(null);
        Attendant created = sampleAttendant();

        when(attendantService.createAttendant(toCreate)).thenReturn(created);

        mockMvc.perform(post("/api/attendants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendantId").value("ATT-001"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void deleteAttendant_found_returnsOk() throws Exception {
        when(attendantService.deleteAttendant(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/attendants/1"))
                .andExpect(status().isOk());

        verify(attendantService).deleteAttendant(1L);
    }

    @Test
    void deleteAttendant_notFound_returns404() throws Exception {
        when(attendantService.deleteAttendant(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/attendants/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvailableAttendants_returnsOkWithList() throws Exception {
        when(attendantService.getAvailableAttendants()).thenReturn(List.of(sampleAttendant()));

        mockMvc.perform(get("/api/attendants/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantId").value("ATT-001"))
                .andExpect(jsonPath("$[0].isAvailable").value(true));

        verify(attendantService).getAvailableAttendants();
    }

    @Test
    void getAttendantByAttendantId_found_returnsOk() throws Exception {
        when(attendantService.getAttendantByAttendantId("ATT-001")).thenReturn(sampleAttendant());

        mockMvc.perform(get("/api/attendants/attendant-id/ATT-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendantId").value("ATT-001"));

        verify(attendantService).getAttendantByAttendantId("ATT-001");
    }

    @Test
    void getAttendantByAttendantId_notFound_returns404() throws Exception {
        when(attendantService.getAttendantByAttendantId("UNKNOWN")).thenReturn(null);

        mockMvc.perform(get("/api/attendants/attendant-id/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAttendantsByType_returnsOkWithList() throws Exception {
        when(attendantService.getAttendantsByType("regular")).thenReturn(List.of(sampleAttendant()));

        mockMvc.perform(get("/api/attendants/type/regular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantType").value("REGULAR"));

        verify(attendantService).getAttendantsByType("regular");
    }

    @Test
    void getAvailableChefs_returnsOkWithList() throws Exception {
        Attendant chef = sampleAttendant();
        chef.setAttendantType(AttendantType.CHEF);
        when(attendantService.getAvailableChefs()).thenReturn(List.of(chef));

        mockMvc.perform(get("/api/attendants/chefs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantType").value("CHEF"));

        verify(attendantService).getAvailableChefs();
    }

    @Test
    void getAvailableChiefs_returnsOkWithList() throws Exception {
        Attendant chief = sampleAttendant();
        chief.setAttendantType(AttendantType.CHIEF);
        when(attendantService.getAvailableChiefs()).thenReturn(List.of(chief));

        mockMvc.perform(get("/api/attendants/chiefs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantType").value("CHIEF"));

        verify(attendantService).getAvailableChiefs();
    }

    @Test
    void getAvailableRegulars_returnsOkWithList() throws Exception {
        when(attendantService.getAvailableRegulars()).thenReturn(List.of(sampleAttendant()));

        mockMvc.perform(get("/api/attendants/regulars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantType").value("REGULAR"));

        verify(attendantService).getAvailableRegulars();
    }

    @Test
    void updateAttendant_found_returnsOk() throws Exception {
        Attendant updated = sampleAttendant();
        updated.setName("Jane Doe");
        updated.setAge(28);

        when(attendantService.updateAttendant(1L, updated)).thenReturn(updated);

        mockMvc.perform(put("/api/attendants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.age").value(28));

        verify(attendantService).updateAttendant(1L, updated);
    }

    @Test
    void updateAttendant_notFound_returns404() throws Exception {
        Attendant toUpdate = sampleAttendant();
        when(attendantService.updateAttendant(99L, toUpdate)).thenReturn(null);

        mockMvc.perform(put("/api/attendants/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAttendantsByIds_returnsOkWithList() throws Exception {
        when(attendantService.getAttendantsByIds(List.of(1L, 2L))).thenReturn(List.of(sampleAttendant()));

        mockMvc.perform(post("/api/attendants/by-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendantId").value("ATT-001"));

        verify(attendantService).getAttendantsByIds(List.of(1L, 2L));
    }

    @Test
    void updateBulkAvailability_returnsOkWithList() throws Exception {
        Attendant updated = sampleAttendant();
        updated.setIsAvailable(false);
        when(attendantService.updateBulkAvailability(List.of(1L, 2L), false))
                .thenReturn(List.of(updated));

        mockMvc.perform(put("/api/attendants/bulk-availability?isAvailable=false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isAvailable").value(false));

        verify(attendantService).updateBulkAvailability(List.of(1L, 2L), false);
    }
}


