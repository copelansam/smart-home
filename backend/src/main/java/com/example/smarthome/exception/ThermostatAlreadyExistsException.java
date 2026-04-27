package com.example.smarthome.exception;

public class ThermostatAlreadyExistsException extends RuntimeException {
    public ThermostatAlreadyExistsException(String message) {
        super(message);
    }
}
