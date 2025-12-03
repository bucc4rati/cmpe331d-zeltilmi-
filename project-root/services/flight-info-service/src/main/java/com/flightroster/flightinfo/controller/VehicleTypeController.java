package com.flightroster.flightinfo.controller;

import com.flightroster.flightinfo.entity.VehicleType;
import com.flightroster.flightinfo.service.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class VehicleTypeController {

    @Autowired
    private VehicleTypeService vehicleTypeService;

    @GetMapping
    public ResponseEntity<List<VehicleType>> getAllVehicleTypes() {
        List<VehicleType> vehicleTypes = vehicleTypeService.getAllVehicleTypes();
        return ResponseEntity.ok(vehicleTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleType> getVehicleTypeById(@PathVariable Long id) {
        VehicleType vehicleType = vehicleTypeService.getVehicleTypeById(id);
        if (vehicleType != null) {
            return ResponseEntity.ok(vehicleType);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{typeName}")
    public ResponseEntity<VehicleType> getVehicleTypeByName(@PathVariable String typeName) {
        VehicleType vehicleType = vehicleTypeService.getVehicleTypeByName(typeName);
        if (vehicleType != null) {
            return ResponseEntity.ok(vehicleType);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VehicleType> createVehicleType(@RequestBody VehicleType vehicleType) {
        VehicleType createdVehicleType = vehicleTypeService.createVehicleType(vehicleType);
        return ResponseEntity.ok(createdVehicleType);
    }
}




