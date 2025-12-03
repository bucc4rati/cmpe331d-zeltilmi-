package com.flightroster.passenger.repository;

import com.flightroster.passenger.entity.Passenger;
import com.flightroster.passenger.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByPassengerId(String passengerId);
    
    List<Passenger> findByFlightId(String flightId);
    
    @Query("SELECT p FROM Passenger p WHERE p.flightId = :flightId AND p.seatType = :seatType")
    List<Passenger> findByFlightIdAndSeatType(@Param("flightId") String flightId, 
                                            @Param("seatType") SeatType seatType);
    
    @Query("SELECT p FROM Passenger p WHERE p.flightId = :flightId AND p.seatNumber = :seatNumber")
    Optional<Passenger> findByFlightIdAndSeatNumber(@Param("flightId") String flightId, 
                                                   @Param("seatNumber") String seatNumber);
    
    @Query("SELECT p FROM Passenger p WHERE p.flightId = :flightId AND p.isInfant = true")
    List<Passenger> findInfantsByFlightId(@Param("flightId") String flightId);
    
    @Query("SELECT p FROM Passenger p WHERE p.flightId = :flightId AND p.seatNumber IS NULL")
    List<Passenger> findUnassignedSeatsByFlightId(@Param("flightId") String flightId);
    
    @Query("SELECT p FROM Passenger p WHERE p.flightId = :flightId AND p.seatNumber IS NOT NULL")
    List<Passenger> findAssignedSeatsByFlightId(@Param("flightId") String flightId);
}




