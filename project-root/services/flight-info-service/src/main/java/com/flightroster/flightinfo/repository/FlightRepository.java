package com.flightroster.flightinfo.repository;

import com.flightroster.flightinfo.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, String> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    @Query("SELECT f FROM Flight f WHERE f.flightDate BETWEEN :startDate AND :endDate")
    List<Flight> findByFlightDateBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT f FROM Flight f WHERE f.sourceAirport.airportCode = :airportCode OR f.destinationAirport.airportCode = :airportCode")
    List<Flight> findByAirportCode(@Param("airportCode") String airportCode);
    
    @Query("SELECT f FROM Flight f WHERE f.vehicleType.typeName = :vehicleType")
    List<Flight> findByVehicleType(@Param("vehicleType") String vehicleType);
}




