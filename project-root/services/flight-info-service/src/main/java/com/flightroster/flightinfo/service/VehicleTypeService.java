package com.flightroster.flightinfo.service;

import com.flightroster.flightinfo.entity.VehicleType;
import com.flightroster.flightinfo.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleTypeService {

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    public List<VehicleType> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll();
    }

    public VehicleType getVehicleTypeById(Long id) {
        Optional<VehicleType> vehicleType = vehicleTypeRepository.findById(id);
        return vehicleType.orElse(null);
    }

    public VehicleType getVehicleTypeByName(String typeName) {
        Optional<VehicleType> vehicleType = vehicleTypeRepository.findByTypeName(typeName);
        return vehicleType.orElse(null);
    }

    public VehicleType createVehicleType(VehicleType vehicleType) {
        return vehicleTypeRepository.save(vehicleType);
    }
}




