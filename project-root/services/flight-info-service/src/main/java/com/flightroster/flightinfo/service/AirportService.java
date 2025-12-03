package com.flightroster.flightinfo.service;

import com.flightroster.flightinfo.entity.Airport;
import com.flightroster.flightinfo.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport getAirportById(Long id) {
        Optional<Airport> airport = airportRepository.findById(id);
        return airport.orElse(null);
    }

    public Airport getAirportByCode(String airportCode) {
        Optional<Airport> airport = airportRepository.findByAirportCode(airportCode);
        return airport.orElse(null);
    }

    public Airport createAirport(Airport airport) {
        return airportRepository.save(airport);
    }
}




