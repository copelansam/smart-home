package com.example.smarthome.controller;


import com.example.smarthome.simulation.SimulationSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationSettings simulationSettings;

    public SimulationController(SimulationSettings simulationSettings){
        this.simulationSettings = simulationSettings;
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
}
