package com.example.smarthome.simulation;

import org.springframework.stereotype.Component;

@Component
public class SimulationSettings {

    private double timeMultiplier;

    private final double baseTime = 5.0;


    public SimulationSettings(){
        this.timeMultiplier = 1;
    }

    public void setTimeMultiplier(double timeMultiplier) {

        if (timeMultiplier <= 0){
            throw new IllegalArgumentException("Enter a multiplier greater than 0.");
        }

        this.timeMultiplier = timeMultiplier;
    }

    public double calculateSecondsPerDegree(){

        return baseTime / timeMultiplier;
    }
}
