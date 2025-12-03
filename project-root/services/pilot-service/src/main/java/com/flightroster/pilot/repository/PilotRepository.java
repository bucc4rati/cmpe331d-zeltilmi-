package com.flightroster.pilot.repository;

import com.flightroster.pilot.entity.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
    Optional<Pilot> findByPilotId(String pilotId);
    
    @Query("SELECT p FROM Pilot p WHERE p.vehicleRestriction = :vehicleType AND p.isAvailable = true")
    List<Pilot> findByVehicleRestrictionAndAvailable(@Param("vehicleType") String vehicleType);
    
    @Query("SELECT p FROM Pilot p WHERE p.seniorityLevel = :level AND p.isAvailable = true")
    List<Pilot> findBySeniorityLevelAndAvailable(@Param("level") com.flightroster.pilot.entity.SeniorityLevel level);
    
    @Query("SELECT p FROM Pilot p WHERE p.maxDistanceKm >= :distance AND p.isAvailable = true")
    List<Pilot> findByMaxDistanceGreaterThanEqualAndAvailable(@Param("distance") Double distance);
    
    List<Pilot> findByIsAvailableTrue();
}
