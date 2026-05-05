package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

/**
 * Represents a temperature value in degrees Fahrenheit.
 *
 * <p>
 * This class is an immutable value object used to model
 * environmental or device temperature readings within the smart home system.
 * </p>
 *
 * <p>
 * Instances are immutable. Any change in temperature should be represented
 * by creating a new {@code Temperature} instance.
 * </p>
 */
public record Temperature(

        /** Current temperature value in Fahrenheit. */
        double temperature

) {

    /**
     * Creates a temperature initialized to 80°F.
     */
    public Temperature() {
        this(80);
    }
}