package com.flightroster.main.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class CabinClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cabin.service.url:http://localhost:8083}")
    private String cabinServiceUrl;

    public List<Map<String, Object>> getAvailableAttendants() {
        String url = cabinServiceUrl + "/api/attendants/available";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getAvailableChefs() {
        String url = cabinServiceUrl + "/api/attendants/chefs";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getAvailableChiefs() {
        String url = cabinServiceUrl + "/api/attendants/chiefs";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getAvailableRegulars() {
        String url = cabinServiceUrl + "/api/attendants/regulars";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getAllAttendants() {
        String url = cabinServiceUrl + "/api/attendants";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getAttendantsByIds(List<Long> ids) {
        String url = cabinServiceUrl + "/api/attendants/by-ids";
        return restTemplate.postForObject(url, ids, List.class);
    }

    public List<Map<String, Object>> updateBulkAvailability(List<Long> ids, Boolean isAvailable) {
        String url = cabinServiceUrl + "/api/attendants/bulk-availability?isAvailable=" + isAvailable;
        restTemplate.put(url, ids);
        return getAttendantsByIds(ids); // Return updated attendants
    }
}
