package com.flightroster.flightinfo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    
    @Id
    @Column(name = "flight_number", length = 6)
    private String flightNumber; // AANNNN format
    
    @Column(name = "flight_date", nullable = false)
    private LocalDateTime flightDate;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_airport_id", nullable = false)
    private Airport sourceAirport;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_airport_id", nullable = false)
    private Airport destinationAirport;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;
    
    @Column(name = "is_shared_flight", nullable = false)
    private Boolean isSharedFlight = false;
    
    @Column(name = "shared_flight_number", length = 6)
    private String sharedFlightNumber; // AANNNN format
    
    @Column(name = "shared_airline_name")
    private String sharedAirlineName;
    
    @Column(name = "connecting_flight_number", length = 6)
    private String connectingFlightNumber; // Only for shared flights
}
