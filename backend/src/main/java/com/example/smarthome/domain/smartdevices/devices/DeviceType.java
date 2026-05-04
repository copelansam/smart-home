package com.example.smarthome.domain.smartdevices.devices;

/**
 * Represents the different types of smart devices supported by the system.
 *
 * <p>
 * Each enum constant maps to a human-readable display name, which can be used
 * for UI representation or client responses.
 * </p>
 */
public enum DeviceType {

    /** A smart light device. */
    LIGHT("Light"),

    /** A smart fan device */
    FAN("Fan"),

    /** A smart thermostat device */
    THERMOSTAT("Thermostat"),

    /** A smart door look device */
    DOORLOCK("Door Lock");

    /** Human-readable name for the device type. */
    private String name;

    DeviceType(String name){
        this.name = name;
    }

    public String getDeviceTypeName(){
        return this.name;
    }
}
