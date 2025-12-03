package com.flightroster.main.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Component
public class FlightInfoClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${flight.info.service.url:http://localhost:8081}")
    private String flightInfoServiceUrl;

    public List<Map<String, Object>> getAllFlights() {
        String url = flightInfoServiceUrl + "/api/flights";
        return restTemplate.getForObject(url, List.class);
    }

    public Map<String, Object> getFlightByNumber(String flightNumber) {
        String url = flightInfoServiceUrl + "/api/flights/" + flightNumber;
        return restTemplate.getForObject(url, Map.class);
    }

    public List<Map<String, Object>> getAirports() {
        String url = flightInfoServiceUrl + "/api/airports";
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getVehicleTypes() {
        String url = flightInfoServiceUrl + "/api/vehicle-types";
        return restTemplate.getForObject(url, List.class);
    }
}




