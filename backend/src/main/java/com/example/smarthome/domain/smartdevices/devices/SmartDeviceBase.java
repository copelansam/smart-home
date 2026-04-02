package com.example.smarthome.domain.smartdevices.devices;

import java.util.UUID;

public abstract class SmartDeviceBase {
    private UUID uuid;
    private String name;
    private String location;
    private DeviceType deviceType;

    public SmartDeviceBase(String name, String location, DeviceType deviceType){
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.deviceType = deviceType;
    }

    public String getName(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public DeviceType getDeviceType(){
        return this.deviceType;
    }
}
