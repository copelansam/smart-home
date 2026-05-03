package com.example.smarthome.exception;

/***
 * Exception thrown when device retrieval is attempted, but the device is not present in the database
 *
 * Typically, occurs when the client tries to alter a device that has been deleted
 */
public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
