package com.example.smarthome.domain.smartdevices.devices.smartfan;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "smart_fan")
public class SmartFan extends SmartDeviceBase {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FanSpeed speed;

    public SmartFan(){}

    public SmartFan(String name, String location){
        super(name, location, DeviceType.FAN);
        this.state = new FanOffState();
        this.speed = FanSpeed.LOW;
    }

    public void setSpeed(FanSpeed speed){
        this.speed = speed;
    }

    public FanSpeed getSpeed(){
        return this.speed;
    }

    public Map<String, Object> getExtraProperties(){

        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("speed", this.speed.getDescription());
        return extraProperties;
    }

    @Override
    public void factoryReset(){
        this.state = new FanOffState();
        this.isOn = false;
        this.speed = FanSpeed.LOW;
    }
}
