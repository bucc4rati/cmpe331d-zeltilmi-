package com.flightroster.flightinfo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "vehicle_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type_name", unique = true, nullable = false)
    private String typeName; // e.g., "Boeing 737", "Airbus A320"
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "business_seats", nullable = false)
    private Integer businessSeats;
    
    @Column(name = "economy_seats", nullable = false)
    private Integer economySeats;
    
    @Column(name = "max_pilots", nullable = false)
    private Integer maxPilots;
    
    @Column(name = "max_cabin_crew", nullable = false)
    private Integer maxCabinCrew;
    
    @Column(name = "seating_plan", columnDefinition = "TEXT")
    private String seatingPlan; // JSON string representing seat layout
    
    @Column(name = "standard_menu", columnDefinition = "TEXT")
    private String standardMenu; // JSON string representing standard menu
}




