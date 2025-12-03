package com.flightroster.main.repository;

import com.flightroster.main.entity.FlightRoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRosterRepository extends JpaRepository<FlightRoster, Long> {
    Optional<FlightRoster> findByFlightNumber(String flightNumber);
    
    List<FlightRoster> findByIsActiveTrue();
    
    @Query("SELECT fr FROM FlightRoster fr WHERE fr.databaseType = :databaseType AND fr.isActive = true")
    List<FlightRoster> findByDatabaseTypeAndActive(@Param("databaseType") String databaseType);
    
    @Query("SELECT fr FROM FlightRoster fr WHERE fr.rosterName LIKE %:name% AND fr.isActive = true")
    List<FlightRoster> findByRosterNameContainingAndActive(@Param("name") String name);
}




