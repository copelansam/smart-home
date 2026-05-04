package com.example.smarthome.domain.smartdevices.devices.smartfan;

/**
 * Represents the different fan speeds supported by the system
 *
 * Each enum constant maps to a human-readable display name, which can be used
 * for UI representation or client responses.
 */
public enum FanSpeed {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    /** Human-readable name for the fan speed. */
    String description;

    FanSpeed (String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
