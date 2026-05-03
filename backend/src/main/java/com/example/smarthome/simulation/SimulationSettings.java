package com.example.smarthome.simulation;

import org.springframework.stereotype.Component;

/***
 * Holds simulation data for the smart home simulation timing system
 *
 * Controls how fast simulated time progresses relative to real time based on a configurable time multiplier
 */
@Component
public class SimulationSettings {

    /**
     * Controls the speed of the simulation relative to real time.
     *
     * A value of 1.0 means real-time speed.
     * Values > 1 speed up simulation, values < 1 slow it down.
     */
    private double timeMultiplier;

    /**
     * Base time constant used in simulation calculations.
     *
     * Represents the number of real seconds per simulated unit
     * before applying the time multiplier.
     */
    private final double baseTime = 5.0;

    /***
     * Initializes the simulation with a default time multiplier of 1.0
     */
    public SimulationSettings(){
        this.timeMultiplier = 1;
    }

    /***
     * Updates the simulation speed with a time multiplier specified by the client
     *
     * @param timeMultiplier the speed factor for simulation time progression
     * @throws IllegalArgumentException if time multiplier is <= 0
     */
    public void setTimeMultiplier(double timeMultiplier) {

        if (timeMultiplier <= 0){
            throw new IllegalArgumentException("Enter a multiplier greater than 0.");
        }

        this.timeMultiplier = timeMultiplier;
    }

    /***
     * Calculates the number of real seconds needed to simulate 1 degree in the simulation
     *
     * @return seconds per degree based on current time multiplier
     */
    public double calculateSecondsPerDegree(){

        return baseTime / timeMultiplier;
    }
}
