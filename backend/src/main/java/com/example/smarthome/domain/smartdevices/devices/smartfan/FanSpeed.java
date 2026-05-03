package com.example.smarthome.domain.smartdevices.devices.smartfan;

public enum FanSpeed {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    String description;

    FanSpeed (String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
