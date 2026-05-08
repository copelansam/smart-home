package com.example.smarthome;

import com.example.smarthome.controller.DeviceCreationRequest;
import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devicefactories.ISmartDeviceFactory;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.devicequeries.QueryBuilder;
import com.example.smarthome.exception.DeviceNotFoundException;
import com.example.smarthome.exception.InvalidStateTransitionException;
import com.example.smarthome.exception.NoDevicesException;
import com.example.smarthome.exception.ThermostatAlreadyExistsException;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.repository.ISmartDeviceRepository;
import com.example.smarthome.service.SmartDeviceService;
import com.example.smarthome.simulation.ThermostatResult;
import com.example.smarthome.simulation.ThermostatSimulationService;
import com.example.smarthome.simulation.strategies.IThermostatStrategy;
import com.example.smarthome.simulation.strategies.ThermostatStrategyFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SmartDeviceService and ThermostatSimulationService.
 *
 * All dependencies (repository, factory, messaging, etc.) are mocked so that
 * business rules are tested independently of HTTP, persistence, and WebSocket concerns.
 */
@ExtendWith(MockitoExtension.class)
public class SmartDeviceServiceTests {

    // =========================================================================
    // SMART DEVICE SERVICE TESTS
    // =========================================================================

    @Nested
    @DisplayName("SmartDeviceService")
    class SmartDeviceServiceTest {

        @Mock private ISmartDeviceRepository repo;
        @Mock private ISmartDeviceFactory deviceFactory;
        @Mock private QueryBuilder queryBuilder;
        @Mock private DeviceLogRepository deviceLogRepository;
        @Mock private SimpMessagingTemplate messagingTemplate;

        @InjectMocks
        private SmartDeviceService service;

        // --- makeLocationPretty ---

        @Test
        @DisplayName("makeLocationPretty: converts to Title Case")
        void makeLocationPretty_titleCase() {
            assertEquals("Living Room", service.makeLocationPretty("living room"));
        }

        @Test
        @DisplayName("makeLocationPretty: trims leading and trailing whitespace")
        void makeLocationPretty_trimsWhitespace() {
            assertEquals("Kitchen", service.makeLocationPretty("  kitchen  "));
        }

        @Test
        @DisplayName("makeLocationPretty: normalizes mixed case input")
        void makeLocationPretty_normalizesMixedCase() {
            assertEquals("Master Bedroom", service.makeLocationPretty("mAsTeR bEdRoOm"));
        }

        @Test
        @DisplayName("makeLocationPretty: blank string throws IllegalArgumentException")
        void makeLocationPretty_blank_throws() {
            assertThrows(IllegalArgumentException.class, () -> service.makeLocationPretty("   "));
        }

        @Test
        @DisplayName("makeLocationPretty: null throws IllegalArgumentException")
        void makeLocationPretty_null_throws() {
            assertThrows(IllegalArgumentException.class, () -> service.makeLocationPretty(null));
        }

        // --- Invariant: thermostat location uniqueness ---

        @Test
        @DisplayName("Invariant: creating a second thermostat in same location throws ThermostatAlreadyExistsException")
        void createDevice_thermostat_duplicateLocation_throws() {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "Hall Thermostat", "hallway", DeviceType.THERMOSTAT);

            // Simulate that a thermostat already exists in "Hallway"
            when(repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, "Hallway"))
                    .thenReturn(true);

            assertThrows(ThermostatAlreadyExistsException.class,
                    () -> service.createDevice(request));

