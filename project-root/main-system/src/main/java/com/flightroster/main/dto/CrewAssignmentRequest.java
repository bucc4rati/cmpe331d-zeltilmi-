package com.flightroster.main.dto;

import lombok.Data;
import java.util.List;

@Data
public class CrewAssignmentRequest {
    private List<Long> pilotIds;
    private List<Long> cabinCrewIds;
    private String assignmentType; // "MANUAL" or "AUTOMATIC"
}




