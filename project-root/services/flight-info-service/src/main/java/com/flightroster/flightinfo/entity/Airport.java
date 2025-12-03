package com.flightroster.flightinfo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "airports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "airport_code", unique = true, nullable = false, length = 3)
    private String airportCode; // AAA format
    
    @Column(name = "airport_name", nullable = false)
    private String airportName;
    
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "country", nullable = false)
    private String country;
}




