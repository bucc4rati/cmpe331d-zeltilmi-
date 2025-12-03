package com.flightroster.passenger.service;

import com.flightroster.passenger.entity.Passenger;
import com.flightroster.passenger.entity.SeatType;
import com.flightroster.passenger.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    public List<Passenger> getPassengersByFlightId(String flightId) {
        return passengerRepository.findByFlightId(flightId);
    }

    public Passenger getPassengerById(Long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        return passenger.orElse(null);
    }

    public Passenger getPassengerByPassengerId(String passengerId) {
        Optional<Passenger> passenger = passengerRepository.findByPassengerId(passengerId);
        return passenger.orElse(null);
    }

    public List<Passenger> getPassengersByFlightAndSeatType(String flightId, String seatType) {
        try {
            SeatType type = SeatType.valueOf(seatType.toUpperCase());
            return passengerRepository.findByFlightIdAndSeatType(flightId, type);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list for invalid seat type
        }
    }

    public List<Passenger> getInfantsByFlightId(String flightId) {
        return passengerRepository.findInfantsByFlightId(flightId);
    }

    public List<Passenger> getUnassignedSeatsByFlightId(String flightId) {
        return passengerRepository.findUnassignedSeatsByFlightId(flightId);
    }

    public List<Passenger> getAssignedSeatsByFlightId(String flightId) {
        return passengerRepository.findAssignedSeatsByFlightId(flightId);
    }

    public Passenger createPassenger(Passenger passenger) {
        return passengerRepository.save(passenger);
    }

    public Passenger updatePassenger(Long id, Passenger passengerDetails) {
        Optional<Passenger> optionalPassenger = passengerRepository.findById(id);
        if (optionalPassenger.isPresent()) {
            Passenger passenger = optionalPassenger.get();
            passenger.setPassengerId(passengerDetails.getPassengerId());
            passenger.setFlightId(passengerDetails.getFlightId());
            passenger.setName(passengerDetails.getName());
            passenger.setAge(passengerDetails.getAge());
            passenger.setGender(passengerDetails.getGender());
            passenger.setNationality(passengerDetails.getNationality());
            passenger.setSeatType(passengerDetails.getSeatType());
            passenger.setSeatNumber(passengerDetails.getSeatNumber());
            passenger.setIsInfant(passengerDetails.getIsInfant());
            passenger.setParentPassengerId(passengerDetails.getParentPassengerId());
            passenger.setAffiliatedPassengerIds(passengerDetails.getAffiliatedPassengerIds());
            
            return passengerRepository.save(passenger);
        }
        return null;
    }

    public boolean deletePassenger(Long id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            passengerRepository.delete(passenger.get());
            return true;
        }
        return false;
    }

    public Passenger assignSeat(String passengerId, String seatNumber) {
        Optional<Passenger> optionalPassenger = passengerRepository.findByPassengerId(passengerId);
        if (optionalPassenger.isPresent()) {
            Passenger passenger = optionalPassenger.get();
            passenger.setSeatNumber(seatNumber);
            return passengerRepository.save(passenger);
        }
        return null;
    }
}




