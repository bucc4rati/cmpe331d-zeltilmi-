package com.flightroster.main.repository;

import com.flightroster.main.entity.RosterPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RosterPersonRepository extends JpaRepository<RosterPerson, Long> {
    List<RosterPerson> findByFlightRosterId(Long flightRosterId);
    
    @Query("SELECT rp FROM RosterPerson rp WHERE rp.flightRoster.id = :rosterId AND rp.personType = :personType")
    List<RosterPerson> findByFlightRosterIdAndPersonType(@Param("rosterId") Long rosterId, 
                                                        @Param("personType") String personType);
    
    @Query("SELECT rp FROM RosterPerson rp WHERE rp.flightRoster.flightNumber = :flightNumber")
    List<RosterPerson> findByFlightNumber(@Param("flightNumber") String flightNumber);
}




