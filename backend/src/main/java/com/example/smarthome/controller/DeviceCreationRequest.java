package com.example.smarthome.controller;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;

import java.util.Map;

public class DeviceCreationRequest {
    public String name;
    public String location;
    public DeviceType deviceType;
    public Map<String, Object> attributes;

    public void setLocation(String location){
        this.location = location;
    }

}
