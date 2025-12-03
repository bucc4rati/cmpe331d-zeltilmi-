package com.flightroster.cabin.controller;

import com.flightroster.cabin.entity.Attendant;
import com.flightroster.cabin.service.AttendantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendants")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AttendantController {

    @Autowired
    private AttendantService attendantService;

    @GetMapping
    public ResponseEntity<List<Attendant>> getAllAttendants() {
        List<Attendant> attendants = attendantService.getAllAttendants();
        return ResponseEntity.ok(attendants);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Attendant>> getAvailableAttendants() {
        List<Attendant> attendants = attendantService.getAvailableAttendants();
        return ResponseEntity.ok(attendants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendant> getAttendantById(@PathVariable Long id) {
        Attendant attendant = attendantService.getAttendantById(id);
        if (attendant != null) {
            return ResponseEntity.ok(attendant);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/attendant-id/{attendantId}")
    public ResponseEntity<Attendant> getAttendantByAttendantId(@PathVariable String attendantId) {
        Attendant attendant = attendantService.getAttendantByAttendantId(attendantId);
        if (attendant != null) {
            return ResponseEntity.ok(attendant);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Attendant>> getAttendantsByType(@PathVariable String type) {
        List<Attendant> attendants = attendantService.getAttendantsByType(type);
        return ResponseEntity.ok(attendants);
    }

    @GetMapping("/chefs")
    public ResponseEntity<List<Attendant>> getAvailableChefs() {
        List<Attendant> chefs = attendantService.getAvailableChefs();
        return ResponseEntity.ok(chefs);
    }

    @GetMapping("/chiefs")
    public ResponseEntity<List<Attendant>> getAvailableChiefs() {
        List<Attendant> chiefs = attendantService.getAvailableChiefs();
        return ResponseEntity.ok(chiefs);
    }

    @GetMapping("/regulars")
    public ResponseEntity<List<Attendant>> getAvailableRegulars() {
        List<Attendant> regulars = attendantService.getAvailableRegulars();
        return ResponseEntity.ok(regulars);
    }

    @PostMapping
    public ResponseEntity<Attendant> createAttendant(@RequestBody Attendant attendant) {
        Attendant createdAttendant = attendantService.createAttendant(attendant);
        return ResponseEntity.ok(createdAttendant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendant> updateAttendant(@PathVariable Long id, @RequestBody Attendant attendant) {
        Attendant updatedAttendant = attendantService.updateAttendant(id, attendant);
        if (updatedAttendant != null) {
            return ResponseEntity.ok(updatedAttendant);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendant(@PathVariable Long id) {
        boolean deleted = attendantService.deleteAttendant(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<Attendant>> getAttendantsByIds(@RequestBody List<Long> ids) {
        List<Attendant> attendants = attendantService.getAttendantsByIds(ids);
        return ResponseEntity.ok(attendants);
    }

    @PutMapping("/bulk-availability")
    public ResponseEntity<List<Attendant>> updateBulkAvailability(@RequestBody List<Long> ids, @RequestParam Boolean isAvailable) {
        List<Attendant> attendants = attendantService.updateBulkAvailability(ids, isAvailable);
        return ResponseEntity.ok(attendants);
    }
}
