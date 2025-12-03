package com.flightroster.cabin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "attendants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "attendant_id", unique = true, nullable = false)
    private String attendantId; // Unique attendant identifier
    
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attendant_type", nullable = false)
    private AttendantType attendantType;
    
    @Column(name = "vehicle_restrictions", columnDefinition = "TEXT")
    private String vehicleRestrictions; // JSON string for multiple vehicle types
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "recipe_types", columnDefinition = "TEXT")
    private String recipeTypes; // JSON string for chef's recipe types (only for CHEF type)
}




