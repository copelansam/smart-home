package com.example.smarthome.domain.smartdevices.devices.smartlight;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.states.lightstates.LightOffState;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "smart_light")
public class SmartLight extends SmartDeviceBase {

    private int brightnessPercentage;

    @Embedded
    private RGB color;
    private boolean isOn;

    public SmartLight(){}

    public SmartLight(String name, String location, DeviceType deviceType, int brightnessPercentage, int R, int G, int B, boolean isOn){
        super(name, location, deviceType);
        this.brightnessPercentage = brightnessPercentage;
        this.color = new RGB(R,G,B);
        this.state = new LightOffState();
    }

    public void setBrightnessPercentage(int brightnessPercentage){

        if(!isOn){ // Invariant: Light color and brightness cannot be changed if the light is off
            throw new IllegalStateException("This light's settings cannot be altered when it is off. Please turn it on and try again.");
        }

        if (brightnessPercentage < 10 || brightnessPercentage > 100){
            throw new IllegalArgumentException("Brightness must be between 10% and 100%");
        }
        else{
            this.brightnessPercentage = brightnessPercentage;
        }
    }

    public int getBrightnessPercentage(){
        return brightnessPercentage;
    }

    public void setColor(int R, int G, int B){

        if (!isOn){ // Invariant: Light color and brightness cannot be changed if the light is off
            throw new IllegalStateException("This light's settings cannot be altered when it is off. Please turn it on and try again.");
        }

        this.color = new RGB(R,G,B);
    }

    public RGB getColor(){
        return color;
    }

    public void setState(IState newState){
        this.state = state.execute().getNewState();
    }
}