            // Factory should never be called — device must not be created
            verify(deviceFactory, never()).createDevice(any());
        }

        @Test
        @DisplayName("Invariant: creating a thermostat in a location with no existing thermostat succeeds")
        void createDevice_thermostat_uniqueLocation_succeeds() {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "Hall Thermostat", "hallway", DeviceType.THERMOSTAT);

            SmartThermostat newThermostat = new SmartThermostat("Hall Thermostat", "Hallway");

            when(repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, "Hallway"))
                    .thenReturn(false);
            when(deviceFactory.createDevice(any())).thenReturn(newThermostat);
            when(deviceLogRepository.save(any())).thenReturn(null);

            CallResult result = service.createDevice(request);

            assertTrue(result.getIsSuccess());
            verify(deviceFactory).createDevice(any());
            verify(repo).save(any());
        }

        @Test
        @DisplayName("Invariant: two thermostats in different locations are both allowed")
        void createDevice_thermostat_differentLocations_bothAllowed() {
            DeviceCreationRequest request1 = new DeviceCreationRequest(
                    "Hall Thermostat", "hallway", DeviceType.THERMOSTAT);
            DeviceCreationRequest request2 = new DeviceCreationRequest(
                    "Bedroom Thermostat", "bedroom", DeviceType.THERMOSTAT);

            SmartThermostat t1 = new SmartThermostat("Hall Thermostat", "Hallway");
            SmartThermostat t2 = new SmartThermostat("Bedroom Thermostat", "Bedroom");

            when(repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, "Hallway")).thenReturn(false);
            when(repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, "Bedroom")).thenReturn(false);
            when(deviceFactory.createDevice(any()))
                    .thenReturn(t1)
                    .thenReturn(t2);
            when(deviceLogRepository.save(any())).thenReturn(null);

            CallResult result1 = service.createDevice(request1);
            CallResult result2 = service.createDevice(request2);

            assertTrue(result1.getIsSuccess());
            assertTrue(result2.getIsSuccess());
        }

        // --- Device creation ---

        @Test
        @DisplayName("createDevice: location is normalized to Title Case before persistence")
        void createDevice_normalizesLocation() {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "My Light", "living room", DeviceType.LIGHT);

            SmartLight newLight = new SmartLight("My Light", "Living Room");

            when(deviceFactory.createDevice(any())).thenReturn(newLight);
            when(deviceLogRepository.save(any())).thenReturn(null);

            service.createDevice(request);

            // Capture the request actually passed to the factory and verify the location was normalized
            verify(deviceFactory).createDevice(argThat(r ->
                    r.location().equals("Living Room")));
        }

        @Test
        @DisplayName("createDevice: non-thermostat device skips location invariant check")
        void createDevice_nonThermostat_skipsInvariantCheck() {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "My Fan", "bedroom", DeviceType.FAN);

            SmartFan newFan = new SmartFan("My Fan", "Bedroom");

            when(deviceFactory.createDevice(any())).thenReturn(newFan);
            when(deviceLogRepository.save(any())).thenReturn(null);

            service.createDevice(request);

            // existsByDeviceTypeAndLocation should never be checked for non-thermostat types
            verify(repo, never()).existsByDeviceTypeAndLocation(any(), any());
        }

        // --- Device retrieval ---

        @Test
        @DisplayName("getDeviceById: returns device when UUID exists")
        void getDeviceById_found_returnsDevice() {
            UUID uuid = UUID.randomUUID();
            SmartLight light = new SmartLight("Test Light", "Office");

            when(repo.findById(uuid)).thenReturn(Optional.of(light));

            ISmartDevice result = service.getDeviceById(uuid);
            assertEquals(light, result);
        }

        @Test
        @DisplayName("getDeviceById: throws DeviceNotFoundException when UUID does not exist")
        void getDeviceById_notFound_throws() {
            UUID uuid = UUID.randomUUID();
            when(repo.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(DeviceNotFoundException.class, () -> service.getDeviceById(uuid));
        }

        // --- Device deletion ---

        @Test
        @DisplayName("deleteDeviceById: deletes device and saves log when UUID exists")
        void deleteDeviceById_found_deletesAndLogs() {
            UUID uuid = UUID.randomUUID();
            when(repo.existsById(uuid)).thenReturn(true);

            service.deleteDeviceById(uuid);

            verify(repo).deleteById(uuid);
            verify(deviceLogRepository).save(any(DeviceLog.class));
        }

        @Test
        @DisplayName("deleteDeviceById: throws DeviceNotFoundException when UUID does not exist")
        void deleteDeviceById_notFound_throws() {
            UUID uuid = UUID.randomUUID();
            when(repo.existsById(uuid)).thenReturn(false);

            assertThrows(DeviceNotFoundException.class, () -> service.deleteDeviceById(uuid));
            verify(repo, never()).deleteById(any());
        }

        // --- executeAction ---

        @Test
        @DisplayName("executeAction: saves device and log on successful transition")
        void executeAction_success_savesDeviceAndLog() {
            UUID uuid = UUID.randomUUID();
            SmartLight light = new SmartLight("Test Light", "Office");

            when(repo.findById(any())).thenReturn(Optional.of(light));
            when(deviceLogRepository.save(any())).thenReturn(null);

            CallResult result = service.executeAction(uuid, "TURN_LIGHT_ON", Map.of());

            assertTrue(result.getIsSuccess());
            verify(repo).save(any());
            verify(deviceLogRepository).save(any());
        }

        @Test
        @DisplayName("executeAction: throws InvalidStateTransitionException on failed transition")
        void executeAction_failure_throwsException() {
            UUID uuid = UUID.randomUUID();
            SmartLight light = new SmartLight("Test Light", "Office");
            // Light starts Off — TURN_LIGHT_OFF is an invalid transition from Off state
            when(repo.findById(any())).thenReturn(Optional.of(light));

            assertThrows(InvalidStateTransitionException.class,
                    () -> service.executeAction(uuid, "TURN_LIGHT_OFF", Map.of()));

            // Device must NOT be saved on a failed transition
            verify(repo, never()).save(any());
        }

        // --- resetAllDevices ---

        @Test
        @DisplayName("resetAllDevices: resets all devices and saves a log for each")
        void resetAllDevices_resetsAll() {
            SmartLight light = new SmartLight("L1", "Office");
            SmartFan fan = new SmartFan("F1", "Bedroom");

            IDeviceQuery mockQuery = mock(IDeviceQuery.class);
            when(mockQuery.getItems()).thenReturn(List.of(light, fan));
            when(queryBuilder.buildQuery(null, null, null)).thenReturn(mockQuery);

            // Turn devices on first so reset is meaningful
            light.execute("TURN_LIGHT_ON", Map.of());
            fan.execute("TURN_FAN_ON", Map.of());

            service.resetAllDevices();

            assertFalse(light.getIsOn());
            assertFalse(fan.getIsOn());
            verify(deviceLogRepository, times(2)).save(any(DeviceLog.class));
        }

        @Test
        @DisplayName("resetAllDevices: throws NoDevicesException when there are no devices")
        void resetAllDevices_noDevices_throws() {
            IDeviceQuery mockQuery = mock(IDeviceQuery.class);
            when(mockQuery.getItems()).thenReturn(List.of());
            when(queryBuilder.buildQuery(null, null, null)).thenReturn(mockQuery);

            assertThrows(NoDevicesException.class, () -> service.resetAllDevices());
        }

        // --- updateLocationAmbientTemperature ---

        @Test
        @DisplayName("updateLocationAmbientTemperature: updates thermostat ambient temperature")
        void updateLocationAmbientTemperature_success() {
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");

            IDeviceQuery mockQuery = mock(IDeviceQuery.class);
            when(mockQuery.getItems()).thenReturn(List.of(thermostat));
            when(queryBuilder.buildQuery(DeviceType.THERMOSTAT, "Hallway", null)).thenReturn(mockQuery);

            service.updateLocationAmbientTemperature("Hallway", 72.0);

            assertEquals(72.0, thermostat.getAmbientTemperature().temperature());
            verify(repo).save(any());
            verify(deviceLogRepository).save(any(DeviceLog.class));
        }

        @Test
        @DisplayName("updateLocationAmbientTemperature: throws DeviceNotFoundException when no thermostat in location")
        void updateLocationAmbientTemperature_noThermostat_throws() {
            IDeviceQuery mockQuery = mock(IDeviceQuery.class);
            when(mockQuery.getItems()).thenReturn(List.of());
            when(queryBuilder.buildQuery(DeviceType.THERMOSTAT, "Garage", null)).thenReturn(mockQuery);

            assertThrows(DeviceNotFoundException.class,
                    () -> service.updateLocationAmbientTemperature("Garage", 70.0));
        }

        @Test
        @DisplayName("updateLocationAmbientTemperature: blank location throws IllegalArgumentException")
        void updateLocationAmbientTemperature_blankLocation_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.updateLocationAmbientTemperature("   ", 70.0));
        }

        // --- doesLocationHaveThermostat ---

        @Test
        @DisplayName("doesLocationHaveThermostat: returns true when thermostat exists in location")
        void doesLocationHaveThermostat_exists_returnsTrue() {
            when(repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, "Hallway"))
                    .thenReturn(true);
            assertTrue(service.doesLocationHaveThermostat("Hallway"));
        }

        @Test
        @DisplayName("doesLocationHaveThermostat: returns false when no thermostat in location")
        void doesLocationHaveThermostat_notExists_returnsFalse() {
            when(repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, "Garage"))
                    .thenReturn(false);
            assertFalse(service.doesLocationHaveThermostat("Garage"));
        }
    }

    // =========================================================================
    // THERMOSTAT SIMULATION SERVICE TESTS
    // =========================================================================

    @Nested
    @DisplayName("ThermostatSimulationService")
    class ThermostatSimulationServiceTest {

        @Mock private ThermostatStrategyFactory strategyFactory;
        @Mock private IThermostatStrategy mockStrategy;

        @InjectMocks
        private ThermostatSimulationService simulationService;

        @Test
        @DisplayName("evaluate: returns empty result when thermostat is Off")
        void evaluate_thermostatOff_returnsEmptyResult() {
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            // Thermostat starts in Off state by default

            ThermostatResult result = simulationService.evaluate(thermostat);

            assertNotNull(result);
            // Strategy factory must not be consulted when the thermostat is off
            verify(strategyFactory, never()).decideStrategy(any(), anyDouble());
        }

        @Test
        @DisplayName("evaluate: delegates to strategy when thermostat is on")
        void evaluate_thermostatOn_delegatesToStrategy() {
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());

            ThermostatResult expectedResult = new ThermostatResult();
            when(strategyFactory.decideStrategy(eq(thermostat), anyDouble()))
                    .thenReturn(mockStrategy);
            when(mockStrategy.apply(eq(thermostat), anyDouble(), anyDouble()))
                    .thenReturn(expectedResult);

            ThermostatResult result = simulationService.evaluate(thermostat);

            assertSame(expectedResult, result);
            verify(strategyFactory).decideStrategy(eq(thermostat), anyDouble());
            verify(mockStrategy).apply(eq(thermostat), anyDouble(), anyDouble());
        }

        @Test
        @DisplayName("evaluate: passes correct temperature difference to strategy factory")
        void evaluate_passesCorrectTempDifferenceToFactory() {
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(65.0);
            thermostat.setDesiredTemperature(70.0);
            // Expected diff: 65 - 70 = -5.0

            when(strategyFactory.decideStrategy(eq(thermostat), anyDouble()))
                    .thenReturn(mockStrategy);
            when(mockStrategy.apply(any(), anyDouble(), anyDouble()))
                    .thenReturn(new ThermostatResult());

            simulationService.evaluate(thermostat);

            verify(strategyFactory).decideStrategy(eq(thermostat), doubleThat(diff ->
                    Math.abs(diff - (-5.0)) < 0.001));
        }

        @Test
        @DisplayName("evaluate: thermostat in Heating state is not considered Off")
        void evaluate_thermostatHeating_isNotSkipped() {
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.execute("START_HEATING", Map.of());

            when(strategyFactory.decideStrategy(any(), anyDouble())).thenReturn(mockStrategy);
            when(mockStrategy.apply(any(), anyDouble(), anyDouble())).thenReturn(new ThermostatResult());

            simulationService.evaluate(thermostat);

            verify(strategyFactory).decideStrategy(any(), anyDouble());
        }

        @Test
        @DisplayName("TEMP_THRESHOLD constant is 1")
        void tempThreshold_isOne() {
            assertEquals(1, ThermostatSimulationService.TEMP_THRESHOLD);
        }
    }
}