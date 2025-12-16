package com.flightroster.pilot.unit.service;

import com.flightroster.pilot.entity.Pilot;
import com.flightroster.pilot.entity.SeniorityLevel;
import com.flightroster.pilot.repository.PilotRepository;
import com.flightroster.pilot.service.PilotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PilotServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(PilotServiceTest.class);

    @Mock
    private PilotRepository pilotRepository;

    @InjectMocks
    private PilotService pilotService;

    private Pilot samplePilot;

    @BeforeEach
    void setUp() {
        samplePilot = new Pilot(
                1L,
                "P123",
                "John Doe",
                40,
                "Male",
                "TR",
                "[\"EN\",\"TR\"]",
                "A320",
                3000.0,
                SeniorityLevel.SENIOR,
                true
        );
    }

    @Test
    void getAllPilots_returnsList() {
        logger.info("Test: getAllPilots should return list of pilots");
        when(pilotRepository.findAll()).thenReturn(List.of(samplePilot));

        List<Pilot> result = pilotService.getAllPilots();

        assertThat(result).hasSize(1);
        verify(pilotRepository).findAll();
        logger.info("Test passed: getAllPilots returned {} pilot(s)", result.size());
    }

    @Test
    void getAvailablePilots_returnsOnlyAvailable() {
        when(pilotRepository.findByIsAvailableTrue()).thenReturn(List.of(samplePilot));

        List<Pilot> result = pilotService.getAvailablePilots();

        assertThat(result).hasSize(1);
        verify(pilotRepository).findByIsAvailableTrue();
    }

    @Test
    void getPilotById_existing_returnsPilot() {
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(samplePilot));

        Pilot result = pilotService.getPilotById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPilotId()).isEqualTo("P123");
    }

    @Test
    void getPilotById_notExisting_returnsNull() {
        when(pilotRepository.findById(2L)).thenReturn(Optional.empty());

        Pilot result = pilotService.getPilotById(2L);

        assertThat(result).isNull();
    }

    @Test
    void getPilotByPilotId_existing_returnsPilot() {
        when(pilotRepository.findByPilotId("P123")).thenReturn(Optional.of(samplePilot));

        Pilot result = pilotService.getPilotByPilotId("P123");

        assertThat(result).isNotNull();
        assertThat(result.getPilotId()).isEqualTo("P123");
    }

    @Test
    void getPilotByPilotId_notExisting_returnsNull() {
        when(pilotRepository.findByPilotId(anyString())).thenReturn(Optional.empty());

        Pilot result = pilotService.getPilotByPilotId("UNKNOWN");

        assertThat(result).isNull();
    }

    @Test
    void getPilotsByVehicleType_returnsFilteredList() {
        when(pilotRepository.findByVehicleRestrictionAndAvailable("A320")).thenReturn(List.of(samplePilot));

        List<Pilot> result = pilotService.getPilotsByVehicleType("A320");

        assertThat(result).hasSize(1);
        verify(pilotRepository).findByVehicleRestrictionAndAvailable("A320");
    }

    @Test
    void getPilotsBySeniorityLevel_validLevel_returnsList() {
        when(pilotRepository.findBySeniorityLevelAndAvailable(SeniorityLevel.SENIOR))
                .thenReturn(List.of(samplePilot));

        List<Pilot> result = pilotService.getPilotsBySeniorityLevel("senior");

        assertThat(result).hasSize(1);
        verify(pilotRepository).findBySeniorityLevelAndAvailable(SeniorityLevel.SENIOR);
    }

    @Test
    void getPilotsBySeniorityLevel_invalidLevel_returnsEmptyList() {
        List<Pilot> result = pilotService.getPilotsBySeniorityLevel("invalid");

        assertThat(result).isEmpty();
    }

    @Test
    void getPilotsByMaxDistance_returnsList() {
        when(pilotRepository.findByMaxDistanceGreaterThanEqualAndAvailable(anyDouble()))
                .thenReturn(List.of(samplePilot));

        List<Pilot> result = pilotService.getPilotsByMaxDistance(1500.0);

        assertThat(result).hasSize(1);
        verify(pilotRepository).findByMaxDistanceGreaterThanEqualAndAvailable(1500.0);
    }

    @Test
    void createPilot_savesAndReturns() {
        when(pilotRepository.save(samplePilot)).thenReturn(samplePilot);

        Pilot result = pilotService.createPilot(samplePilot);

        assertThat(result).isEqualTo(samplePilot);
        verify(pilotRepository).save(samplePilot);
    }

    @Test
    void updatePilot_existing_updatesAndReturns() {
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(samplePilot));
        when(pilotRepository.save(any(Pilot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pilot updated = new Pilot(
                1L,
                "P123",
                "Jane Doe",
                35,
                "Female",
                "TR",
                "[\"EN\"]",
                "B737",
                3500.0,
                SeniorityLevel.JUNIOR,
                false
        );

        Pilot result = pilotService.updatePilot(1L, updated);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getIsAvailable()).isFalse();
        verify(pilotRepository).save(any(Pilot.class));
    }

    @Test
    void updatePilot_notExisting_returnsNull() {
        when(pilotRepository.findById(2L)).thenReturn(Optional.empty());

        Pilot result = pilotService.updatePilot(2L, samplePilot);

        assertThat(result).isNull();
    }

    @Test
    void deletePilot_existing_deletesAndReturnsTrue() {
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(samplePilot));

        boolean deleted = pilotService.deletePilot(1L);

        assertThat(deleted).isTrue();
        verify(pilotRepository).delete(samplePilot);
    }

    @Test
    void deletePilot_notExisting_returnsFalse() {
        when(pilotRepository.findById(2L)).thenReturn(Optional.empty());

        boolean deleted = pilotService.deletePilot(2L);

        assertThat(deleted).isFalse();
    }

    @Test
    void getPilotsByIds_returnsList() {
        when(pilotRepository.findAllById(List.of(1L))).thenReturn(List.of(samplePilot));

        List<Pilot> result = pilotService.getPilotsByIds(List.of(1L));

        assertThat(result).hasSize(1);
        verify(pilotRepository).findAllById(List.of(1L));
    }

    @Test
    void updateAvailability_existing_updatesFlag() {
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(samplePilot));
        when(pilotRepository.save(any(Pilot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pilot result = pilotService.updateAvailability(1L, false);

        assertThat(result.getIsAvailable()).isFalse();
        verify(pilotRepository).save(any(Pilot.class));
    }

    @Test
    void updateAvailability_notExisting_returnsNull() {
        when(pilotRepository.findById(2L)).thenReturn(Optional.empty());

        Pilot result = pilotService.updateAvailability(2L, false);

        assertThat(result).isNull();
    }

    @Test
    void updateBulkAvailability_updatesAll() {
        when(pilotRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(samplePilot, samplePilot));
        when(pilotRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Pilot> result = pilotService.updateBulkAvailability(List.of(1L, 2L), false);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsAvailable()).isFalse();
        verify(pilotRepository).saveAll(any());
    }

    @Test
    void setAllPilotsAvailable_updatesAll() {
        when(pilotRepository.findAll()).thenReturn(List.of(samplePilot));
        when(pilotRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Pilot> result = pilotService.setAllPilotsAvailable(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsAvailable()).isTrue();
        verify(pilotRepository).saveAll(any());
    }
}

