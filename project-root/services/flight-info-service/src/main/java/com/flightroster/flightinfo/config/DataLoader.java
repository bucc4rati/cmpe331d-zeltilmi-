package com.flightroster.flightinfo.config;

import com.flightroster.flightinfo.entity.Airport;
import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.entity.VehicleType;
import com.flightroster.flightinfo.repository.AirportRepository;
import com.flightroster.flightinfo.repository.FlightRepository;
import com.flightroster.flightinfo.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public void run(String... args) throws Exception {
        // Örnek havaalanları - sadece yoksa ekle
        if (airportRepository.findByAirportCode("IST").isEmpty()) {
            Airport istanbul = new Airport();
            istanbul.setAirportCode("IST");
            istanbul.setAirportName("Istanbul Airport");
            istanbul.setCity("Istanbul");
            istanbul.setCountry("Turkey");
            airportRepository.save(istanbul);
        }

        if (airportRepository.findByAirportCode("ESB").isEmpty()) {
            Airport ankara = new Airport();
            ankara.setAirportCode("ESB");
            ankara.setAirportName("Esenboga Airport");
            ankara.setCity("Ankara");
            ankara.setCountry("Turkey");
            airportRepository.save(ankara);
        }

        if (airportRepository.findByAirportCode("LHR").isEmpty()) {
            Airport london = new Airport();
            london.setAirportCode("LHR");
            london.setAirportName("Heathrow Airport");
            london.setCity("London");
            london.setCountry("UK");
            airportRepository.save(london);
        }

        if (airportRepository.findByAirportCode("CDG").isEmpty()) {
            Airport paris = new Airport();
            paris.setAirportCode("CDG");
            paris.setAirportName("Charles de Gaulle Airport");
            paris.setCity("Paris");
            paris.setCountry("France");
            airportRepository.save(paris);
        }

        if (airportRepository.findByAirportCode("TXL").isEmpty()) {
            Airport berlin = new Airport();
            berlin.setAirportCode("TXL");
            berlin.setAirportName("Tegel Airport");
            berlin.setCity("Berlin");
            berlin.setCountry("Germany");
            airportRepository.save(berlin);
        }

        if (airportRepository.findByAirportCode("FCO").isEmpty()) {
            Airport rome = new Airport();
            rome.setAirportCode("FCO");
            rome.setAirportName("Fiumicino Airport");
            rome.setCity("Rome");
            rome.setCountry("Italy");
            airportRepository.save(rome);
        }

        // Additional airports for TK006-TK012
        if (airportRepository.findByAirportCode("JFK").isEmpty()) {
            Airport newyork = new Airport();
            newyork.setAirportCode("JFK");
            newyork.setAirportName("John F. Kennedy International Airport");
            newyork.setCity("New York");
            newyork.setCountry("USA");
            airportRepository.save(newyork);
        }

        if (airportRepository.findByAirportCode("DXB").isEmpty()) {
            Airport dubai = new Airport();
            dubai.setAirportCode("DXB");
            dubai.setAirportName("Dubai International Airport");
            dubai.setCity("Dubai");
            dubai.setCountry("UAE");
            airportRepository.save(dubai);
        }

        if (airportRepository.findByAirportCode("NRT").isEmpty()) {
            Airport tokyo = new Airport();
            tokyo.setAirportCode("NRT");
            tokyo.setAirportName("Narita International Airport");
            tokyo.setCity("Tokyo");
            tokyo.setCountry("Japan");
            airportRepository.save(tokyo);
        }

        if (airportRepository.findByAirportCode("MAD").isEmpty()) {
            Airport madrid = new Airport();
            madrid.setAirportCode("MAD");
            madrid.setAirportName("Adolfo Suárez Madrid-Barajas Airport");
            madrid.setCity("Madrid");
            madrid.setCountry("Spain");
            airportRepository.save(madrid);
        }

        if (airportRepository.findByAirportCode("AMS").isEmpty()) {
            Airport amsterdam = new Airport();
            amsterdam.setAirportCode("AMS");
            amsterdam.setAirportName("Amsterdam Airport Schiphol");
            amsterdam.setCity("Amsterdam");
            amsterdam.setCountry("Netherlands");
            airportRepository.save(amsterdam);
        }

        if (airportRepository.findByAirportCode("FRA").isEmpty()) {
            Airport frankfurt = new Airport();
            frankfurt.setAirportCode("FRA");
            frankfurt.setAirportName("Frankfurt Airport");
            frankfurt.setCity("Frankfurt");
            frankfurt.setCountry("Germany");
            airportRepository.save(frankfurt);
        }

        if (airportRepository.findByAirportCode("SVO").isEmpty()) {
            Airport moscow = new Airport();
            moscow.setAirportCode("SVO");
            moscow.setAirportName("Sheremetyevo International Airport");
            moscow.setCity("Moscow");
            moscow.setCountry("Russia");
            airportRepository.save(moscow);
        }

        // Örnek uçak tipleri - sadece yoksa ekle
        if (vehicleTypeRepository.findByTypeName("Boeing 737-800").isEmpty()) {
            VehicleType boeing737 = new VehicleType();
            boeing737.setTypeName("Boeing 737-800");
            boeing737.setTotalSeats(189);
            boeing737.setBusinessSeats(20);
            boeing737.setEconomySeats(169);
            boeing737.setMaxPilots(2);
            boeing737.setMaxCabinCrew(6);
            boeing737.setSeatingPlan("{\"rows\": 31, \"seatsPerRow\": 6, \"businessRows\": 4}");
            boeing737.setStandardMenu("{\"meals\": [\"Chicken\", \"Beef\", \"Vegetarian\"]}");
            vehicleTypeRepository.save(boeing737);
        }

        if (vehicleTypeRepository.findByTypeName("Airbus A320").isEmpty()) {
            VehicleType airbus320 = new VehicleType();
            airbus320.setTypeName("Airbus A320");
            airbus320.setTotalSeats(180);
            airbus320.setBusinessSeats(16);
            airbus320.setEconomySeats(164);
            airbus320.setMaxPilots(2);
            airbus320.setMaxCabinCrew(5);
            airbus320.setSeatingPlan("{\"rows\": 30, \"seatsPerRow\": 6, \"businessRows\": 3}");
            airbus320.setStandardMenu("{\"meals\": [\"Fish\", \"Pasta\", \"Salad\"]}");
            vehicleTypeRepository.save(airbus320);
        }

        // Additional vehicle types for TK006-TK012
        if (vehicleTypeRepository.findByTypeName("Airbus A330").isEmpty()) {
            VehicleType airbus330 = new VehicleType();
            airbus330.setTypeName("Airbus A330");
            airbus330.setTotalSeats(290);
            airbus330.setBusinessSeats(30);
            airbus330.setEconomySeats(260);
            airbus330.setMaxPilots(2);
            airbus330.setMaxCabinCrew(7);
            airbus330.setSeatingPlan("{\"rows\": 48, \"seatsPerRow\": 6, \"businessRows\": 5}");
            airbus330.setStandardMenu("{\"meals\": [\"Lamb\", \"Chicken\", \"Pasta\", \"Salad\"]}");
            vehicleTypeRepository.save(airbus330);
        }

        // Örnek uçuşlar - sadece yoksa ekle
        if (flightRepository.findByFlightNumber("TK001").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport ankara = airportRepository.findByAirportCode("ESB").orElse(null);
            VehicleType boeing737 = vehicleTypeRepository.findByTypeName("Boeing 737-800").orElse(null);
            
            if (istanbul != null && ankara != null && boeing737 != null) {
                Flight flight1 = new Flight();
                flight1.setFlightNumber("TK001");
                flight1.setFlightDate(LocalDateTime.of(2024, 1, 15, 10, 30));
                flight1.setDurationMinutes(90);
                flight1.setDistanceKm(350.0);
                flight1.setSourceAirport(istanbul);
                flight1.setDestinationAirport(ankara);
                flight1.setVehicleType(boeing737);
                flight1.setIsSharedFlight(false);
                flightRepository.save(flight1);
            }
        }

        if (flightRepository.findByFlightNumber("TK002").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport london = airportRepository.findByAirportCode("LHR").orElse(null);
            VehicleType airbus320 = vehicleTypeRepository.findByTypeName("Airbus A320").orElse(null);
            
            if (istanbul != null && london != null && airbus320 != null) {
                Flight flight2 = new Flight();
                flight2.setFlightNumber("TK002");
                flight2.setFlightDate(LocalDateTime.of(2024, 1, 15, 14, 45));
                flight2.setDurationMinutes(240);
                flight2.setDistanceKm(1200.0);
                flight2.setSourceAirport(istanbul);
                flight2.setDestinationAirport(london);
                flight2.setVehicleType(airbus320);
                flight2.setIsSharedFlight(false);
                flightRepository.save(flight2);
            }
        }

        // Yeni uçuşlar - TK003, TK004, TK005
        if (flightRepository.findByFlightNumber("TK003").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport paris = airportRepository.findByAirportCode("CDG").orElse(null);
            VehicleType boeing737 = vehicleTypeRepository.findByTypeName("Boeing 737-800").orElse(null);
            
            if (istanbul != null && paris != null && boeing737 != null) {
                Flight flight3 = new Flight();
                flight3.setFlightNumber("TK003");
                flight3.setFlightDate(LocalDateTime.of(2024, 1, 15, 16, 30));
                flight3.setDurationMinutes(180);
                flight3.setDistanceKm(1000.0);
                flight3.setSourceAirport(istanbul);
                flight3.setDestinationAirport(paris);
                flight3.setVehicleType(boeing737);
                flight3.setIsSharedFlight(false);
                flightRepository.save(flight3);
            }
        }

        if (flightRepository.findByFlightNumber("TK004").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport berlin = airportRepository.findByAirportCode("TXL").orElse(null);
            VehicleType airbus320 = vehicleTypeRepository.findByTypeName("Airbus A320").orElse(null);
            
            if (istanbul != null && berlin != null && airbus320 != null) {
                Flight flight4 = new Flight();
                flight4.setFlightNumber("TK004");
                flight4.setFlightDate(LocalDateTime.of(2024, 1, 15, 18, 45));
                flight4.setDurationMinutes(200);
                flight4.setDistanceKm(1100.0);
                flight4.setSourceAirport(istanbul);
                flight4.setDestinationAirport(berlin);
                flight4.setVehicleType(airbus320);
                flight4.setIsSharedFlight(false);
                flightRepository.save(flight4);
            }
        }

        if (flightRepository.findByFlightNumber("TK005").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport rome = airportRepository.findByAirportCode("FCO").orElse(null);
            VehicleType boeing737 = vehicleTypeRepository.findByTypeName("Boeing 737-800").orElse(null);
            
            if (istanbul != null && rome != null && boeing737 != null) {
                Flight flight5 = new Flight();
                flight5.setFlightNumber("TK005");
                flight5.setFlightDate(LocalDateTime.of(2024, 1, 15, 20, 15));
                flight5.setDurationMinutes(190);
                flight5.setDistanceKm(1050.0);
                flight5.setSourceAirport(istanbul);
                flight5.setDestinationAirport(rome);
                flight5.setVehicleType(boeing737);
                flight5.setIsSharedFlight(false);
                flightRepository.save(flight5);
            }
        }

        // Additional flights TK006-TK012
        // TK006 removed - was using Boeing 777-300ER

        if (flightRepository.findByFlightNumber("TK007").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport dubai = airportRepository.findByAirportCode("DXB").orElse(null);
            VehicleType airbus330 = vehicleTypeRepository.findByTypeName("Airbus A330").orElse(null);
            
            if (istanbul != null && dubai != null && airbus330 != null) {
                Flight flight7 = new Flight();
                flight7.setFlightNumber("TK007");
                flight7.setFlightDate(LocalDateTime.of(2024, 1, 16, 10, 30));
                flight7.setDurationMinutes(240);
                flight7.setDistanceKm(2000.0);
                flight7.setSourceAirport(istanbul);
                flight7.setDestinationAirport(dubai);
                flight7.setVehicleType(airbus330);
                flight7.setIsSharedFlight(false);
                flightRepository.save(flight7);
            }
        }

        // TK008 removed - was using Boeing 777-300ER

        if (flightRepository.findByFlightNumber("TK009").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport madrid = airportRepository.findByAirportCode("MAD").orElse(null);
            VehicleType airbus320 = vehicleTypeRepository.findByTypeName("Airbus A320").orElse(null);
            
            if (istanbul != null && madrid != null && airbus320 != null) {
                Flight flight9 = new Flight();
                flight9.setFlightNumber("TK009");
                flight9.setFlightDate(LocalDateTime.of(2024, 1, 16, 14, 15));
                flight9.setDurationMinutes(180);
                flight9.setDistanceKm(1200.0);
                flight9.setSourceAirport(istanbul);
                flight9.setDestinationAirport(madrid);
                flight9.setVehicleType(airbus320);
                flight9.setIsSharedFlight(false);
                flightRepository.save(flight9);
            }
        }

        if (flightRepository.findByFlightNumber("TK010").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport amsterdam = airportRepository.findByAirportCode("AMS").orElse(null);
            VehicleType boeing737 = vehicleTypeRepository.findByTypeName("Boeing 737-800").orElse(null);
            
            if (istanbul != null && amsterdam != null && boeing737 != null) {
                Flight flight10 = new Flight();
                flight10.setFlightNumber("TK010");
                flight10.setFlightDate(LocalDateTime.of(2024, 1, 16, 16, 30));
                flight10.setDurationMinutes(200);
                flight10.setDistanceKm(1300.0);
                flight10.setSourceAirport(istanbul);
                flight10.setDestinationAirport(amsterdam);
                flight10.setVehicleType(boeing737);
                flight10.setIsSharedFlight(false);
                flightRepository.save(flight10);
            }
        }

        if (flightRepository.findByFlightNumber("TK011").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport frankfurt = airportRepository.findByAirportCode("FRA").orElse(null);
            VehicleType airbus330 = vehicleTypeRepository.findByTypeName("Airbus A330").orElse(null);
            
            if (istanbul != null && frankfurt != null && airbus330 != null) {
                Flight flight11 = new Flight();
                flight11.setFlightNumber("TK011");
                flight11.setFlightDate(LocalDateTime.of(2024, 1, 16, 18, 45));
                flight11.setDurationMinutes(220);
                flight11.setDistanceKm(1400.0);
                flight11.setSourceAirport(istanbul);
                flight11.setDestinationAirport(frankfurt);
                flight11.setVehicleType(airbus330);
                flight11.setIsSharedFlight(false);
                flightRepository.save(flight11);
            }
        }

        if (flightRepository.findByFlightNumber("TK012").isEmpty()) {
            Airport istanbul = airportRepository.findByAirportCode("IST").orElse(null);
            Airport moscow = airportRepository.findByAirportCode("SVO").orElse(null);
            VehicleType airbus320 = vehicleTypeRepository.findByTypeName("Airbus A320").orElse(null);
            
            if (istanbul != null && moscow != null && airbus320 != null) {
                Flight flight12 = new Flight();
                flight12.setFlightNumber("TK012");
                flight12.setFlightDate(LocalDateTime.of(2024, 1, 16, 20, 0));
                flight12.setDurationMinutes(210);
                flight12.setDistanceKm(1500.0);
                flight12.setSourceAirport(istanbul);
                flight12.setDestinationAirport(moscow);
                flight12.setVehicleType(airbus320);
                flight12.setIsSharedFlight(false);
                flightRepository.save(flight12);
            }
        }

        System.out.println("Örnek veriler yüklendi!");
    }
}
