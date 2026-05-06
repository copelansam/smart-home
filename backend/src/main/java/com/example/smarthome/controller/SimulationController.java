package com.example.smarthome.controller;

import com.example.smarthome.service.SmartDeviceService;
import com.example.smarthome.simulation.SimulationSettings;
import com.example.smarthome.simulation.TempRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for controlling smart home simulation settings.
 *
 * These endpoints are COMMAND operations that mutate simulation state.
 * They return HTTP 204 (No Content) when successful.
 */
@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationSettings simulationSettings;
    private final SmartDeviceService deviceService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SimulationController.class);

    public SimulationController(SimulationSettings simulationSettings, SmartDeviceService deviceService) {
        this.simulationSettings = simulationSettings;
        this.deviceService = deviceService;
    }

    /**
     * Updates the speed multiplier of the thermostat temperature simulation.
     *
     * This affects how quickly simulated temperatures change over time.
     *
     * @param timeMultiplier multiplier applied to simulation speed (> 0)
     * @return HTTP 204 No Content if the update is successful
     */
    @PostMapping("/speed")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> updateSimulationRate(@RequestParam @Positive @NotNull double timeMultiplier) {

        simulationSettings.setTimeMultiplier(timeMultiplier);
        logger.info("Simulation speed changed to {}x", timeMultiplier);
        return ResponseEntity.noContent().build();

    }

    /**
     * Updates the ambient temperature for a specific location in the simulation.
     *
     * This directly affects the thermostat associated with that location.
     *
     * @param tempRequest request containing location and temperature values
     * @return HTTP 204 No Content if the update is successful
     */
    @PostMapping("/location/temperature")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> updateAmbientTemperatureByLocation(
            @Valid @RequestBody TempRequest tempRequest) {

        // Pass the request values to the device service to update the location's temperature
        deviceService.updateLocationAmbientTemperature(tempRequest.getLocation(), tempRequest.getTemperature());

        // Return no content response
        return ResponseEntity.noContent().build();
    }

    /**
     * Resets all smart devices in the simulation to their factory default state.
     *
     * This clears runtime state and restores initial configuration.
     *
     * @return HTTP 204 No Content if reset is successful
     */
    @PostMapping("/reset")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> factoryResetAllDevices(){

        // Call the service's reset devices method
        deviceService.resetAllDevices();

        return ResponseEntity.noContent().build();
    }
}
