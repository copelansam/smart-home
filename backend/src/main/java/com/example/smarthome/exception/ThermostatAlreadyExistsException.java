package com.example.smarthome.exception;

/**
 * Exception thrown when an attempt is made to create a thermostat
 * in a location that already contains one.
 *
 * Enforces the business rule that each location may only have one thermostat.
 */
public class ThermostatAlreadyExistsException extends RuntimeException {
    public ThermostatAlreadyExistsException(String message) {
        super(message);
    }
}
