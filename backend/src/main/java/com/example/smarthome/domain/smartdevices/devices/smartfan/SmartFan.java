package com.example.smarthome.domain.smartdevices.devices.smartfan;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "smart_fan")
public class SmartFan extends SmartDeviceBase {

    @Enumerated(EnumType.STRING)
    private FanSpeed speed;

    public SmartFan(){}

    public SmartFan(String name, String location, DeviceType deviceType){
        super(name, location, deviceType);
        this.state = new FanOffState();
        this.speed = FanSpeed.OFF;
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

    public void execute(String transition){
        state.execute(transition, this);
    }
}
