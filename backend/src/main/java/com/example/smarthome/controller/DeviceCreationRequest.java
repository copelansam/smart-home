package com.example.smarthome.controller;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;

import java.util.Map;

public class DeviceCreationRequest {
    public String name;
    public String location;
    public DeviceType deviceType;
    public Map<String, Object> attributes;

    public DeviceCreationRequest(String name, String location, DeviceType deviceType, Map<String, Object> attributes) {
        this.name = name;
        this.location = location;
        this.deviceType = deviceType;
        this.attributes = attributes;
    }

    public DeviceCreationRequest(String name, String location, DeviceType deviceType){
        this.name = name;
        this.location = location;
        this.deviceType = deviceType;
        attributes = null;
    }

    public void setLocation(String location){
        this.location = location;
    }

}
