package com.example.smarthome.domain.smartdevices;

public class SmartLight extends SmartDeviceBase{

    private int brightnessPercentage;
    private RGB color;
    private boolean isOn;

    public SmartLight(String name, String location, DeviceType deviceType){
        super(name, location, deviceType);
    }
}
