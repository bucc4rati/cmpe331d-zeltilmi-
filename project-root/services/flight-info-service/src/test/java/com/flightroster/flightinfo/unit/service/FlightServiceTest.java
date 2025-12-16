package com.flightroster.flightinfo.unit.service;

import com.flightroster.flightinfo.entity.Airport;
import com.flightroster.flightinfo.entity.Flight;
import com.flightroster.flightinfo.entity.VehicleType;
import com.flightroster.flightinfo.repository.FlightRepository;
import com.flightroster.flightinfo.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(FlightServiceTest.class);

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight sampleFlight;

    @BeforeEach
    void setUp() {
        Airport source = new Airport();
        source.setAirportCode("IST");

        Airport dest = new Airport();
        dest.setAirportCode("AMS");

        VehicleType vehicleType = new VehicleType();
        vehicleType.setTypeName("A320");

        sampleFlight = new Flight(
                "TK1234",
                LocalDateTime.now(),
                120,
                2000.0,
                source,
                dest,
                vehicleType,
                false,
                null,
                null,
                null
        );
    }

    @Test
    void getAllFlights_returnsList() {
        logger.info("Test: getAllFlights should return list of flights");
        when(flightRepository.findAll()).thenReturn(List.of(sampleFlight));

        List<Flight> result = flightService.getAllFlights();

        assertThat(result).hasSize(1);
        verify(flightRepository).findAll();
        logger.info("Test passed: getAllFlights returned {} flight(s)", result.size());
    }

    @Test
    void getFlightByNumber_existingFlight_returnsFlight() {
        when(flightRepository.findByFlightNumber("TK1234")).thenReturn(Optional.of(sampleFlight));

        Flight result = flightService.getFlightByNumber("TK1234");

        assertThat(result).isNotNull();
        assertThat(result.getFlightNumber()).isEqualTo("TK1234");
    }

    @Test
    void getFlightByNumber_nonExistingFlight_returnsNull() {
        when(flightRepository.findByFlightNumber(anyString())).thenReturn(Optional.empty());

        Flight result = flightService.getFlightByNumber("UNKNOWN");

        assertThat(result).isNull();
    }

    @Test
    void searchFlights_whenSourceAirportProvided_usesAirportQuery() {
        when(flightRepository.findByAirportCode("IST")).thenReturn(List.of(sampleFlight));

        List<Flight> result = flightService.searchFlights("IST", null, null, null, null);

        assertThat(result).hasSize(1);
        verify(flightRepository).findByAirportCode("IST");
    }

    @Test
    void searchFlights_whenVehicleTypeProvided_usesVehicleTypeQuery() {
        when(flightRepository.findByVehicleType("A320")).thenReturn(List.of(sampleFlight));

        List<Flight> result = flightService.searchFlights(null, null, "A320", null, null);

        assertThat(result).hasSize(1);
        verify(flightRepository).findByVehicleType("A320");
    }

    @Test
    void searchFlights_whenDateRangeProvided_usesDateRangeQuery() {
        when(flightRepository.findByFlightDateBetween(any(), any())).thenReturn(List.of(sampleFlight));

        List<Flight> result = flightService.searchFlights(
                null,
                null,
                null,
                "2024-01-01T00:00",
                "2024-12-31T23:59"
        );

        assertThat(result).hasSize(1);
        verify(flightRepository).findByFlightDateBetween(any(), any());
    }

    @Test
    void searchFlights_whenNoFiltersProvided_returnsAll() {
        when(flightRepository.findAll()).thenReturn(List.of(sampleFlight));

        List<Flight> result = flightService.searchFlights(null, null, null, null, null);

        assertThat(result).hasSize(1);
        verify(flightRepository).findAll();
    }

    @Test
    void createFlight_savesAndReturnsEntity() {
        when(flightRepository.save(sampleFlight)).thenReturn(sampleFlight);

        Flight result = flightService.createFlight(sampleFlight);

        assertThat(result).isEqualTo(sampleFlight);
        verify(flightRepository).save(sampleFlight);
    }

    @Test
    void updateFlight_existingFlight_updatesAndReturns() {
        when(flightRepository.findByFlightNumber("TK1234")).thenReturn(Optional.of(sampleFlight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Flight updatedDetails = new Flight(
                "TK1234",
                LocalDateTime.now().plusDays(1),
                140,
                2200.0,
                sampleFlight.getSourceAirport(),
                sampleFlight.getDestinationAirport(),
                sampleFlight.getVehicleType(),
                true,
                "TK5678",
                "Turkish Airlines",
                "TK9999"
        );

        Flight result = flightService.updateFlight("TK1234", updatedDetails);

        assertThat(result.getDurationMinutes()).isEqualTo(140);
        assertThat(result.getIsSharedFlight()).isTrue();
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void updateFlight_nonExistingFlight_returnsNull() {
        when(flightRepository.findByFlightNumber("UNKNOWN")).thenReturn(Optional.empty());

        Flight result = flightService.updateFlight("UNKNOWN", sampleFlight);

        assertThat(result).isNull();
    }

    @Test
    void deleteFlight_existingFlight_deletesAndReturnsTrue() {
        when(flightRepository.findByFlightNumber("TK1234")).thenReturn(Optional.of(sampleFlight));

        boolean deleted = flightService.deleteFlight("TK1234");

        assertThat(deleted).isTrue();
        verify(flightRepository).delete(sampleFlight);
    }

    @Test
    void deleteFlight_nonExistingFlight_returnsFalse() {
        when(flightRepository.findByFlightNumber("UNKNOWN")).thenReturn(Optional.empty());

        boolean deleted = flightService.deleteFlight("UNKNOWN");

        assertThat(deleted).isFalse();
    }
}

