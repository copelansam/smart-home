package com.example.smarthome.domain.smartdevices.devices;


import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.repository.StateConverter;
import jakarta.persistence.*;

import java.io.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SmartDeviceBase implements ISmartDevice{

    @Id
    @GeneratedValue
    private UUID uuid;

    private String name;
    private String location;
    protected boolean isOn;

    @Convert(converter = StateConverter.class)
    protected IState state;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    public SmartDeviceBase(){}

    public SmartDeviceBase(String name, String location, DeviceType deviceType){
        this.name = name;
        this.location = location;
        this.deviceType = deviceType;
    }

    public UUID getUuid(){return this.uuid;}
    public String getName(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public DeviceType getDeviceType(){
        return this.deviceType;
    }
    public boolean getIsOn(){
        return this.isOn;
    }
    public String getState(){return this.state.getName();}
    public void setState(IState newState){
        this.state = state.execute().getNewState();
    }
}
