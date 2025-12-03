package com.flightroster.pilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "pilots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pilot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pilot_id", unique = true, nullable = false)
    private String pilotId; // Unique pilot identifier
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "age", nullable = false)
    private Integer age;
    
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @Column(name = "nationality", nullable = false)
    private String nationality;
    
    @Column(name = "known_languages", columnDefinition = "TEXT")
    private String knownLanguages; // JSON string for multiple languages
    
    @Column(name = "vehicle_restriction", nullable = false)
    private String vehicleRestriction; // Single vehicle type pilot can operate
    
    @Column(name = "max_distance_km", nullable = false)
    private Double maxDistanceKm; // Maximum allowed distance
    
    @Enumerated(EnumType.STRING)
    @Column(name = "seniority_level", nullable = false)
    private SeniorityLevel seniorityLevel;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
}

