package com.flightroster.main.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SeatAssignmentService {

    public List<Map<String, Object>> assignSeatsAutomatically(List<?> passengers, 
                                                             Map<String, Object> vehicleType) {
        if (passengers == null || passengers.isEmpty()) {
            return new ArrayList<>();
        }

        // Parse vehicle type information
        int totalSeats = (Integer) vehicleType.get("totalSeats");
        int businessSeats = (Integer) vehicleType.get("businessSeats");
        int economySeats = (Integer) vehicleType.get("economySeats");
        
        // Parse seating plan
        String seatingPlanJson = (String) vehicleType.get("seatingPlan");
        Map<String, Object> seatingPlan = parseSeatingPlan(seatingPlanJson);
        
        int rows = (Integer) seatingPlan.get("rows");
        int seatsPerRow = (Integer) seatingPlan.get("seatsPerRow");
        int businessRows = (Integer) seatingPlan.get("businessRows");
        
        List<Map<String, Object>> assignedPassengers = new ArrayList<>();
        
        // Convert passengers to Map format and sort by priority (business class passengers first)
        List<Map<String, Object>> sortedPassengers = new ArrayList<>();
        for (Object passenger : passengers) {
            if (passenger instanceof Map) {
                sortedPassengers.add((Map<String, Object>) passenger);
            } else {
                // Convert entity to map if needed
                Map<String, Object> passengerMap = new HashMap<>();
                // This will be handled by the calling service
                sortedPassengers.add(passengerMap);
            }
        }
        sortedPassengers.sort((p1, p2) -> {
            String seatType1 = (String) p1.getOrDefault("seatType", "ECONOMY");
            String seatType2 = (String) p2.getOrDefault("seatType", "ECONOMY");
            
            if ("BUSINESS".equals(seatType1) && !"BUSINESS".equals(seatType2)) {
                return -1;
            } else if (!"BUSINESS".equals(seatType1) && "BUSINESS".equals(seatType2)) {
                return 1;
            }
            return 0;
        });
        
        // Track assigned seats
        Set<String> assignedSeats = new HashSet<>();
        
        // Assign business class seats first
        int businessAssigned = 0;
        for (Map<String, Object> passenger : sortedPassengers) {
            String preferredSeatType = (String) passenger.getOrDefault("seatType", "ECONOMY");
            
            if ("BUSINESS".equals(preferredSeatType) && businessAssigned < businessSeats) {
                String seatNumber = assignBusinessSeat(businessAssigned, businessRows, seatsPerRow, assignedSeats);
                if (seatNumber != null) {
                    passenger.put("seatNumber", seatNumber);
                    passenger.put("seatType", "BUSINESS");
                    assignedSeats.add(seatNumber);
                    businessAssigned++;
                }
            }
        }
        
        // Assign economy class seats
        int economyAssigned = 0;
        for (Map<String, Object> passenger : sortedPassengers) {
            if (!passenger.containsKey("seatNumber")) {
                String seatNumber = assignEconomySeat(economyAssigned, businessRows, rows, seatsPerRow, assignedSeats);
                if (seatNumber != null) {
                    passenger.put("seatNumber", seatNumber);
                    passenger.put("seatType", "ECONOMY");
                    assignedSeats.add(seatNumber);
                    economyAssigned++;
                }
            }
        }
        
        return sortedPassengers;
    }
    
    private String assignBusinessSeat(int businessAssigned, int businessRows, int seatsPerRow, Set<String> assignedSeats) {
        int row = (businessAssigned / seatsPerRow) + 1;
        int seat = (businessAssigned % seatsPerRow) + 1;
        
        if (row <= businessRows) {
            String seatNumber = String.format("%d%c", row, (char)('A' + seat - 1));
            if (!assignedSeats.contains(seatNumber)) {
                return seatNumber;
            }
        }
        
        // If business class is full, try to find any available business seat
        for (int r = 1; r <= businessRows; r++) {
            for (int s = 1; s <= seatsPerRow; s++) {
                String seatNumber = String.format("%d%c", r, (char)('A' + s - 1));
                if (!assignedSeats.contains(seatNumber)) {
                    return seatNumber;
                }
            }
        }
        
        return null;
    }
    
    private String assignEconomySeat(int economyAssigned, int businessRows, int totalRows, int seatsPerRow, Set<String> assignedSeats) {
        int row = businessRows + 1 + (economyAssigned / seatsPerRow);
        int seat = (economyAssigned % seatsPerRow) + 1;
        
        if (row <= totalRows) {
            String seatNumber = String.format("%d%c", row, (char)('A' + seat - 1));
            if (!assignedSeats.contains(seatNumber)) {
                return seatNumber;
            }
        }
        
        // If assigned seat is taken, find next available
        for (int r = businessRows + 1; r <= totalRows; r++) {
            for (int s = 1; s <= seatsPerRow; s++) {
                String seatNumber = String.format("%d%c", r, (char)('A' + s - 1));
                if (!assignedSeats.contains(seatNumber)) {
                    return seatNumber;
                }
            }
        }
        
        return null;
    }
    
    private Map<String, Object> parseSeatingPlan(String seatingPlanJson) {
        Map<String, Object> seatingPlan = new HashMap<>();
        
        if (seatingPlanJson != null && !seatingPlanJson.isEmpty()) {
            try {
                // Simple JSON parsing for seating plan
                seatingPlanJson = seatingPlanJson.replaceAll("[{}\"]", "");
                String[] parts = seatingPlanJson.split(",");
                
                for (String part : parts) {
                    String[] keyValue = part.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        
                        switch (key) {
                            case "rows":
                                seatingPlan.put("rows", Integer.parseInt(value));
                                break;
                            case "seatsPerRow":
                                seatingPlan.put("seatsPerRow", Integer.parseInt(value));
                                break;
                            case "businessRows":
                                seatingPlan.put("businessRows", Integer.parseInt(value));
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                // Use default values if parsing fails
                seatingPlan.put("rows", 30);
                seatingPlan.put("seatsPerRow", 6);
                seatingPlan.put("businessRows", 4);
            }
        } else {
            // Default seating plan
            seatingPlan.put("rows", 30);
            seatingPlan.put("seatsPerRow", 6);
            seatingPlan.put("businessRows", 4);
        }
        
        return seatingPlan;
    }
    
    public Map<String, Object> getSeatMap(List<?> passengers, Map<String, Object> vehicleType) {
        Map<String, Object> seatMap = new HashMap<>();
        
        // Parse seating plan
        String seatingPlanJson = (String) vehicleType.get("seatingPlan");
        Map<String, Object> seatingPlan = parseSeatingPlan(seatingPlanJson);
        
        int rows = (Integer) seatingPlan.get("rows");
        int seatsPerRow = (Integer) seatingPlan.get("seatsPerRow");
        int businessRows = (Integer) seatingPlan.get("businessRows");
        
        // Create seat map
        List<List<Map<String, Object>>> seatLayout = new ArrayList<>();
        
        for (int row = 1; row <= rows; row++) {
            List<Map<String, Object>> rowSeats = new ArrayList<>();
            
            for (int seat = 1; seat <= seatsPerRow; seat++) {
                String seatNumber = String.format("%d%c", row, (char)('A' + seat - 1));
                String seatType = row <= businessRows ? "BUSINESS" : "ECONOMY";
                
                // Find passenger for this seat
                Map<String, Object> passenger = null;
                if (passengers != null) {
                    for (Object p : passengers) {
                        if (p instanceof Map) {
                            Map<String, Object> passengerMap = (Map<String, Object>) p;
                            if (seatNumber.equals(passengerMap.get("seatNumber"))) {
                                passenger = passengerMap;
                                break;
                            }
                        }
                    }
                }
                
                Map<String, Object> seatInfo = new HashMap<>();
                seatInfo.put("seatNumber", seatNumber);
                seatInfo.put("seatType", seatType);
                seatInfo.put("isOccupied", passenger != null);
                seatInfo.put("passenger", passenger);
                
                rowSeats.add(seatInfo);
            }
            
            seatLayout.add(rowSeats);
        }
        
        seatMap.put("layout", seatLayout);
        seatMap.put("totalRows", rows);
        seatMap.put("seatsPerRow", seatsPerRow);
        seatMap.put("businessRows", businessRows);
        seatMap.put("totalSeats", rows * seatsPerRow);
        
        return seatMap;
    }
}
