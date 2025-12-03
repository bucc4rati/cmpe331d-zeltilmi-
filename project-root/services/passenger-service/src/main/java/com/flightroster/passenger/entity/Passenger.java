package com.flightroster.passenger.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "passenger_id", unique = true, nullable = false)
    private String passengerId; // Unique passenger identifier
    
    @Column(name = "flight_id", nullable = false, length = 6)
    private String flightId; // Flight number (AANNNN format)
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "age", nullable = false)
    private Integer age;
    
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @Column(name = "nationality", nullable = false)
    private String nationality;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;
    
    @Column(name = "seat_number")
    private String seatNumber; // Can be null if not assigned yet
    
    @Column(name = "is_infant", nullable = false)
    private Boolean isInfant = false; // Age 0-2
    
    @Column(name = "parent_passenger_id")
    private String parentPassengerId; // For infant passengers
    
    @Column(name = "affiliated_passenger_ids", columnDefinition = "TEXT")
    private String affiliatedPassengerIds; // JSON string for neighboring passengers
}




