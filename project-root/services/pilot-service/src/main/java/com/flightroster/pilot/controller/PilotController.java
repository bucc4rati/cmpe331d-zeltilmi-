package com.flightroster.pilot.controller;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.service.PilotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pilots")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class PilotController {

    @Autowired
    private PilotService pilotService;

    @GetMapping
    public ResponseEntity<List<Pilot>> getAllPilots() {
        List<Pilot> pilots = pilotService.getAllPilots();
        return ResponseEntity.ok(pilots);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Pilot>> getAvailablePilots() {
        List<Pilot> pilots = pilotService.getAvailablePilots();
        return ResponseEntity.ok(pilots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        Pilot pilot = pilotService.getPilotById(id);
        if (pilot != null) {
            return ResponseEntity.ok(pilot);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/pilot-id/{pilotId}")
    public ResponseEntity<Pilot> getPilotByPilotId(@PathVariable String pilotId) {
        Pilot pilot = pilotService.getPilotByPilotId(pilotId);
        if (pilot != null) {
            return ResponseEntity.ok(pilot);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<List<Pilot>> getPilotsByVehicleType(@PathVariable String vehicleType) {
        List<Pilot> pilots = pilotService.getPilotsByVehicleType(vehicleType);
        return ResponseEntity.ok(pilots);
    }

    @GetMapping("/seniority/{level}")
    public ResponseEntity<List<Pilot>> getPilotsBySeniorityLevel(@PathVariable String level) {
        List<Pilot> pilots = pilotService.getPilotsBySeniorityLevel(level);
        return ResponseEntity.ok(pilots);
    }

    @GetMapping("/distance/{distance}")
    public ResponseEntity<List<Pilot>> getPilotsByMaxDistance(@PathVariable Double distance) {
        List<Pilot> pilots = pilotService.getPilotsByMaxDistance(distance);
        return ResponseEntity.ok(pilots);
    }

    @PostMapping
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        Pilot createdPilot = pilotService.createPilot(pilot);
        return ResponseEntity.ok(createdPilot);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<Pilot>> getPilotsByIds(@RequestBody List<Long> ids) {
        List<Pilot> pilots = pilotService.getPilotsByIds(ids);
        return ResponseEntity.ok(pilots);
    }

    @PutMapping("/bulk-availability")
    public ResponseEntity<List<Pilot>> updateBulkAvailability(@RequestBody List<Long> ids, @RequestParam Boolean isAvailable) {
        List<Pilot> pilots = pilotService.updateBulkAvailability(ids, isAvailable);
        return ResponseEntity.ok(pilots);
    }

    @PutMapping("/all/availability")
    public ResponseEntity<List<Pilot>> setAllPilotsAvailable(@RequestParam Boolean isAvailable) {
        List<Pilot> pilots = pilotService.setAllPilotsAvailable(isAvailable);
        return ResponseEntity.ok(pilots);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long id, @RequestBody Pilot pilot) {
        Pilot updatedPilot = pilotService.updatePilot(id, pilot);
        if (updatedPilot != null) {
            return ResponseEntity.ok(updatedPilot);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Pilot> updateAvailability(@PathVariable Long id, @RequestParam Boolean isAvailable) {
        Pilot pilot = pilotService.updateAvailability(id, isAvailable);
        if (pilot != null) {
            return ResponseEntity.ok(pilot);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePilot(@PathVariable Long id) {
        boolean deleted = pilotService.deletePilot(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
