package com.example.smarthome.exception;

/**
 * Exception thrown when device retrieval is attempted but no devices exist in the database.
 *
 * This typically occurs when performing operations that require at least one registered device.
 */
public class NoDevicesException extends RuntimeException {
    public NoDevicesException(String message) {
        super(message);
    }
}
