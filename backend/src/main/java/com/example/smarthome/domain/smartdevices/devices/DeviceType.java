package com.example.smarthome.domain.smartdevices.devices;

public enum DeviceType {
    LIGHT("Light"),
    FAN("Fan"),
    THERMOSTAT("Thermostat"),
    DOORLOCK("Door Lock");

    private String name;

    DeviceType(String name){
        this.name = name;
    }

    public String getDeviceTypeName(){
        return this.name;
    }
}
