package com.example.smarthome.controller;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;

import java.util.Map;

/***
 * Request object used to create a new smart device
 *
 * Contains basic data required to register a new device in the system
 * including name, location, device type, and configuration metadata
 */
public class DeviceCreationRequest {

    /***
     * Name of the device
     */
    public String name;

    /***
     * Location of the device
     */
    public String location;

    /***
     * Type of device (LIGHT, FAN, DOORLOCK, THERMOSTAT)
     */
    public DeviceType deviceType;

    /***
     * Device specific attributes
     */
    public Map<String, Object> attributes;

    public DeviceCreationRequest() {
    }

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
