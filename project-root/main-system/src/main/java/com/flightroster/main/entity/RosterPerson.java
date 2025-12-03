package com.flightroster.main.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roster_persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RosterPerson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "person_id", nullable = false)
    private String personId;
    
    @Column(name = "person_type", nullable = false)
    private String personType; // PILOT, CABIN_CREW, PASSENGER
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "age", nullable = false)
    private Integer age;
    
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @Column(name = "nationality", nullable = false)
    private String nationality;
    
    @Column(name = "seat_number")
    private String seatNumber;
    
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo; // JSON string for type-specific information
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_roster_id", nullable = false)
    private FlightRoster flightRoster;
}




