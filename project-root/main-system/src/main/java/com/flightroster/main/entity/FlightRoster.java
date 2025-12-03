package com.flightroster.main.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flight_rosters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightRoster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flight_number", nullable = false, length = 6)
    private String flightNumber;
    
    @Column(name = "roster_name", nullable = false)
    private String rosterName;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "database_type", nullable = false)
    private String databaseType; // SQL or NoSQL
    
    @Column(name = "roster_data", columnDefinition = "TEXT")
    private String rosterData; // JSON string containing all roster information
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}




