package com.example.smarthome.domain.smartdevices.devices.smartfan;

public enum FanSpeed {
    OFF("off"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    String description;

    FanSpeed (String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
