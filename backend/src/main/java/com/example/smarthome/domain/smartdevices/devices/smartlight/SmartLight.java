package com.example.smarthome.domain.smartdevices.devices.smartlight;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.lightstates.LightOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "smart_light")
public class SmartLight extends SmartDeviceBase {

    private int brightnessPercentage;

    @Embedded
    private RGB color;

    public SmartLight(){}

    public SmartLight(String name, String location){
        super(name, location, DeviceType.LIGHT);
        this.brightnessPercentage = 100;
        setColor(255,255,255);
        this.state = new LightOffState();
        this.isOn = false;
    }

    public SmartLight(String name, String location, int brightnessPercentage, int red, int green, int blue){
        super(name, location, DeviceType.LIGHT);
        setBrightnessPercentage(brightnessPercentage);
        this. color = new RGB(red, green, blue);
        this.state = new LightOffState();
        this.isOn = false;
    }

    public void setBrightnessPercentage(int brightnessPercentage){

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

        this.color = new RGB(R,G,B);
    }

    public RGB getColor(){
        return color;
    }

    public Map<String, Object> getExtraProperties(){

        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("color", this.color.getColor());
        extraProperties.put("brightnessPercentage", this.brightnessPercentage);
        return extraProperties;
    }

    public CallResult execute(String transition){
        return this.state.execute(transition, this);
    }

    @Override
    public void factoryReset(){
        this.isOn = false;
        this.state = new LightOffState();
        this.color = new RGB(255,255,255);
        this.brightnessPercentage = 100;
    }
}
