package com.example.smarthome.controller;


import com.example.smarthome.service.SmartDeviceService;
import com.example.smarthome.simulation.SimulationSettings;
import com.example.smarthome.simulation.TempRequest;
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

    public SimulationController(SimulationSettings simulationSettings, SmartDeviceService deviceService){
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
    public ResponseEntity<String> updateSimulationRate(@RequestParam double timeMultiplier){

        if (timeMultiplier <= 0){
            return ResponseEntity.badRequest().body("Multiplier must be greater than zero.");
        }
        else{
            simulationSettings.setTimeMultiplier(timeMultiplier);
            System.out.println("The speed has been changed to: " + timeMultiplier + "x");
            return ResponseEntity.ok("Simulation speed updated to: " + timeMultiplier + "x");
        }
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
            @RequestBody TempRequest tempRequest){

        // Make sure that a location has been specified, if not return an error
        if (tempRequest.location.trim().isEmpty()){
            return ResponseEntity.badRequest().body("Please enter a location");
        }
        else{ // Otherwise, pass the request to the service

            String result = deviceService.updateLocationAmbientTemperature(tempRequest.location, tempRequest.temperature);

            // return result
            return ResponseEntity.ok(result);
        }
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
