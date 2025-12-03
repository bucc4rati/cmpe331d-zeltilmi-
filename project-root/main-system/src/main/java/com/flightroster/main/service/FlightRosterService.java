package com.flightroster.main.service;

import com.flightroster.main.client.*;
import com.flightroster.main.dto.CrewAssignmentRequest;
import com.flightroster.main.entity.FlightRoster;
import com.flightroster.main.entity.RosterPerson;
import com.flightroster.main.repository.FlightRosterRepository;
import com.flightroster.main.repository.RosterPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightRosterService {

    @Autowired
    private FlightRosterRepository flightRosterRepository;

    @Autowired
    private RosterPersonRepository rosterPersonRepository;

    @Autowired
    private FlightInfoClient flightInfoClient;

    @Autowired
    private PilotClient pilotClient;

    @Autowired
    private CabinClient cabinClient;

    @Autowired
    private PassengerClient passengerClient;

    @Autowired
    private SeatAssignmentService seatAssignmentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<FlightRoster> getAllRosters() {
        return flightRosterRepository.findByIsActiveTrue();
    }

    public FlightRoster getRosterById(Long id) {
        return flightRosterRepository.findById(id).orElse(null);
    }

    public FlightRoster getRosterByFlightNumber(String flightNumber) {
        return flightRosterRepository.findByFlightNumber(flightNumber).orElse(null);
    }

    public FlightRoster createRoster(String flightNumber, String databaseType) {
        try {
            // Get flight information
            Map<String, Object> flight = flightInfoClient.getFlightByNumber(flightNumber);
            if (flight == null) {
                throw new RuntimeException("Flight not found: " + flightNumber);
            }

            // Get vehicle type to determine crew requirements
            Map<String, Object> vehicleType = (Map<String, Object>) flight.get("vehicleType");
            String vehicleTypeName = (String) vehicleType.get("typeName");

            // Get pilots by vehicle type and filter by seniority
            // Rules: At least 1 SENIOR, at least 1 JUNIOR, at most 2 TRAINEE
            List<Map<String, Object>> allPilotsByVehicle = pilotClient.getPilotsByVehicleType(vehicleTypeName);
            System.out.println("DEBUG: Found " + allPilotsByVehicle.size() + " pilots for vehicle type " + vehicleTypeName);
            
            // Filter by seniority level
            List<Map<String, Object>> seniorPilots = allPilotsByVehicle.stream()
                    .filter(p -> "SENIOR".equalsIgnoreCase((String) p.get("seniorityLevel")))
                    .filter(p -> Boolean.TRUE.equals(p.get("isAvailable")))
                    .collect(Collectors.toList());
            
            List<Map<String, Object>> juniorPilots = allPilotsByVehicle.stream()
                    .filter(p -> "JUNIOR".equalsIgnoreCase((String) p.get("seniorityLevel")))
                    .filter(p -> Boolean.TRUE.equals(p.get("isAvailable")))
                    .collect(Collectors.toList());
            
            List<Map<String, Object>> traineePilots = allPilotsByVehicle.stream()
                    .filter(p -> "TRAINEE".equalsIgnoreCase((String) p.get("seniorityLevel")))
                    .filter(p -> Boolean.TRUE.equals(p.get("isAvailable")))
                    .collect(Collectors.toList());
            
            System.out.println("DEBUG: SENIOR pilots: " + seniorPilots.size() + ", JUNIOR pilots: " + juniorPilots.size() + ", TRAINEE pilots: " + traineePilots.size());
            
            // Validate we have enough pilots
            if (seniorPilots.isEmpty()) {
                String errorMsg = "Cannot create roster: No available SENIOR pilot for vehicle type " + vehicleTypeName + " (found " + allPilotsByVehicle.size() + " total pilots)";
                System.err.println("ERROR: " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            if (juniorPilots.isEmpty()) {
                String errorMsg = "Cannot create roster: No available JUNIOR pilot for vehicle type " + vehicleTypeName + " (found " + allPilotsByVehicle.size() + " total pilots)";
                System.err.println("ERROR: " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // Select pilots according to rules: at least 1 SENIOR, at least 1 JUNIOR, at most 2 TRAINEE
            List<Map<String, Object>> selectedPilots = new ArrayList<>();
            
            // Add 1 SENIOR pilot (required)
            selectedPilots.add(seniorPilots.get(0));
            
            // Add 1 JUNIOR pilot (required)
            selectedPilots.add(juniorPilots.get(0));
            
            // Optionally add up to 2 TRAINEE pilots
            int maxTrainees = Math.min(traineePilots.size(), 2);
            Random random = new Random();
            int numTrainees = random.nextInt(maxTrainees + 1);
            if (numTrainees > 0) {
                selectedPilots.addAll(traineePilots.subList(0, numTrainees));
            }
            
            List<Map<String, Object>> pilots = selectedPilots;

            // Get cabin crew and filter by vehicle type
            List<Map<String, Object>> allChiefs = cabinClient.getAvailableChiefs();
            List<Map<String, Object>> allRegulars = cabinClient.getAvailableRegulars();
            List<Map<String, Object>> allChefs = cabinClient.getAvailableChefs();
            
            // Get passengers first (needed for optimal crew calculation)
            List<?> passengersRaw = passengerClient.getPassengersByFlightId(flightNumber);
            List<Map<String, Object>> passengers = new ArrayList<>();
            if (passengersRaw != null) {
                for (Object passenger : passengersRaw) {
                    if (passenger instanceof Map) {
                        passengers.add((Map<String, Object>) passenger);
                    }
                }
            }
            
            // Filter cabin crew by vehicle type
            List<Map<String, Object>> chiefs = filterCabinCrewByVehicleType(allChiefs, vehicleTypeName);
            List<Map<String, Object>> regulars = filterCabinCrewByVehicleType(allRegulars, vehicleTypeName);
            List<Map<String, Object>> chefs = filterCabinCrewByVehicleType(allChefs, vehicleTypeName);
            
            // Validate we have enough crew
            if (chiefs.isEmpty()) {
                throw new RuntimeException("Cannot create roster: No available CHIEF attendant for vehicle type " + vehicleTypeName);
            }
            if (regulars.size() < 4) {
                throw new RuntimeException("Cannot create roster: Not enough REGULAR attendants for vehicle type " + vehicleTypeName + " (need at least 4, found " + regulars.size() + ")");
            }
            
            // Select according to constraints (flexible within ranges)
            // 1-4 CHIEF (required: at least 1)
            int maxChiefs = Math.min(chiefs.size(), 4);
            int minChiefs = 1;
            int numChiefs = minChiefs + (maxChiefs > minChiefs ? random.nextInt(maxChiefs - minChiefs + 1) : 0);
            chiefs = chiefs.subList(0, numChiefs);
            
            // 4-16 REGULAR (required: at least 4)
            int maxRegulars = Math.min(regulars.size(), 16);
            int minRegulars = 4;
            int passengerCount = passengers.size();
            int optimalRegulars = Math.max(minRegulars, Math.min(maxRegulars, (int)Math.ceil(passengerCount / 12.0)));
            int numRegulars = Math.max(minRegulars, Math.min(maxRegulars, optimalRegulars + random.nextInt(Math.max(1, maxRegulars - optimalRegulars + 1))));
            regulars = regulars.subList(0, numRegulars);
            
            // 0-2 CHEF (optional)
            int maxChefs = Math.min(chefs.size(), 2);
            int numChefs = random.nextInt(maxChefs + 1);
            chefs = numChefs > 0 ? chefs.subList(0, numChefs) : new ArrayList<>();
            
            // Combine all cabin crew
            List<Map<String, Object>> allCabinCrew = new ArrayList<>();
            allCabinCrew.addAll(chiefs);
            allCabinCrew.addAll(regulars);
            allCabinCrew.addAll(chefs);

            // Create roster
            FlightRoster roster = new FlightRoster();
            roster.setFlightNumber(flightNumber);
            roster.setRosterName("Roster for " + flightNumber + " - " + LocalDateTime.now().toString());
            roster.setCreatedDate(LocalDateTime.now());
            roster.setDatabaseType(databaseType);
            roster.setIsActive(true);

            // Assign seats to passengers automatically
            List<Map<String, Object>> passengersWithSeats = seatAssignmentService.assignSeatsAutomatically(passengers, vehicleType);
            
            // Create seat map
            Map<String, Object> seatMap = seatAssignmentService.getSeatMap(passengersWithSeats, vehicleType);

            // Create roster data JSON
            Map<String, Object> rosterData = new HashMap<>();
            rosterData.put("flight", flight);
            rosterData.put("pilots", pilots);
            rosterData.put("cabinCrew", createCabinCrewList(chiefs, regulars, chefs));
            rosterData.put("passengers", passengersWithSeats);
            rosterData.put("seatMap", seatMap);
            rosterData.put("summary", createSummary(pilots, chiefs, regulars, chefs, passengersWithSeats));

            roster.setRosterData(objectMapper.writeValueAsString(rosterData));

            // Save roster first
            FlightRoster savedRoster = flightRosterRepository.save(roster);
            
            // Update pilot availability to false (they are now assigned)
            try {
                List<Long> pilotIds = pilots.stream()
                    .map(pilot -> ((Number) pilot.get("id")).longValue())
                    .collect(Collectors.toList());
                
                if (!pilotIds.isEmpty()) {
                    pilotClient.updateBulkAvailability(pilotIds, false);
                }
            } catch (Exception e) {
                // Log error but don't fail roster creation
                System.err.println("Warning: Could not update pilot availability: " + e.getMessage());
            }

            // Update cabin crew availability to false (they are now assigned)
            try {
                List<Long> cabinCrewIds = allCabinCrew.stream()
                    .map(crew -> ((Number) crew.get("id")).longValue())
                    .collect(Collectors.toList());
                
                if (!cabinCrewIds.isEmpty()) {
                    cabinClient.updateBulkAvailability(cabinCrewIds, false);
                }
            } catch (Exception e) {
                // Log error but don't fail roster creation
                System.err.println("Warning: Could not update cabin crew availability: " + e.getMessage());
            }

            return savedRoster;

        } catch (Exception e) {
            throw new RuntimeException("Error creating roster: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> createCabinCrewList(List<Map<String, Object>> chiefs, 
                                                         List<Map<String, Object>> regulars, 
                                                         List<Map<String, Object>> chefs) {
        List<Map<String, Object>> cabinCrew = new ArrayList<>();
        cabinCrew.addAll(chiefs);
        cabinCrew.addAll(regulars);
        cabinCrew.addAll(chefs);
        return cabinCrew;
    }

    private Map<String, Object> createSummary(List<Map<String, Object>> pilots, 
                                            List<Map<String, Object>> chiefs, 
                                            List<Map<String, Object>> regulars, 
                                            List<Map<String, Object>> chefs, 
                                            List<Map<String, Object>> passengers) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPilots", pilots.size());
        summary.put("totalCabinCrew", chiefs.size() + regulars.size() + chefs.size());
        summary.put("totalPassengers", passengers.size());
        summary.put("totalPeople", pilots.size() + chiefs.size() + regulars.size() + chefs.size() + passengers.size());
        return summary;
    }

    /**
     * Filter cabin crew by vehicle type restrictions
     * vehicleRestrictions is stored as JSON array string like: ["Boeing 737-800", "Airbus A320"]
     */
    private List<Map<String, Object>> filterCabinCrewByVehicleType(List<Map<String, Object>> cabinCrew, String vehicleTypeName) {
        if (cabinCrew == null || cabinCrew.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> filtered = new ArrayList<>();
        
        for (Map<String, Object> crew : cabinCrew) {
            Object restrictionsObj = crew.get("vehicleRestrictions");
            if (restrictionsObj == null) {
                continue; // Skip if no restrictions
            }
            
            try {
                String restrictionsJson = restrictionsObj.toString();
                // Parse JSON array string
                List<String> restrictions = objectMapper.readValue(restrictionsJson, List.class);
                
                // Check if vehicle type is in the restrictions list
                if (restrictions != null && restrictions.contains(vehicleTypeName)) {
                    filtered.add(crew);
                }
            } catch (Exception e) {
                // If parsing fails, try simple string contains check
                String restrictionsStr = restrictionsObj.toString();
                if (restrictionsStr.contains(vehicleTypeName)) {
                    filtered.add(crew);
                }
            }
        }
        
        return filtered;
    }

    public boolean deleteRoster(Long id) {
        Optional<FlightRoster> roster = flightRosterRepository.findById(id);
        if (roster.isPresent()) {
            FlightRoster rosterToDelete = roster.get();
            rosterToDelete.setIsActive(false);
            flightRosterRepository.save(rosterToDelete);

            // Make pilots and cabin crew available again when roster is deleted
            try {
                Map<String, Object> rosterData = objectMapper.readValue(rosterToDelete.getRosterData(), Map.class);
                
                // Release pilots
                List<Map<String, Object>> pilots = (List<Map<String, Object>>) rosterData.get("pilots");
                if (pilots != null && !pilots.isEmpty()) {
                    List<Long> pilotIds = pilots.stream()
                        .map(pilot -> ((Number) pilot.get("id")).longValue())
                        .collect(Collectors.toList());

                    if (!pilotIds.isEmpty()) {
                        pilotClient.updateBulkAvailability(pilotIds, true);
                    }
                }

                // Release cabin crew
                List<Map<String, Object>> cabinCrew = (List<Map<String, Object>>) rosterData.get("cabinCrew");
                if (cabinCrew != null && !cabinCrew.isEmpty()) {
                    List<Long> cabinCrewIds = cabinCrew.stream()
                        .map(crew -> ((Number) crew.get("id")).longValue())
                        .collect(Collectors.toList());

                    if (!cabinCrewIds.isEmpty()) {
                        cabinClient.updateBulkAvailability(cabinCrewIds, true);
                    }
                }
            } catch (Exception e) {
                // Log error but don't fail roster deletion
                System.err.println("Warning: Could not update crew availability after roster deletion: " + e.getMessage());
            }

            return true;
        }
        return false;
    }

    public String exportRosterAsJson(Long id) {
        FlightRoster roster = flightRosterRepository.findById(id).orElse(null);
        if (roster != null) {
            return roster.getRosterData();
        }
        return null;
    }

    // Crew Selection Methods
    public List<?> getAvailablePilots() {
        try {
            return pilotClient.getAllPilots();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch pilots: " + e.getMessage());
        }
    }

    public List<?> getAvailableCabinCrew() {
        try {
            return cabinClient.getAllAttendants();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch cabin crew: " + e.getMessage());
        }
    }

    public FlightRoster assignCrewManually(Long rosterId, CrewAssignmentRequest request) {
        FlightRoster roster = flightRosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found"));

        try {
            // Parse existing roster data
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> rosterData = mapper.readValue(roster.getRosterData(), Map.class);
            
            // Get OLD pilots and cabin crew before updating (to release them later)
            List<Map<String, Object>> oldPilots = new ArrayList<>();
            Object oldPilotsObj = rosterData.get("pilots");
            if (oldPilotsObj instanceof List) {
                for (Object pilot : (List<?>) oldPilotsObj) {
                    if (pilot instanceof Map) {
                        oldPilots.add((Map<String, Object>) pilot);
                    }
                }
            }
            
            List<Map<String, Object>> oldCabinCrew = new ArrayList<>();
            Object oldCabinCrewObj = rosterData.get("cabinCrew");
            if (oldCabinCrewObj instanceof List) {
                for (Object crew : (List<?>) oldCabinCrewObj) {
                    if (crew instanceof Map) {
                        oldCabinCrew.add((Map<String, Object>) crew);
                    }
                }
            }
            
            // Update crew assignments - REPLACE existing crew, don't add to it
            List<?> pilots = new ArrayList<>();
            List<?> cabinCrew = new ArrayList<>();
            List<?> passengers = (List<?>) rosterData.get("passengers");
            
            // Get pilots - either from request or keep existing ones
            if (request.getPilotIds() != null && !request.getPilotIds().isEmpty()) {
                pilots = pilotClient.getPilotsByIds(request.getPilotIds());
            } else {
                // Keep existing pilots if no new selection provided
                Object existingPilotsObj = rosterData.get("pilots");
                if (existingPilotsObj instanceof List) {
                    pilots = (List<?>) existingPilotsObj;
                }
            }
            
            // Validate pilot seniority rules: at least 1 SENIOR, at least 1 JUNIOR, at most 2 TRAINEE
            if (pilots != null && !pilots.isEmpty()) {
                int seniorCount = 0;
                int juniorCount = 0;
                int traineeCount = 0;
                
                for (Object pilotObj : pilots) {
                    if (pilotObj instanceof Map) {
                        Map<String, Object> pilot = (Map<String, Object>) pilotObj;
                        String seniority = (String) pilot.get("seniorityLevel");
                        if ("SENIOR".equalsIgnoreCase(seniority)) {
                            seniorCount++;
                        } else if ("JUNIOR".equalsIgnoreCase(seniority)) {
                            juniorCount++;
                        } else if ("TRAINEE".equalsIgnoreCase(seniority)) {
                            traineeCount++;
                        }
                    }
                }
                
                if (seniorCount < 1) {
                    throw new RuntimeException("Cannot assign crew: At least 1 SENIOR pilot is required (found " + seniorCount + ")");
                }
                if (juniorCount < 1) {
                    throw new RuntimeException("Cannot assign crew: At least 1 JUNIOR pilot is required (found " + juniorCount + ")");
                }
                if (traineeCount > 2) {
                    throw new RuntimeException("Cannot assign crew: At most 2 TRAINEE pilots allowed (found " + traineeCount + ")");
                }
                if (pilots.size() > 4) { // Max 2 SENIOR + 2 TRAINEE or 1 SENIOR + 1 JUNIOR + 2 TRAINEE
                    throw new RuntimeException("Cannot assign crew: Maximum 4 pilots allowed (found " + pilots.size() + ")");
                }
            }
            rosterData.put("pilots", pilots);
            
            // Get cabin crew - either from request or keep existing ones
            if (request.getCabinCrewIds() != null && !request.getCabinCrewIds().isEmpty()) {
                cabinCrew = cabinClient.getAttendantsByIds(request.getCabinCrewIds());
            } else {
                // Keep existing cabin crew if no new selection provided
                Object existingCabinCrewObj = rosterData.get("cabinCrew");
                if (existingCabinCrewObj instanceof List) {
                    cabinCrew = (List<?>) existingCabinCrewObj;
                }
            }
            
            // Validate cabin crew constraints: 1-4 CHIEF, 4-16 REGULAR, 0-2 CHEF, Min 5, Max 22 total
            if (cabinCrew != null && !cabinCrew.isEmpty()) {
                int chiefCount = 0;
                int regularCount = 0;
                int chefCount = 0;
                
                for (Object crewObj : cabinCrew) {
                    if (crewObj instanceof Map) {
                        Map<String, Object> crew = (Map<String, Object>) crewObj;
                        String attendantType = (String) crew.get("attendantType");
                        if ("CHIEF".equalsIgnoreCase(attendantType)) {
                            chiefCount++;
                        } else if ("REGULAR".equalsIgnoreCase(attendantType)) {
                            regularCount++;
                        } else if ("CHEF".equalsIgnoreCase(attendantType)) {
                            chefCount++;
                        }
                    }
                }
                
                int totalCrew = cabinCrew.size();
                
                if (chiefCount < 1) {
                    throw new RuntimeException("Cannot assign crew: At least 1 CHIEF attendant is required (found " + chiefCount + ")");
                }
                if (chiefCount > 4) {
                    throw new RuntimeException("Cannot assign crew: At most 4 CHIEF attendants allowed (found " + chiefCount + ")");
                }
                if (regularCount < 4) {
                    throw new RuntimeException("Cannot assign crew: At least 4 REGULAR attendants are required (found " + regularCount + ")");
                }
                if (regularCount > 16) {
                    throw new RuntimeException("Cannot assign crew: At most 16 REGULAR attendants allowed (found " + regularCount + ")");
                }
                if (chefCount > 2) {
                    throw new RuntimeException("Cannot assign crew: At most 2 CHEF attendants allowed (found " + chefCount + ")");
                }
                if (totalCrew < 5) {
                    throw new RuntimeException("Cannot assign crew: Minimum 5 cabin crew required (found " + totalCrew + ")");
                }
                if (totalCrew > 22) {
                    throw new RuntimeException("Cannot assign crew: Maximum 22 cabin crew allowed (found " + totalCrew + ")");
                }
            }
            rosterData.put("cabinCrew", cabinCrew);
            
            // Re-assign seats if passengers exist
            if (passengers != null && !passengers.isEmpty()) {
                Map<String, Object> flight = (Map<String, Object>) rosterData.get("flight");
                if (flight != null && flight.get("vehicleType") != null) {
                    Map<String, Object> vehicleType = (Map<String, Object>) flight.get("vehicleType");
                    List<Map<String, Object>> passengersWithSeats = seatAssignmentService.assignSeatsAutomatically(passengers, vehicleType);
                    Map<String, Object> seatMap = seatAssignmentService.getSeatMap(passengersWithSeats, vehicleType);
                    
                    rosterData.put("passengers", passengersWithSeats);
                    rosterData.put("seatMap", seatMap);
                }
            }
            
            // Update summary with new crew counts
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalPilots", pilots != null ? pilots.size() : 0);
            summary.put("totalCabinCrew", cabinCrew != null ? cabinCrew.size() : 0);
            summary.put("totalPassengers", passengers != null ? passengers.size() : 0);
            summary.put("totalPeople", 
                (pilots != null ? pilots.size() : 0) + 
                (cabinCrew != null ? cabinCrew.size() : 0) + 
                (passengers != null ? passengers.size() : 0)
            );
            rosterData.put("summary", summary);
            
            rosterData.put("assignmentType", request.getAssignmentType());
            rosterData.put("lastUpdated", LocalDateTime.now().toString());
            
            // Save updated roster first
            roster.setRosterData(mapper.writeValueAsString(rosterData));
            FlightRoster savedRoster = flightRosterRepository.save(roster);
            
            // Update pilot and cabin crew availability: Release old ones and assign new ones
            try {
                // ===== PILOTS =====
                // Get old pilot IDs
                List<Long> oldPilotIds = oldPilots.stream()
                    .map(pilot -> ((Number) pilot.get("id")).longValue())
                    .collect(Collectors.toList());
                
                // Get new pilot IDs
                List<Long> newPilotIds = new ArrayList<>();
                if (pilots != null && !pilots.isEmpty()) {
                    for (Object pilot : pilots) {
                        if (pilot instanceof Map) {
                            Map<String, Object> pilotMap = (Map<String, Object>) pilot;
                            Object idObj = pilotMap.get("id");
                            if (idObj instanceof Number) {
                                newPilotIds.add(((Number) idObj).longValue());
                            }
                        }
                    }
                }
                
                // Release old pilots (set availability to true)
                if (!oldPilotIds.isEmpty()) {
                    pilotClient.updateBulkAvailability(oldPilotIds, true);
                }
                
                // Assign new pilots (set availability to false)
                if (!newPilotIds.isEmpty()) {
                    pilotClient.updateBulkAvailability(newPilotIds, false);
                }

                // ===== CABIN CREW =====
                // Get old cabin crew IDs
                List<Long> oldCabinCrewIds = oldCabinCrew.stream()
                    .map(crew -> ((Number) crew.get("id")).longValue())
                    .collect(Collectors.toList());
                
                // Get new cabin crew IDs
                List<Long> newCabinCrewIds = new ArrayList<>();
                if (cabinCrew != null && !cabinCrew.isEmpty()) {
                    for (Object crew : cabinCrew) {
                        if (crew instanceof Map) {
                            Map<String, Object> crewMap = (Map<String, Object>) crew;
                            Object idObj = crewMap.get("id");
                            if (idObj instanceof Number) {
                                newCabinCrewIds.add(((Number) idObj).longValue());
                            }
                        }
                    }
                }
                
                // Release old cabin crew (set availability to true)
                if (!oldCabinCrewIds.isEmpty()) {
                    cabinClient.updateBulkAvailability(oldCabinCrewIds, true);
                }
                
                // Assign new cabin crew (set availability to false)
                if (!newCabinCrewIds.isEmpty()) {
                    cabinClient.updateBulkAvailability(newCabinCrewIds, false);
                }
            } catch (Exception e) {
                // Log error but don't fail crew assignment
                System.err.println("Warning: Could not update crew availability during crew assignment: " + e.getMessage());
            }
            
            return savedRoster;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign crew: " + e.getMessage());
        }
    }

    // Flight Info Methods
    public List<?> getAllFlights() {
        try {
            return flightInfoClient.getAllFlights();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch flights: " + e.getMessage());
        }
    }

    public Object getFlightByNumber(String flightNumber) {
        try {
            return flightInfoClient.getFlightByNumber(flightNumber);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch flight: " + e.getMessage());
        }
    }
}
