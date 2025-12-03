package com.flightroster.passenger.controller;

import com.flightroster.passenger.entity.Passenger;
import com.flightroster.passenger.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @GetMapping
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        List<Passenger> passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Passenger>> getPassengersByFlightId(@PathVariable String flightId) {
        List<Passenger> passengers = passengerService.getPassengersByFlightId(flightId);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        Passenger passenger = passengerService.getPassengerById(id);
        if (passenger != null) {
            return ResponseEntity.ok(passenger);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/passenger-id/{passengerId}")
    public ResponseEntity<Passenger> getPassengerByPassengerId(@PathVariable String passengerId) {
        Passenger passenger = passengerService.getPassengerByPassengerId(passengerId);
        if (passenger != null) {
            return ResponseEntity.ok(passenger);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/flight/{flightId}/seat-type/{seatType}")
    public ResponseEntity<List<Passenger>> getPassengersByFlightAndSeatType(
            @PathVariable String flightId, @PathVariable String seatType) {
        List<Passenger> passengers = passengerService.getPassengersByFlightAndSeatType(flightId, seatType);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/flight/{flightId}/infants")
    public ResponseEntity<List<Passenger>> getInfantsByFlightId(@PathVariable String flightId) {
        List<Passenger> infants = passengerService.getInfantsByFlightId(flightId);
        return ResponseEntity.ok(infants);
    }

    @GetMapping("/flight/{flightId}/unassigned")
    public ResponseEntity<List<Passenger>> getUnassignedSeatsByFlightId(@PathVariable String flightId) {
        List<Passenger> passengers = passengerService.getUnassignedSeatsByFlightId(flightId);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/flight/{flightId}/assigned")
    public ResponseEntity<List<Passenger>> getAssignedSeatsByFlightId(@PathVariable String flightId) {
        List<Passenger> passengers = passengerService.getAssignedSeatsByFlightId(flightId);
        return ResponseEntity.ok(passengers);
    }

    @PostMapping
    public ResponseEntity<Passenger> createPassenger(@RequestBody Passenger passenger) {
        Passenger createdPassenger = passengerService.createPassenger(passenger);
        return ResponseEntity.ok(createdPassenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(@PathVariable Long id, @RequestBody Passenger passenger) {
        Passenger updatedPassenger = passengerService.updatePassenger(id, passenger);
        if (updatedPassenger != null) {
            return ResponseEntity.ok(updatedPassenger);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{passengerId}/assign-seat/{seatNumber}")
    public ResponseEntity<Passenger> assignSeat(@PathVariable String passengerId, @PathVariable String seatNumber) {
        Passenger passenger = passengerService.assignSeat(passengerId, seatNumber);
        if (passenger != null) {
            return ResponseEntity.ok(passenger);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        boolean deleted = passengerService.deletePassenger(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}




