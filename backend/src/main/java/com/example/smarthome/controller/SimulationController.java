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

/***
 * REST controller for controlling the smart house simulation settings
 *
 * provides endpoints to update the simulation speed, update a location's ambient temperature, and reset smart devices
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

    /***
     * Updates the speed of the thermostat temperature simulation
     *
     * @param timeMultiplier the multiplier by which the rate that the temperature changes will be multiplied
     * @return a response entity that says whether or not the speed was successfully updated
     */
    @PostMapping("/speed")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateSimulationRate(@RequestParam @Positive @NotNull double timeMultiplier) {

        simulationSettings.setTimeMultiplier(timeMultiplier);
        logger.info("Simulation speed changed to {}x", timeMultiplier);
        return ResponseEntity.ok("Simulation speed updated to: " + timeMultiplier + "x");

    }

    /***
     * Updates the ambient temperature of a specific location
     *
     * @param tempRequest an object that contains information needed to update a location's temperature (location and temperautre)
     * @return a response entity that says whether or not the location's temperature was successfully updated
     */
    @PostMapping("/location/temperature")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateAmbientTemperatureByLocation(
            @Valid @RequestBody TempRequest tempRequest) {

        // Pass the request values to the device service to update the location's temperature
        deviceService.updateLocationAmbientTemperature(tempRequest.getLocation(), tempRequest.getTemperature());

        // Return no content response
        return ResponseEntity.noContent().build();
    }

    /***
     * factory resets all smart devices
     *
     * @return a response entity that says whether or not it was successful
     */
    @PostMapping("/reset")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String,String>> factoryResetAllDevices(){

        // Call the service's reset device method, and store the amount of devices reset
        int devicesReset = deviceService.resetAllDevices();

        // Return result including how many devices were reset
        return ResponseEntity.ok(Map.of(
                "message","All " + devicesReset + " device(s) successfully reset to their factory conditions"));

    }
}
