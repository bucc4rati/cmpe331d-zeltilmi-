package com.flightroster.cabin.service;

import com.flightroster.cabin.entity.Attendant;
import com.flightroster.cabin.entity.AttendantType;
import com.flightroster.cabin.repository.AttendantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AttendantService {

    @Autowired
    private AttendantRepository attendantRepository;

    public List<Attendant> getAllAttendants() {
        return attendantRepository.findAll();
    }

    public List<Attendant> getAvailableAttendants() {
        return attendantRepository.findByIsAvailableTrue();
    }

    public Attendant getAttendantById(Long id) {
        Optional<Attendant> attendant = attendantRepository.findById(id);
        return attendant.orElse(null);
    }

    public Attendant getAttendantByAttendantId(String attendantId) {
        Optional<Attendant> attendant = attendantRepository.findByAttendantId(attendantId);
        return attendant.orElse(null);
    }

    public List<Attendant> getAttendantsByType(String type) {
        try {
            AttendantType attendantType = AttendantType.valueOf(type.toUpperCase());
            return attendantRepository.findByAttendantTypeAndAvailable(attendantType);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list for invalid type
        }
    }

    public List<Attendant> getAvailableChefs() {
        return attendantRepository.findAvailableChefs();
    }

    public List<Attendant> getAvailableChiefs() {
        return attendantRepository.findAvailableChiefs();
    }

    public List<Attendant> getAvailableRegulars() {
        return attendantRepository.findAvailableRegulars();
    }

    public Attendant createAttendant(Attendant attendant) {
        return attendantRepository.save(attendant);
    }

    public Attendant updateAttendant(Long id, Attendant attendantDetails) {
        Optional<Attendant> optionalAttendant = attendantRepository.findById(id);
        if (optionalAttendant.isPresent()) {
            Attendant attendant = optionalAttendant.get();
            attendant.setAttendantId(attendantDetails.getAttendantId());
            attendant.setName(attendantDetails.getName());
            attendant.setAge(attendantDetails.getAge());
            attendant.setGender(attendantDetails.getGender());
            attendant.setNationality(attendantDetails.getNationality());
            attendant.setKnownLanguages(attendantDetails.getKnownLanguages());
            attendant.setAttendantType(attendantDetails.getAttendantType());
            attendant.setVehicleRestrictions(attendantDetails.getVehicleRestrictions());
            attendant.setIsAvailable(attendantDetails.getIsAvailable());
            attendant.setRecipeTypes(attendantDetails.getRecipeTypes());
            
            return attendantRepository.save(attendant);
        }
        return null;
    }

    public boolean deleteAttendant(Long id) {
        Optional<Attendant> attendant = attendantRepository.findById(id);
        if (attendant.isPresent()) {
            attendantRepository.delete(attendant.get());
            return true;
        }
        return false;
    }

    public List<Attendant> getAttendantsByIds(List<Long> ids) {
        return attendantRepository.findAllById(ids);
    }

    public List<Attendant> updateBulkAvailability(List<Long> ids, Boolean isAvailable) {
        List<Attendant> attendants = attendantRepository.findAllById(ids);
        for (Attendant attendant : attendants) {
            attendant.setIsAvailable(isAvailable);
        }
        return attendantRepository.saveAll(attendants);
    }
}
