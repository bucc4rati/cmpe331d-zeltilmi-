package com.flightroster.cabin.repository;

import com.flightroster.cabin.entity.Attendant;
import com.flightroster.cabin.entity.AttendantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendantRepository extends JpaRepository<Attendant, Long> {
    Optional<Attendant> findByAttendantId(String attendantId);
    
    @Query("SELECT a FROM Attendant a WHERE a.attendantType = :type AND a.isAvailable = true")
    List<Attendant> findByAttendantTypeAndAvailable(@Param("type") AttendantType type);
    
    @Query("SELECT a FROM Attendant a WHERE a.isAvailable = true")
    List<Attendant> findByIsAvailableTrue();
    
    @Query("SELECT a FROM Attendant a WHERE a.attendantType = 'CHEF' AND a.isAvailable = true")
    List<Attendant> findAvailableChefs();
    
    @Query("SELECT a FROM Attendant a WHERE a.attendantType = 'CHIEF' AND a.isAvailable = true")
    List<Attendant> findAvailableChiefs();
    
    @Query("SELECT a FROM Attendant a WHERE a.attendantType = 'REGULAR' AND a.isAvailable = true")
    List<Attendant> findAvailableRegulars();
}




