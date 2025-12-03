package com.flightroster.flightinfo.controller;

import com.flightroster.flightinfo.entity.Airport;
import com.flightroster.flightinfo.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        List<Airport> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airport> getAirportById(@PathVariable Long id) {
        Airport airport = airportService.getAirportById(id);
        if (airport != null) {
            return ResponseEntity.ok(airport);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/code/{airportCode}")
    public ResponseEntity<Airport> getAirportByCode(@PathVariable String airportCode) {
        Airport airport = airportService.getAirportByCode(airportCode);
        if (airport != null) {
            return ResponseEntity.ok(airport);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Airport> createAirport(@RequestBody Airport airport) {
        Airport createdAirport = airportService.createAirport(airport);
        return ResponseEntity.ok(createdAirport);
    }
}




