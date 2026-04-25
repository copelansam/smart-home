package com.example.smarthome.controller;


import com.example.smarthome.service.SmartDeviceService;
import com.example.smarthome.simulation.SimulationSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationSettings simulationSettings;
    private final SmartDeviceService deviceService;

    public SimulationController(SimulationSettings simulationSettings, SmartDeviceService deviceService){
        this.simulationSettings = simulationSettings;
        this.deviceService = deviceService;
    }

    @PostMapping("/speed")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateSimulationRate(@RequestParam double timeMultiplier){

        if (timeMultiplier <= 0){
            return ResponseEntity.badRequest().body("Multiplier must be greater than zero.");
        }
        else{
            simulationSettings.setTimeMultiplier(timeMultiplier);
            return ResponseEntity.ok("Simulation speed updated to: " + timeMultiplier + "x");
        }
    }

    @PostMapping("/location/temperature")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> updateAmbientTemperatureByLocation(
            @RequestParam(required = true) String location,
            @RequestParam(required = true) double ambientTemperature){

        if (location.trim().isEmpty()){
            return ResponseEntity.badRequest().body("Please enter a location");
        }
        else{
            String result = deviceService.updateLocationAmbientTemperature(location, ambientTemperature);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/reset")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String,String>> factoryResetAllDevices(){

        int devicesReset = deviceService.resetAllDevices();
        return ResponseEntity.ok(Map.of(
                "message","All " + devicesReset + " device(s) successfully reset to their factory conditions"));

    }
}
