package com.example.smarthome.integration;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.repository.ISmartDeviceRepository;
import com.example.smarthome.simulation.ThermostatResult;
import com.example.smarthome.service.ThermostatSimulationService;
import com.example.smarthome.simulation.strategies.CoolingStrategy;
import com.example.smarthome.simulation.strategies.HeatingStrategy;
import com.example.smarthome.simulation.strategies.IdleStrategy;
import com.example.smarthome.simulation.strategies.ThermostatStrategyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.smarthome.api.request.DeviceCreationRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests covering:
 * - API contract: correct status codes and response shapes for all endpoints
 * - Persistence round-trip: device state survives write and read-back
 * - Thermostat simulation: temperature moves toward desired, transitions to Idle on arrival
 *
 * Uses @SpringBootTest to load the full application context with an in-memory SQLite database.
 * @DirtiesContext resets the database between test classes so tests are fully isolated.
 */
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SmartHomeIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired
    private ISmartDeviceRepository deviceRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================================================================
    // HELPERS
    // =========================================================================

    @BeforeEach
    void clearDatabase() {
        // delete logs first if they have a FK to devices
        // logRepository.deleteAll();
        deviceRepository.deleteAll();
    }

    /**
     * Creates a device via the API and returns its UUID string.
     */
    private String createDeviceAndGetId(String name, String location, DeviceType type) throws Exception {
        DeviceCreationRequest request = new DeviceCreationRequest(name, location, type);

        MvcResult result = mockMvc.perform(post("/api/devices/create-device")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Fetch the created device from GET /api/devices and find by name
        MvcResult listResult = mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andReturn();

        // Parse UUID from the device list by matching on name
        var devices = objectMapper.readTree(listResult.getResponse().getContentAsString());
        for (var device : devices) {
            if (device.get("name").asText().equals(name)) {
                return device.get("uuid").asText();
            }
        }
        throw new IllegalStateException("Created device not found in list: " + name);
    }

    // =========================================================================
    // API CONTRACT — GET /api/devices
    // =========================================================================

    @Nested
    @DisplayName("GET /api/devices")
    class GetDevicesTests {

        @Test
        @DisplayName("Returns 200 and an empty array when no devices exist")
        void getDevices_empty_returns200EmptyArray() throws Exception {
            mockMvc.perform(get("/api/devices"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Returns 200 and a list containing created devices")
        void getDevices_withDevices_returns200WithList() throws Exception {
            createDeviceAndGetId("Test Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(get("/api/devices"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("Test Light")))
                    .andExpect(jsonPath("$[0].location", is("Office")))
                    .andExpect(jsonPath("$[0].deviceType", is("LIGHT")));
        }

        @Test
        @DisplayName("Response shape contains all required DeviceDTO fields")
        void getDevices_responseShape_containsAllFields() throws Exception {
            createDeviceAndGetId("Shape Light", "Hall", DeviceType.LIGHT);

            mockMvc.perform(get("/api/devices"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].uuid", notNullValue()))
                    .andExpect(jsonPath("$[0].name", notNullValue()))
                    .andExpect(jsonPath("$[0].location", notNullValue()))
                    .andExpect(jsonPath("$[0].deviceType", notNullValue()))
                    .andExpect(jsonPath("$[0].state", notNullValue()))
                    .andExpect(jsonPath("$[0].isOn", notNullValue()))
                    .andExpect(jsonPath("$[0].materialIcon", notNullValue()))
                    .andExpect(jsonPath("$[0].availableTransitions", notNullValue()))
                    .andExpect(jsonPath("$[0].updatableFields", notNullValue()))
                    .andExpect(jsonPath("$[0].properties", notNullValue()));
        }

        @Test
        @DisplayName("Filter by device type returns only matching devices")
        void getDevices_filterByType_returnsOnlyMatchingType() throws Exception {
            createDeviceAndGetId("My Light", "Kitchen", DeviceType.LIGHT);
            createDeviceAndGetId("My Fan", "Kitchen", DeviceType.FAN);

            mockMvc.perform(get("/api/devices").param("type", "LIGHT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].deviceType", is("LIGHT")));
        }

        @Test
        @DisplayName("Filter by location returns only matching devices")
        void getDevices_filterByLocation_returnsOnlyMatchingLocation() throws Exception {
            createDeviceAndGetId("Kitchen Light", "Kitchen", DeviceType.LIGHT);
            createDeviceAndGetId("Bedroom Light", "Bedroom", DeviceType.LIGHT);

            mockMvc.perform(get("/api/devices").param("location", "Kitchen"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].location", is("Kitchen")));
        }

        @Test
        @DisplayName("Filter by isOn=false returns only powered-off devices")
        void getDevices_filterByIsOn_returnsOnlyOffDevices() throws Exception {
            createDeviceAndGetId("Off Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(get("/api/devices").param("isOn", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].isOn", is(false)));
        }
    }

    // =========================================================================
    // API CONTRACT — GET /api/devices/{id}
    // =========================================================================

    @Nested
    @DisplayName("GET /api/devices/{id}")
    class GetDeviceByIdTests {

        @Test
        @DisplayName("Returns 200 and the device when UUID exists")
        void getDeviceById_found_returns200() throws Exception {
            String uuid = createDeviceAndGetId("My Fan", "Bedroom", DeviceType.FAN);

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uuid", is(uuid)))
                    .andExpect(jsonPath("$.name", is("My Fan")))
                    .andExpect(jsonPath("$.deviceType", is("FAN")));
        }

        @Test
        @DisplayName("Returns 404 when UUID does not exist")
        void getDeviceById_notFound_returns404() throws Exception {
            UUID randomUuid = UUID.randomUUID();

            mockMvc.perform(get("/api/devices/" + randomUuid))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // API CONTRACT — POST /api/devices/create-device
    // =========================================================================

    @Nested
    @DisplayName("POST /api/devices/create-device")
    class CreateDeviceTests {

        @Test
        @DisplayName("Returns 200 and success CallResult when request is valid")
        void createDevice_valid_returns200() throws Exception {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "Living Room Light", "Living Room", DeviceType.LIGHT);

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess", is(true)));
        }

        @Test
        @DisplayName("Returns 400 when name is blank")
        void createDevice_blankName_returns400() throws Exception {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "", "Living Room", DeviceType.LIGHT);

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 400 when location is blank")
        void createDevice_blankLocation_returns400() throws Exception {
            DeviceCreationRequest request = new DeviceCreationRequest(
                    "My Light", "", DeviceType.LIGHT);

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 400 when deviceType is null")
        void createDevice_nullDeviceType_returns400() throws Exception {
            // Send raw JSON with null deviceType to bypass record validation
            String json = "{\"name\":\"My Light\",\"location\":\"Office\",\"deviceType\":null}";

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Invariant: returns 409 when creating a second thermostat in same location")
        void createDevice_duplicateThermostat_returns409() throws Exception {
            DeviceCreationRequest first = new DeviceCreationRequest(
                    "Thermostat 1", "Hallway", DeviceType.THERMOSTAT);
            DeviceCreationRequest second = new DeviceCreationRequest(
                    "Thermostat 2", "Hallway", DeviceType.THERMOSTAT);

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(first)))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(second)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Two thermostats in different locations are both created successfully")
        void createDevice_thermostats_differentLocations_bothSucceed() throws Exception {
            DeviceCreationRequest t1 = new DeviceCreationRequest(
                    "Thermostat 1", "Hallway", DeviceType.THERMOSTAT);
            DeviceCreationRequest t2 = new DeviceCreationRequest(
                    "Thermostat 2", "Bedroom", DeviceType.THERMOSTAT);

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(t1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess", is(true)));

            mockMvc.perform(post("/api/devices/create-device")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(t2)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess", is(true)));
        }
    }

    // =========================================================================
    // API CONTRACT — DELETE /api/devices/{id}
    // =========================================================================

    @Nested
    @DisplayName("DELETE /api/devices/{id}")
    class DeleteDeviceTests {

        @Test
        @DisplayName("Returns 204 when device is successfully deleted")
        void deleteDevice_found_returns204() throws Exception {
            String uuid = createDeviceAndGetId("Temp Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(delete("/api/devices/" + uuid))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Device is no longer retrievable after deletion")
        void deleteDevice_deviceGoneAfterDeletion() throws Exception {
            String uuid = createDeviceAndGetId("Temp Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(delete("/api/devices/" + uuid))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Returns 404 when UUID does not exist")
        void deleteDevice_notFound_returns404() throws Exception {
            UUID randomUuid = UUID.randomUUID();

            mockMvc.perform(delete("/api/devices/" + randomUuid))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // API CONTRACT — PUT /api/devices/{id}/state
    // =========================================================================

    @Nested
    @DisplayName("PUT /api/devices/{id}/state")
    class ExecuteActionTests {

        @Test
        @DisplayName("Returns 200 and success result on valid transition")
        void executeAction_valid_returns200() throws Exception {
            String uuid = createDeviceAndGetId("Action Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_LIGHT_ON")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess", is(true)));
        }

        @Test
        @DisplayName("Returns 400 or 409 on invalid state transition")
        void executeAction_invalidTransition_returnsError() throws Exception {
            String uuid = createDeviceAndGetId("Action Light", "Office", DeviceType.LIGHT);
            // TURN_LIGHT_OFF from Off state is invalid
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_LIGHT_OFF")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Returns 404 when device UUID does not exist")
        void executeAction_deviceNotFound_returns404() throws Exception {
            UUID randomUuid = UUID.randomUUID();

            mockMvc.perform(put("/api/devices/" + randomUuid + "/state")
                            .param("action", "TURN_LIGHT_ON")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Device state is updated correctly after valid transition")
        void executeAction_stateUpdatedAfterTransition() throws Exception {
            String uuid = createDeviceAndGetId("State Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_LIGHT_ON")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state", is("Light On")))
                    .andExpect(jsonPath("$.isOn", is(true)));
        }

        @Test
        @DisplayName("Light brightness update persists correctly")
        void executeAction_updateBrightness_persists() throws Exception {
            String uuid = createDeviceAndGetId("Bright Light", "Office", DeviceType.LIGHT);

            // Turn on first
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_LIGHT_ON")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());

            // Update brightness
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "UPDATE_BRIGHTNESS")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"brightnessPercentage\": 50}"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.properties.brightnessPercentage", is(50)));
        }
    }

    // =========================================================================
    // API CONTRACT — GET /api/devices/{id}/history
    // =========================================================================

    @Nested
    @DisplayName("GET /api/devices/{id}/history")
    class DeviceHistoryTests {

        @Test
        @DisplayName("Returns 200 and logs after device creation")
        void getHistory_afterCreation_returnsLogs() throws Exception {
            String uuid = createDeviceAndGetId("Log Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(get("/api/devices/" + uuid + "/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("Returns 404 when device UUID does not exist")
        void getHistory_notFound_returns404() throws Exception {
            mockMvc.perform(get("/api/devices/" + UUID.randomUUID() + "/history"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("New log entry is added after a state transition")
        void getHistory_afterTransition_hasAdditionalLog() throws Exception {
            String uuid = createDeviceAndGetId("Log Light", "Office", DeviceType.LIGHT);

            // Get baseline log count
            MvcResult before = mockMvc.perform(get("/api/devices/" + uuid + "/history"))
                    .andReturn();
            int countBefore = objectMapper.readTree(before.getResponse().getContentAsString()).size();

            // Perform a transition
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_LIGHT_ON")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());

            // Verify log count increased
            mockMvc.perform(get("/api/devices/" + uuid + "/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThan(countBefore))));
        }
    }

    // =========================================================================
    // API CONTRACT — POST /api/simulation endpoints
    // =========================================================================

    @Nested
    @DisplayName("POST /api/simulation")
    class SimulationControllerTests {

        @Test
        @DisplayName("POST /api/simulation/speed returns 204 with valid multiplier")
        void updateSimulationSpeed_valid_returns204() throws Exception {
            mockMvc.perform(post("/api/simulation/speed")
                            .param("timeMultiplier", "2.0"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /api/simulation/speed returns 400 with non-positive multiplier")
        void updateSimulationSpeed_nonPositive_returns400() throws Exception {
            mockMvc.perform(post("/api/simulation/speed")
                            .param("timeMultiplier", "0"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/simulation/reset returns 204 when devices exist")
        void resetAllDevices_withDevices_returns204() throws Exception {
            createDeviceAndGetId("Reset Light", "Office", DeviceType.LIGHT);

            mockMvc.perform(post("/api/simulation/reset"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /api/simulation/reset returns 404 when no devices exist")
        void resetAllDevices_noDevices_returns404() throws Exception {
            mockMvc.perform(post("/api/simulation/reset"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/simulation/location/temperature returns 204 when thermostat exists in location")
        void updateLocationTemperature_valid_returns204() throws Exception {
            createDeviceAndGetId("Hall Thermostat", "Hallway", DeviceType.THERMOSTAT);

            String body = "{\"location\":\"Hallway\",\"temperature\":68.0}";
            mockMvc.perform(post("/api/simulation/location/temperature")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("POST /api/simulation/location/temperature returns 404 when no thermostat in location")
        void updateLocationTemperature_noThermostat_returns404() throws Exception {
            String body = "{\"location\":\"Garage\",\"temperature\":68.0}";
            mockMvc.perform(post("/api/simulation/location/temperature")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // PERSISTENCE ROUND-TRIP
    // =========================================================================

    @Nested
    @DisplayName("Persistence round-trip")
    class PersistenceTests {

        @Test
        @DisplayName("Light: state and brightness survive write and read-back")
        void persistence_light_stateAndBrightnessSurvive() throws Exception {
            String uuid = createDeviceAndGetId("Persist Light", "Office", DeviceType.LIGHT);

            // Turn on and update brightness
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_LIGHT_ON")
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isOk());

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "UPDATE_BRIGHTNESS")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"brightnessPercentage\": 42}"))
                    .andExpect(status().isOk());

            // Read back and verify
            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state", is("Light On")))
                    .andExpect(jsonPath("$.isOn", is(true)))
                    .andExpect(jsonPath("$.properties.brightnessPercentage", is(42)));
        }

        @Test
        @DisplayName("Fan: state and speed survive write and read-back")
        void persistence_fan_stateAndSpeedSurvive() throws Exception {
            String uuid = createDeviceAndGetId("Persist Fan", "Bedroom", DeviceType.FAN);

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "TURN_FAN_ON")
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isOk());

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "UPDATE_SPEED")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"speed\": \"HIGH\"}"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state", is("Fan On")))
                    .andExpect(jsonPath("$.isOn", is(true)))
                    .andExpect(jsonPath("$.properties.speed", is("High")));
        }

        @Test
        @DisplayName("Thermostat: desired temperature survives write and read-back")
        void persistence_thermostat_desiredTempSurvives() throws Exception {
            String uuid = createDeviceAndGetId("Persist Thermostat", "Hallway", DeviceType.THERMOSTAT);

            // Update desired temp while off (ThermostatOffState allows this)
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "UPDATE_DESIRED_TEMP")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"desiredTemp\": 72.0}"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.properties.desiredTemperature", is(72.0)));
        }

        @Test
        @DisplayName("Door lock: locked state survives write and read-back")
        void persistence_doorLock_lockedStateSurvives() throws Exception {
            String uuid = createDeviceAndGetId("Persist Lock", "Entrance", DeviceType.DOORLOCK);

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                            .param("action", "LOCK")
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state", is("Door Locked")));
        }

        @Test
        @DisplayName("Device persists correctly after multiple sequential transitions")
        void persistence_multipleTransitions_finalStateSurvives() throws Exception {
            String uuid = createDeviceAndGetId("Multi Light", "Kitchen", DeviceType.LIGHT);

            // On → update brightness → off → on again
            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                    .param("action", "TURN_LIGHT_ON")
                    .contentType(MediaType.APPLICATION_JSON).content("{}"));

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                    .param("action", "UPDATE_BRIGHTNESS")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"brightnessPercentage\": 30}"));

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                    .param("action", "TURN_LIGHT_OFF")
                    .contentType(MediaType.APPLICATION_JSON).content("{}"));

            mockMvc.perform(put("/api/devices/" + uuid + "/state")
                    .param("action", "TURN_LIGHT_ON")
                    .contentType(MediaType.APPLICATION_JSON).content("{}"));

            mockMvc.perform(get("/api/devices/" + uuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state", is("Light On")))
                    .andExpect(jsonPath("$.properties.brightnessPercentage", is(30)));
        }
    }

    // =========================================================================
    // THERMOSTAT SIMULATION
    // =========================================================================

    @Nested
    @DisplayName("Thermostat simulation")
    class ThermostatSimulationTests {

        private ThermostatSimulationService buildSimulationService() {
            HeatingStrategy heatingStrategy = new HeatingStrategy();
            CoolingStrategy coolingStrategy = new CoolingStrategy();
            IdleStrategy idleStrategy = new IdleStrategy();
            ThermostatStrategyFactory factory = new ThermostatStrategyFactory(
                    heatingStrategy, coolingStrategy, idleStrategy);
            return new ThermostatSimulationService(factory);
        }

        @Test
        @DisplayName("Ambient temperature increases toward desired when heating")
        void simulation_heating_ambientIncreasesEachCycle() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(65.0);
            thermostat.setDesiredTemperature(70.0);

            double before = thermostat.getAmbientTemperature().temperature();
            simulation.evaluate(thermostat);
            double after = thermostat.getAmbientTemperature().temperature();

            assertTrue(after > before,
                    "Ambient temperature should increase during heating cycle");
        }

        @Test
        @DisplayName("Thermostat transitions to Heating state when ambient is below desired")
        void simulation_heating_transitionsToHeatingState() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(65.0);
            thermostat.setDesiredTemperature(70.0);

            simulation.evaluate(thermostat);

            assertEquals("Thermostat Heating", thermostat.getState());
        }

        @Test
        @DisplayName("Thermostat transitions to Idle when ambient reaches desired temperature")
        void simulation_heating_transitionsToIdleWhenTargetReached() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setDesiredTemperature(70.0);

            // Run simulation until ambient reaches desired (or max 20 cycles)
            int maxCycles = 20;
            for (int i = 0; i < maxCycles; i++) {
                simulation.evaluate(thermostat);
                double diff = Math.abs(
                        thermostat.getAmbientTemperature().temperature() -
                                thermostat.getDesiredTemperature().temperature());
                if (diff < ThermostatSimulationService.TEMP_THRESHOLD) break;
            }

            simulation.evaluate(thermostat); // one final cycle to trigger idle
            assertEquals("Thermostat Idle", thermostat.getState(),
                    "Thermostat should be Idle once ambient reaches desired temperature");
        }

        @Test
        @DisplayName("Ambient temperature decreases toward desired when cooling")
        void simulation_cooling_ambientDecreasesEachCycle() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(78.0);
            thermostat.setDesiredTemperature(70.0);

            double before = thermostat.getAmbientTemperature().temperature();
            simulation.evaluate(thermostat);
            double after = thermostat.getAmbientTemperature().temperature();

            assertTrue(after < before,
                    "Ambient temperature should decrease during cooling cycle");
        }

        @Test
        @DisplayName("Thermostat transitions to Cooling state when ambient is above desired")
        void simulation_cooling_transitionsToCoolingState() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(78.0);
            thermostat.setDesiredTemperature(70.0);

            simulation.evaluate(thermostat);

            assertEquals("Thermostat Cooling", thermostat.getState());
        }

        @Test
        @DisplayName("Thermostat stays Idle when ambient is already at desired temperature")
        void simulation_idle_staysIdleWhenAtDesiredTemp() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(70.0);
            thermostat.setDesiredTemperature(70.0);

            simulation.evaluate(thermostat);

            assertEquals("Thermostat Idle", thermostat.getState());
        }

        @Test
        @DisplayName("No simulation occurs when thermostat is Off")
        void simulation_off_noChanges() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            // stays Off — factoryReset ambient is 60, desired is 75
            double ambientBefore = thermostat.getAmbientTemperature().temperature();

            ThermostatResult result = simulation.evaluate(thermostat);

            assertEquals(ambientBefore, thermostat.getAmbientTemperature().temperature(),
                    "Ambient temperature should not change when thermostat is Off");
            assertEquals("Thermostat Off", thermostat.getState());
        }

        @Test
        @DisplayName("HEAT mode only heats — does not cool even when ambient is above desired")
        void simulation_heatMode_doesNotCoolWhenAboveDesired() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(78.0);
            thermostat.setDesiredTemperature(70.0);
            thermostat.setMode(com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode.HEAT);

            // In HEAT mode with ambient > desired, no strategy matches except idle
            // so the factory should throw IllegalStateException or return idle
            // Either way, temperature should not decrease
            double ambientBefore = thermostat.getAmbientTemperature().temperature();
            try {
                simulation.evaluate(thermostat);
            } catch (IllegalStateException e) {
                // acceptable — no strategy matched
            }
            double ambientAfter = thermostat.getAmbientTemperature().temperature();
            assertFalse(ambientAfter < ambientBefore,
                    "Thermostat in HEAT mode should not cool");
        }

        @Test
        @DisplayName("COOL mode only cools — does not heat even when ambient is below desired")
        void simulation_coolMode_doesNotHeatWhenBelowDesired() {
            ThermostatSimulationService simulation = buildSimulationService();
            SmartThermostat thermostat = new SmartThermostat("T1", "Hallway");
            thermostat.execute("POWER_THERMOSTAT_ON", Map.of());
            thermostat.setAmbientTemperature(65.0);
            thermostat.setDesiredTemperature(70.0);
            thermostat.setMode(com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode.COOL);

            double ambientBefore = thermostat.getAmbientTemperature().temperature();
            try {
                simulation.evaluate(thermostat);
            } catch (IllegalStateException e) {
                // acceptable — no strategy matched
            }
            double ambientAfter = thermostat.getAmbientTemperature().temperature();
            assertFalse(ambientAfter > ambientBefore,
                    "Thermostat in COOL mode should not heat");
        }
    }
}