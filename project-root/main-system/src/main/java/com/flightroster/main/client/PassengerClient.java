package com.flightroster.main.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class PassengerClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${passenger.service.url:http://localhost:8084}")
    private String passengerServiceUrl;

    public List<Map<String, Object>> getPassengersByFlightId(String flightId) {
        String url = passengerServiceUrl + "/api/passengers/flight/" + flightId;
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getPassengersByFlightAndSeatType(String flightId, String seatType) {
        String url = passengerServiceUrl + "/api/passengers/flight/" + flightId + "/seat-type/" + seatType;
        return restTemplate.getForObject(url, List.class);
    }

    public List<Map<String, Object>> getInfantsByFlightId(String flightId) {
        String url = passengerServiceUrl + "/api/passengers/flight/" + flightId + "/infants";
        return restTemplate.getForObject(url, List.class);
    }
}




