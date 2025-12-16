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
}


