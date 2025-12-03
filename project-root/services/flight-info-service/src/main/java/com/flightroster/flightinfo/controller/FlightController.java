package com.flightroster.flightinfo.controller;

import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        Flight flight = flightService.getFlightByNumber(flightNumber);
        if (flight != null) {
            return ResponseEntity.ok(flight);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(required = false) String sourceAirport,
            @RequestParam(required = false) String destinationAirport,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        List<Flight> flights = flightService.searchFlights(sourceAirport, destinationAirport, 
                                                          vehicleType, startDate, endDate);
        return ResponseEntity.ok(flights);
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        Flight createdFlight = flightService.createFlight(flight);
        return ResponseEntity.ok(createdFlight);
    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<Flight> updateFlight(@PathVariable String flightNumber, 
                                             @RequestBody Flight flight) {
        Flight updatedFlight = flightService.updateFlight(flightNumber, flight);
        if (updatedFlight != null) {
            return ResponseEntity.ok(updatedFlight);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        boolean deleted = flightService.deleteFlight(flightNumber);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}




