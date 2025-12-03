package com.flightroster.main.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class PilotClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pilot.service.url:http://localhost:8082}")
    private String pilotServiceUrl;

    public List<Map<String, Object>> getAvailablePilots() {
        String url = pilotServiceUrl + "/api/pilots/available";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getPilotsByVehicleType(String vehicleType) {
        String url = pilotServiceUrl + "/api/pilots/vehicle-type/" + vehicleType;
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getPilotsBySeniorityLevel(String level) {
        String url = pilotServiceUrl + "/api/pilots/seniority/" + level;
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getAllPilots() {
        String url = pilotServiceUrl + "/api/pilots";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getPilotsByIds(List<Long> ids) {
        String url = pilotServiceUrl + "/api/pilots/by-ids";
        return restTemplate.postForObject(url, ids, List.class);
    }

    public List<Map<String, Object>> updateBulkAvailability(List<Long> ids, Boolean isAvailable) {
        String url = pilotServiceUrl + "/api/pilots/bulk-availability?isAvailable=" + isAvailable;
        restTemplate.put(url, ids);
        return getPilotsByIds(ids); // Return updated pilots
    }
}
