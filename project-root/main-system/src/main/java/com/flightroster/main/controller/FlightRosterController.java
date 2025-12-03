package com.flightroster.main.controller;

import com.flightroster.main.dto.CrewAssignmentRequest;
import com.flightroster.main.entity.FlightRoster;
import com.flightroster.main.service.FlightRosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rosters")
@CrossOrigin(origins = {"http://localhost:3000"})
public class FlightRosterController {

    @Autowired
    private FlightRosterService flightRosterService;

    @GetMapping
    public ResponseEntity<List<FlightRoster>> getAllRosters() {
        List<FlightRoster> rosters = flightRosterService.getAllRosters();
        return ResponseEntity.ok(rosters);
    }

    @GetMapping("/new")
    public ResponseEntity<?> createRoster(@RequestParam String flightNumber, 
                                                   @RequestParam String databaseType) {
        try {
            FlightRoster roster = flightRosterService.createRoster(flightNumber, databaseType);
            return ResponseEntity.ok(roster);
        } catch (Exception e) {
            // Log the full exception for debugging
            System.err.println("Exception creating roster for flight " + flightNumber + ":");
            e.printStackTrace();
            
            String errorMessage = e.getMessage();
            if (errorMessage == null && e.getCause() != null) {
                errorMessage = e.getCause().getMessage();
            }
            if (errorMessage == null) {
                errorMessage = "Unknown error: " + e.getClass().getSimpleName();
            }
            
            // Return as JSON object for better frontend parsing
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating roster: " + errorMessage);
            errorResponse.put("error", errorMessage);
            System.err.println("Returning error response: " + errorResponse);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Spesifik route'lar genel route'lardan önce olmalı
    @GetMapping("/flight/{flightNumber}")
    public ResponseEntity<FlightRoster> getRosterByFlightNumber(@PathVariable String flightNumber) {
        FlightRoster roster = flightRosterService.getRosterByFlightNumber(flightNumber);
        if (roster != null) {
            return ResponseEntity.ok(roster);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/flights/{flightNumber}")
    public ResponseEntity<?> getFlightByNumber(@PathVariable String flightNumber) {
        try {
            return ResponseEntity.ok(flightRosterService.getFlightByNumber(flightNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching flight: " + e.getMessage());
        }
    }

    @GetMapping("/flights")
    public ResponseEntity<?> getAllFlights() {
        try {
            return ResponseEntity.ok(flightRosterService.getAllFlights());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching flights: " + e.getMessage());
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<FlightRoster> getRosterById(@PathVariable Long id) {
        FlightRoster roster = flightRosterService.getRosterById(id);
        if (roster != null) {
            return ResponseEntity.ok(roster);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteRoster(@PathVariable Long id) {
        boolean deleted = flightRosterService.deleteRoster(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id:\\d+}/export")
    public ResponseEntity<String> exportRosterAsJson(@PathVariable Long id) {
        String jsonData = flightRosterService.exportRosterAsJson(id);
        if (jsonData != null) {
            return ResponseEntity.ok(jsonData);
        }
        return ResponseEntity.notFound().build();
    }

    // Crew Selection APIs
    @GetMapping("/available-pilots")
    public ResponseEntity<?> getAvailablePilots() {
        try {
            return ResponseEntity.ok(flightRosterService.getAvailablePilots());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching pilots: " + e.getMessage());
        }
    }

    @GetMapping("/available-cabin-crew")
    public ResponseEntity<?> getAvailableCabinCrew() {
        try {
            return ResponseEntity.ok(flightRosterService.getAvailableCabinCrew());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching cabin crew: " + e.getMessage());
        }
    }

    @PostMapping("/{id:\\d+}/assign-crew")
    public ResponseEntity<?> assignCrewManually(@PathVariable Long id, 
                                               @RequestBody CrewAssignmentRequest request) {
        try {
            // Check user role - only MANAGER and ADMIN can assign crew
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                // For now, allow all authenticated users
                // TODO: Implement proper role-based access control
            }
            
            FlightRoster roster = flightRosterService.assignCrewManually(id, request);
            return ResponseEntity.ok(roster);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning crew: " + e.getMessage());
        }
    }

}