package com.flightroster.flightinfo.service;

import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightByNumber(String flightNumber) {
        Optional<Flight> flight = flightRepository.findByFlightNumber(flightNumber);
        return flight.orElse(null);
    }

    public List<Flight> searchFlights(String sourceAirport, String destinationAirport, 
                                    String vehicleType, String startDate, String endDate) {
        if (sourceAirport != null && !sourceAirport.isEmpty()) {
            return flightRepository.findByAirportCode(sourceAirport);
        }
        
        if (vehicleType != null && !vehicleType.isEmpty()) {
            return flightRepository.findByVehicleType(vehicleType);
        }
        
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            return flightRepository.findByFlightDateBetween(start, end);
        }
        
        return flightRepository.findAll();
    }

    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public Flight updateFlight(String flightNumber, Flight flightDetails) {
        Optional<Flight> optionalFlight = flightRepository.findByFlightNumber(flightNumber);
        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();
            flight.setFlightDate(flightDetails.getFlightDate());
            flight.setDurationMinutes(flightDetails.getDurationMinutes());
            flight.setDistanceKm(flightDetails.getDistanceKm());
            flight.setSourceAirport(flightDetails.getSourceAirport());
            flight.setDestinationAirport(flightDetails.getDestinationAirport());
            flight.setVehicleType(flightDetails.getVehicleType());
            flight.setIsSharedFlight(flightDetails.getIsSharedFlight());
            flight.setSharedFlightNumber(flightDetails.getSharedFlightNumber());
            flight.setSharedAirlineName(flightDetails.getSharedAirlineName());
            flight.setConnectingFlightNumber(flightDetails.getConnectingFlightNumber());
            
            return flightRepository.save(flight);
        }
        return null;
    }

    public boolean deleteFlight(String flightNumber) {
        Optional<Flight> flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight.isPresent()) {
            flightRepository.delete(flight.get());
            return true;
        }
        return false;
    }
}




