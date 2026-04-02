package com.example.smarthome.domain.smartdevices.devices;

public class SmartLight extends SmartDeviceBase {

    private int brightnessPercentage;
    private RGB color;
    private boolean isOn;

    public SmartLight(String name, String location, DeviceType deviceType, int brightnessPercentage, int R, int G, int B, boolean isOn){
        super(name, location, deviceType);


    }

    public void setBrightnessPercentage(int brightnessPercentage){
        if (brightnessPercentage < 10 || brightnessPercentage > 100){
            throw new IllegalArgumentException("Brightness must be between 10% and 100%");
        }
    }

    public int getBrightnessPercentage(){
        return brightnessPercentage;
    }

    public void setColor(int R, int G, int B){
        this.color = new RGB(R,G,B);
    }

    public RGB getColor(){
        return color;
    }
}
