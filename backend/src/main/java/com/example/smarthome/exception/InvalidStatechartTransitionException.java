package com.example.smarthome.exception;

public class InvalidStatechartTransitionException extends RuntimeException {
    public InvalidStatechartTransitionException(String message) {
        super(message);
    }
}
