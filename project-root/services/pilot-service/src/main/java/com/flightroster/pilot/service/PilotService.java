package com.flightroster.pilot.service;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.repository.PilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PilotService {

    @Autowired
    private PilotRepository pilotRepository;

    public List<Pilot> getAllPilots() {
        return pilotRepository.findAll();
    }

    public List<Pilot> getAvailablePilots() {
        return pilotRepository.findByIsAvailableTrue();
    }

    public Pilot getPilotById(Long id) {
        Optional<Pilot> pilot = pilotRepository.findById(id);
        return pilot.orElse(null);
    }

    public Pilot getPilotByPilotId(String pilotId) {
        Optional<Pilot> pilot = pilotRepository.findByPilotId(pilotId);
        return pilot.orElse(null);
    }

    public List<Pilot> getPilotsByVehicleType(String vehicleType) {
        return pilotRepository.findByVehicleRestrictionAndAvailable(vehicleType);
    }

    public List<Pilot> getPilotsBySeniorityLevel(String level) {
        try {
            com.flightroster.pilot.entity.SeniorityLevel seniorityLevel = 
                com.flightroster.pilot.entity.SeniorityLevel.valueOf(level.toUpperCase());
            return pilotRepository.findBySeniorityLevelAndAvailable(seniorityLevel);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list for invalid level
        }
    }

    public List<Pilot> getPilotsByMaxDistance(Double distance) {
        return pilotRepository.findByMaxDistanceGreaterThanEqualAndAvailable(distance);
    }

    public Pilot createPilot(Pilot pilot) {
        return pilotRepository.save(pilot);
    }

    public Pilot updatePilot(Long id, Pilot pilotDetails) {
        Optional<Pilot> optionalPilot = pilotRepository.findById(id);
        if (optionalPilot.isPresent()) {
            Pilot pilot = optionalPilot.get();
            pilot.setPilotId(pilotDetails.getPilotId());
            pilot.setName(pilotDetails.getName());
            pilot.setAge(pilotDetails.getAge());
            pilot.setGender(pilotDetails.getGender());
            pilot.setNationality(pilotDetails.getNationality());
            pilot.setKnownLanguages(pilotDetails.getKnownLanguages());
            pilot.setVehicleRestriction(pilotDetails.getVehicleRestriction());
            pilot.setMaxDistanceKm(pilotDetails.getMaxDistanceKm());
            pilot.setSeniorityLevel(pilotDetails.getSeniorityLevel());
            pilot.setIsAvailable(pilotDetails.getIsAvailable());
            
            return pilotRepository.save(pilot);
        }
        return null;
    }

    public boolean deletePilot(Long id) {
        Optional<Pilot> pilot = pilotRepository.findById(id);
        if (pilot.isPresent()) {
            pilotRepository.delete(pilot.get());
            return true;
        }
        return false;
    }

    public List<Pilot> getPilotsByIds(List<Long> ids) {
        return pilotRepository.findAllById(ids);
    }

    public Pilot updateAvailability(Long id, Boolean isAvailable) {
        Optional<Pilot> optionalPilot = pilotRepository.findById(id);
        if (optionalPilot.isPresent()) {
            Pilot pilot = optionalPilot.get();
            pilot.setIsAvailable(isAvailable);
            return pilotRepository.save(pilot);
        }
        return null;
    }

    public List<Pilot> updateBulkAvailability(List<Long> ids, Boolean isAvailable) {
        List<Pilot> pilots = pilotRepository.findAllById(ids);
        for (Pilot pilot : pilots) {
            pilot.setIsAvailable(isAvailable);
        }
        return pilotRepository.saveAll(pilots);
    }

    public List<Pilot> setAllPilotsAvailable(Boolean isAvailable) {
        List<Pilot> allPilots = pilotRepository.findAll();
        for (Pilot pilot : allPilots) {
            pilot.setIsAvailable(isAvailable);
        }
        return pilotRepository.saveAll(allPilots);
    }
}
