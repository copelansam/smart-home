package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;

public class SmartDoorLock extends SmartDeviceBase {

    private IState state;

    public SmartDoorLock(String name, String location, DeviceType deviceType){
        super(name, location, deviceType);
    }

    public void setState(IState newState){
        this.state = state.execute().getNewState();
    }
}
