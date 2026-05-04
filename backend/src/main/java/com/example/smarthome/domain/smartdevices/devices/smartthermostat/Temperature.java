package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

import jakarta.persistence.Transient;

import java.util.Random;

/**
 * Represents a temperature value in degrees Fahrenheit.
 *
 * <p>
 * This class is a simple mutable value object used to model and update
 * environmental or device temperature readings within the smart home system.
 * </p>
 *
 * <p>
 * Temperature values can be incremented or decremented over time to support
 * simulation and automated device behavior (e.g., thermostat adjustments).
 * </p>
 */public class Temperature{

    /** Current temperature value in Fahrenheit. */
    private double temperature;

    public Temperature(){}

    /**
     * Creates a temperature with an initial value.
     *
     * @param temperature initial temperature in Fahrenheit
     */
    public Temperature(double temperature){
        this.temperature = temperature;
    }

    /**
     * @return current temperature in Fahrenheit
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Adjusts the current temperature by a delta value.
     *
     * <p>
     * Positive values increase temperature, negative values decrease it.
     * This method is primarily used for simulation and thermostat automation.
     * </p>
     *
     * @param amount change in temperature (can be positive or negative)
     */
    public void updateTemperature(double amount){
        this.temperature += amount;
    }
}
